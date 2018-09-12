package com.sc.utils.classscan;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * 类扫描器

 */
public interface ClassScanner {

    /**
     * 获取指定包名中的所有类
     */
    List<Class<?>> getClassList(String packageName);

    /**
     * 获取指定包名中指定注解的相关类
     */
    List<Class<?>> getClassListByAnnotation(String packageName, Class<? extends Annotation> annotationClass);

    /**
     * 获取指定包名中指定父类或接口的相关类
     */
    List<Class<?>> getClassListBySuper(String packageName, Class<?> superClass);
    
    /**
     * 获取指定包名中指定父类或接口的相关类，不通过注解，HeaderHandle中用到
     */
    List<Class<?>> getClassListBySuperNotAnnotation(String packageName, Class<?> superClass);
    
}
