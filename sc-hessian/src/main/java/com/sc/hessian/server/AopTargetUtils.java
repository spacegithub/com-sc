package com.sc.hessian.server;

import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.Field;

/**
 * Aop工具类
 *
 *
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class AopTargetUtils {
    /**
     * 获取 被代理的接口类
     * @param proxy 代理对象
     * @return
     * @throws Exception
     */
    public static Class<?>[] getInterfaces(Object proxy) throws Exception {
        if(!AopUtils.isAopProxy(proxy)) {
            return new Class[0];//不是代理对象
        }
        if(AopUtils.isJdkDynamicProxy(proxy)) {
            return getJdkDynamicProxyTargetInterface(proxy);
        } else { //cglib
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



    /**
     * 获取 目标对象
     * @param proxy 代理对象
     * @return
     * @throws Exception
     */
    public static Object getTarget(Object proxy) throws Exception {
        if(!AopUtils.isAopProxy(proxy)) {
            return proxy;//不是代理对象
        }
        if(AopUtils.isJdkDynamicProxy(proxy)) {
            return getJdkDynamicProxyTargetObject(proxy);
        } else { //cglib
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
