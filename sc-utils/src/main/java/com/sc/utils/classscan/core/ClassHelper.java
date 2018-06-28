package com.sc.utils.classscan.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * 根据条件获取相关类

 */
public class ClassHelper {

    private static final Logger logger = LoggerFactory.getLogger(ClassHelper.class);

    /**
     * 获取基础包名
     */
//    private static final String basePackage = ConfigHelper.getString("smart.framework.app.base_package");
    private static String basePackageStatic;
    
    private String basePackage;
    
    static{
		WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
		try {
			ClassHelper classHelper = (ClassHelper) wac.getBean("classHelper");
			basePackageStatic = classHelper.getBasePackage();
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
    }

    /**
     * 获取 ClassScanner
     */
    private static final ClassScanner classScanner = InstanceFactory.getClassScanner();

    /**
     * 获取基础包名中的所有类
     */
    public static List<Class<?>> getClassList() {
        return classScanner.getClassList(basePackageStatic);
    }

    /**
     * 获取基础包名中指定父类或接口的相关类
     */
    public static List<Class<?>> getClassListBySuper(Class<?> superClass) {
        return classScanner.getClassListBySuper(basePackageStatic, superClass);
    }

    /**
     * 获取基础包名中指定注解的相关类
     */
    public static List<Class<?>> getClassListByAnnotation(Class<? extends Annotation> annotationClass) {
        return classScanner.getClassListByAnnotation(basePackageStatic, annotationClass);
    }

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	public String getBasePackage() {
		return basePackage;
	}
	
}
