package com.sc.utils.classscan.core.impl;

import com.sc.utils.classscan.core.ClassScanner;
import com.sc.utils.classscan.core.impl.support.AnnotationClassTemplate;
import com.sc.utils.classscan.core.impl.support.ClassTemplate;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;


/**
 * 默认类扫描器
 *

 */
public class DefaultClassScanner implements ClassScanner {

    @Override
    public List<Class<?>> getClassList(String packageName) {
        return new ClassTemplate(packageName) {
            @Override
            public boolean checkAddClass(Class<?> cls) {
                String className = cls.getName();
                String pkgName = className.substring(0, className.lastIndexOf("."));
                return pkgName.startsWith(packageName);
            }
        }.getClassList();
    }

    @Override
    public List<Class<?>> getClassListByAnnotation(String packageName, Class<? extends Annotation> annotationClass) {
        return new AnnotationClassTemplate(packageName, annotationClass) {
            @Override
            public boolean checkAddClass(Class<?> cls) {
                return cls.isAnnotationPresent(annotationClass);
            }
        }.getClassList();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public List<Class<?>> getClassListBySuper(String packageName, Class<?> superClass) {
    	List result = new ArrayList();
    	List<Class<?>> list = getClassList(packageName);
    	for(Class<?> cls : list){
    		if(superClass.isAssignableFrom(cls) && !superClass.equals(cls)){
    			result.add(cls);
    		}
    	}
    	return result;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<Class<?>> getClassListBySuperNotAnnotation(String packageName, Class<?> superClass) {
    	List result = new ArrayList();
    	List<Class<?>> list = getClassListNotAnnotation(packageName);
    	for(Class<?> cls : list){
    		if(superClass.isAssignableFrom(cls) && !superClass.equals(cls)){
    			result.add(cls);
    		}
    	}
    	return result;
    }
    
    public List<Class<?>> getClassListNotAnnotation(String packageName) {
        return new ClassTemplate(packageName) {
            @Override
            public boolean checkAddClass(Class<?> cls) {
                String className = cls.getName();
                String pkgName = className.substring(0, className.lastIndexOf("."));
                return pkgName.startsWith(packageName);
            }
        }.getClassList();
    }
    
}
