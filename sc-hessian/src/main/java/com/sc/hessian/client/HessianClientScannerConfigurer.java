package com.sc.hessian.client;

import com.caucho.hessian.client.HessianProxyFactory;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.remoting.caucho.HessianProxyFactoryBean;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;

import static org.springframework.util.Assert.notNull;

/**
 * hessian 接口客户端自动扫描注入
 *
 * <bean class="com.xxx.HessianClientScannerConfigurer">
 *  <property name="basePackage" value="com.**.remote"></property>
 *  <property name="annotationClass" value="Hessian"></property>
 *  <property name="locations">
 *      <list>
 *          <value>classpath:hessianurl.properties</value>
 *      </list>
 *  </property>
 * </bean>
 *
 *
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class HessianClientScannerConfigurer implements
        BeanDefinitionRegistryPostProcessor, InitializingBean,
        ApplicationContextAware, BeanNameAware {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private String beanName;
    private String basePackage;

    private Resource[] locations;

    private boolean includeAnnotationConfig = true;

    private ApplicationContext applicationContext;

    // 实现了该接口
    private Class<?> markerInterface;
    // 配置了该注解
    private Class<? extends Annotation> annotationClass;
    //配置bean代理工厂
    private HessianProxyFactory proxyFactory;

    private BeanNameGenerator nameGenerator;

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
        this.applicationContext = applicationContext;
    }

    public void setProxyFactory(HessianProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        notNull(this.locations, "Property 'locations' is required " + beanName);
        notNull(this.basePackage, "Property 'basePackage' is required " + beanName);

        Properties props = new Properties();
        loadProperties(props);
        for (Map.Entry<Object, Object> resource : props.entrySet()) {
            System.setProperty((String)resource.getKey(), (String)resource.getValue());
        }
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        HessianClassPathScanner scan = new HessianClassPathScanner(registry);
        scan.setResourceLoader(this.applicationContext);
        scan.setBeanNameGenerator(this.nameGenerator);
        // 引入注解配置
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

    public void setLocations(Resource... locations) {
        this.locations = locations;
    }

    protected void loadProperties(Properties props) throws IOException {
        if (this.locations != null) {
            for (Resource location : this.locations) {
                if (logger.isInfoEnabled()) {
                    logger.info("Loading properties file from " + location);
                }
                try {
                    PropertiesLoaderUtils.fillProperties(props, location);
                }catch (IOException ex) {
                    if (logger.isWarnEnabled()) {
                        logger.warn("Could not load properties from " + location + ": " + ex.getMessage());
                    }
                }
            }
        }
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
                        logger.debug("Creating HessianFactoryBean with name '"
                                + holder.getBeanName() + "' and '"
                                + definition.getBeanClassName()
                                + "' hessianInterface");
                    }

                    AnnotationMetadata metadata = ((ScannedGenericBeanDefinition)definition).getMetadata();
                    Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(annotationClass.getName());
                    String context = (String)annotationAttributes.get("context");
                    String uri = (String)annotationAttributes.get("uri");
                    Boolean overloadEnabled = MapUtils.getBoolean(annotationAttributes, "overloadEnabled", false);

                    definition.getPropertyValues().add("serviceUrl", getRemoteUrl(context,uri));
                    definition.getPropertyValues().add("serviceInterface", definition.getBeanClassName());
//                  新增overloadEnabled属性，并把它的值设置为true，默认是false，则Hessian就能支持方法的重载了
                    definition.getPropertyValues().add("overloadEnabled", overloadEnabled);
                    //如果定义了proxyFactory则,注入
                    if(null!=proxyFactory){
                        definition.getPropertyValues().add("proxyFactory", proxyFactory);
                    }

                    definition.setBeanClass(HessianProxyFactoryBean.class);


                    // the mapper interface is the original class of the bean
                    // but, the actual class of the bean is HessianFactoryBean
//                  definition.getPropertyValues().add("hessianInterface", definition.getBeanClassName());
//                  definition.setBeanClass(HessianFactoryBean.class);
                }
            }
            return beanDefinitions;

        }

        /**
         * 如果是多个server做负载,则有多条server地址
         * @param context
         * @param uri
         * @return
         */
        private  String getRemoteUrl(String context,String uri){
            String urlProperty= System.getProperty(context,"");
            String[] urls = StringUtils.tokenizeToStringArray(urlProperty, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
            List<String> urlList=new ArrayList();
            for(String url:urls){
                urlList.add(url+uri);
            }
            return org.apache.commons.lang3.StringUtils.join(urlList,";");
        }

        @Override
        protected boolean isCandidateComponent(
                AnnotatedBeanDefinition beanDefinition) {
            return (beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected boolean checkCandidate(String beanName,
                                         BeanDefinition beanDefinition) throws IllegalStateException {
            if (super.checkCandidate(beanName, beanDefinition)) {
                return true;
            } else {
                logger.warn("Skipping HessianFactoryBean with name '" + beanName
                        + "' and '" + beanDefinition.getBeanClassName()
                        + "' hessianInterface"
                        + ". Bean already defined with the same name!");
                return false;
            }
        }

        public void registerFilters() {
            boolean acceptAllInterfaces = true;

            // if specified, use the given annotation and / or marker interface
            if (HessianClientScannerConfigurer.this.annotationClass != null) {
                addIncludeFilter(new AnnotationTypeFilter(HessianClientScannerConfigurer.this.annotationClass));
                acceptAllInterfaces = false;
            }

            // override AssignableTypeFilter to ignore matches on the actual marker interface
            if (HessianClientScannerConfigurer.this.markerInterface != null) {
                addIncludeFilter(new AssignableTypeFilter(HessianClientScannerConfigurer.this.markerInterface) {
                    @Override
                    protected boolean matchClassName(String className) {
                        return false;
                    }
                });
                acceptAllInterfaces = false;
            }

            if (acceptAllInterfaces) {
                // default include filter that accepts all classes
                addIncludeFilter(new TypeFilter() {
                    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
                        return true;
                    }
                });
            }

            // exclude package-info.java
            addExcludeFilter(new TypeFilter() {
                public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
                    String className = metadataReader.getClassMetadata().getClassName();
                    return className.endsWith("package-info");
                }
            });
        }
    }
}
