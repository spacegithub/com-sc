package com.sc.hessian.server;

import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.Field;


public class AopTargetUtils {
    
    public static Class<?>[] getInterfaces(Object proxy) throws Exception {
        if(!AopUtils.isAopProxy(proxy)) {
            return new Class[0];
        }
        if(AopUtils.isJdkDynamicProxy(proxy)) {
            return getJdkDynamicProxyTargetInterface(proxy);
        } else { 
            return getCglibProxyTargetInterface(proxy);
        }
    }


    private static Class<?>[] getCglibProxyTargetInterface(Object proxy) throws Exception {
        Field h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
        h.setAccessible(true);
        Object dynamicAdvisedInterceptor = h.get(proxy);

        Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
        advised.setAccessible(true);
        Class<?>[] proxiedInterfaces = ((AdvisedSupport)advised.get(dynamicAdvisedInterceptor)).getProxiedInterfaces();
        return proxiedInterfaces;
    }


    private static Class<?>[] getJdkDynamicProxyTargetInterface(Object proxy) throws Exception {
        Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
        h.setAccessible(true);
        AopProxy aopProxy = (AopProxy) h.get(proxy);

        Field advised = aopProxy.getClass().getDeclaredField("advised");
        advised.setAccessible(true);

        Class<?>[] proxiedInterfaces = ((AdvisedSupport)advised.get(aopProxy)).getProxiedInterfaces();
        return proxiedInterfaces;
    }



    
    public static Object getTarget(Object proxy) throws Exception {
        if(!AopUtils.isAopProxy(proxy)) {
            return proxy;
        }
        if(AopUtils.isJdkDynamicProxy(proxy)) {
            return getJdkDynamicProxyTargetObject(proxy);
        } else { 
            return getCglibProxyTargetObject(proxy);
        }
    }


    private static Object getCglibProxyTargetObject(Object proxy) throws Exception {
        Field h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
        h.setAccessible(true);
        Object dynamicAdvisedInterceptor = h.get(proxy);

        Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
        advised.setAccessible(true);
        ((AdvisedSupport)advised.get(dynamicAdvisedInterceptor)).getProxiedInterfaces();
        Object target = ((AdvisedSupport)advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget();
        if (AopUtils.isCglibProxy(target)){
            target = getCglibProxyTargetObject(target);
        }
        return target;
    }


    private static Object getJdkDynamicProxyTargetObject(Object proxy) throws Exception {
        Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
        h.setAccessible(true);
        AopProxy aopProxy = (AopProxy) h.get(proxy);

        Field advised = aopProxy.getClass().getDeclaredField("advised");
        advised.setAccessible(true);

        Object target = ((AdvisedSupport)advised.get(aopProxy)).getTargetSource().getTarget();
        if (AopUtils.isJdkDynamicProxy(target)){
            target = getJdkDynamicProxyTargetObject(target);
        }
        return target;
    }

}
