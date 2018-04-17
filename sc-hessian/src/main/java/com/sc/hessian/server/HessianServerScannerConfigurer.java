package com.sc.hessian.server;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.remoting.caucho.HessianExporter;
import org.springframework.remoting.caucho.HessianServiceExporter;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.springframework.util.Assert.notNull;



public class HessianServerScannerConfigurer implements
        BeanDefinitionRegistryPostProcessor, InitializingBean,
        ApplicationContextAware, BeanNameAware {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private String beanName;
    private String basePackage;
    private boolean includeAnnotationConfig = true;

    private ApplicationContext applicationContext;

    
    private Class<?> markerInterface;

    
    private Class<? extends Annotation> annotationClass;
    
    private Class<? extends HessianExporter> hessianExporter;
    
    private Map<String, String> implClassContextName = new HashMap<String, String>();

    private BeanNameGenerator nameGenerator = new AnnotationBeanNameGenerator() {
        @Override
        protected String buildDefaultBeanName(BeanDefinition definition) {
            AnnotationMetadata metadata = ((ScannedGenericBeanDefinition) definition).getMetadata();
            Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(annotationClass.getName());
            String uri = (String) annotationAttributes.get("uri");
            return uri;
        }
    };

    @Override
    public void postProcessBeanFactory(
            ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        ApplicationContext rootApplicationContext=applicationContext.getParent();
        this.applicationContext = (null==rootApplicationContext?applicationContext:rootApplicationContext);

    }

    public void setHessianExporter(Class hessianExporter) {
        this.hessianExporter = hessianExporter;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        notNull(this.basePackage, "Property 'basePackage' is required " + beanName);
        notNull(this.annotationClass, "Property 'annotationClass' is required " + beanName);
        if (null==this.hessianExporter) {
            this.hessianExporter = HessianServiceExporter.class;
        }
    }

    @Override
    public void postProcessBeanDefinitionRegistry(
            BeanDefinitionRegistry registry) throws BeansException {
        HessianClassPathScanner scan = new HessianClassPathScanner(registry);
        scan.setResourceLoader(this.applicationContext);
        scan.setBeanNameGenerator(this.nameGenerator);
        
        scan.setIncludeAnnotationConfig(this.includeAnnotationConfig);
        scan.registerFilters();

        String[] basePackages = StringUtils.tokenizeToStringArray(this.basePackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
        scan.scan(basePackages);
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public void setIncludeAnnotationConfig(boolean includeAnnotationConfig) {
        this.includeAnnotationConfig = includeAnnotationConfig;
    }

    public Class<?> getMarkerInterface() {
        return markerInterface;
    }

    public void setMarkerInterface(Class<?> markerInterface) {
        this.markerInterface = markerInterface;
    }

    public Class<? extends Annotation> getAnnotationClass() {
        return annotationClass;
    }

    public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }

    public BeanNameGenerator getNameGenerator() {
        return nameGenerator;
    }

    public void setNameGenerator(BeanNameGenerator nameGenerator) {
        this.nameGenerator = nameGenerator;
    }

    private Class<?>[] getActualInterfaces(Object obj) {
        try {
            return AopTargetUtils.getInterfaces(obj);
        } catch (Exception e) {
            logger.error(obj + " find Actual Interface error", e);
        }
        return new Class[0];
    }


    private class HessianClassPathScanner extends ClassPathBeanDefinitionScanner {

        public HessianClassPathScanner(BeanDefinitionRegistry registry) {
            super(registry, false);
        }

        @Override
        public Set<BeanDefinitionHolder> doScan(String... basePackages) {
            Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
            if (beanDefinitions.isEmpty()) {
                logger.warn("No hessian was found in '"
                        + Arrays.toString(basePackages)
                        + "' package. Please check your configuration.");
            } else {
                for (BeanDefinitionHolder holder : beanDefinitions) {
                    GenericBeanDefinition definition = (GenericBeanDefinition) holder.getBeanDefinition();

                    if (logger.isDebugEnabled()) {
                        logger.debug("Creating HessianServiceExporter with name '"
                                + holder.getBeanName() + "' and '"
                                + definition.getBeanClassName()
                                + "' serviceInterface");
                    }

                    
                    
                    definition.getPropertyValues().add("serviceInterface", definition.getBeanClassName());
                    String beanNameRef = implClassContextName.get(definition.getBeanClassName());
                    definition.getPropertyValues().add("service", new RuntimeBeanReference(beanNameRef));
                    definition.setBeanClass(hessianExporter);
                }
            }
            return beanDefinitions;

        }

        @Override
        protected boolean isCandidateComponent(
                AnnotatedBeanDefinition beanDefinition) {
            return (beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent());
        }

        
        @Override
        protected boolean checkCandidate(String beanName,
                                         BeanDefinition beanDefinition) throws IllegalStateException {

            String implBeanName = getImplBeanName(beanDefinition);
            if (!StringUtils.isEmpty(implBeanName) && super.checkCandidate(beanName, beanDefinition)) {
                return true;
            } else {
                logger.warn("Skipping HessianServiceExporter with name '" + beanName
                        + "' and '" + beanDefinition.getBeanClassName()
                        + "' serviceInterface "
                        + ". Bean already defined with the same name!");
                return false;
            }
        }

        
        private String getImplBeanName(BeanDefinition beanDefinition) {
            try {
                if (implClassContextName.containsKey(beanDefinition.getBeanClassName())) {
                    return implClassContextName.get(beanDefinition.getBeanClassName());
                }
                Class clazz = Class.forName(beanDefinition.getBeanClassName());
                String[] beanNames = applicationContext.getBeanNamesForType(clazz);
                if (ArrayUtils.isNotEmpty(beanNames)) {
                    String beanImpl = beanNames[0];
                    implClassContextName.put(beanDefinition.getBeanClassName(), beanImpl);
                    return beanImpl;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                logger.warn("Skipping HessianServiceExporter with name '" + beanName
                        + "' and '" + beanDefinition.getBeanClassName()
                        + "' serviceInterface "
                        + ". Bean not defined with the same name!");
            }
            return null;
        }

        public void registerFilters() {
            boolean acceptAllInterfaces = true;

            
            if (HessianServerScannerConfigurer.this.annotationClass != null) {
                addIncludeFilter(new AnnotationTypeFilter(HessianServerScannerConfigurer.this.annotationClass));
                acceptAllInterfaces = false;
            }

            
            if (HessianServerScannerConfigurer.this.markerInterface != null) {
                addIncludeFilter(new AssignableTypeFilter(HessianServerScannerConfigurer.this.markerInterface) {
                    @Override
                    protected boolean matchClassName(String className) {
                        return false;
                    }
                });
                acceptAllInterfaces = false;
            }

            if (acceptAllInterfaces) {
                
                addIncludeFilter(new TypeFilter() {
                    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
                        return true;
                    }
                });
            }

            
            addExcludeFilter(new TypeFilter() {
                public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
                    String className = metadataReader.getClassMetadata().getClassName();
                    return className.endsWith("package-info");
                }
            });
        }
    }
}
