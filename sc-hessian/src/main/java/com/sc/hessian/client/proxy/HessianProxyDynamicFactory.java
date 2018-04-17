package com.sc.hessian.client.proxy;

import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.io.HessianRemoteObject;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;


public class HessianProxyDynamicFactory extends HessianProxyFactory {

    @Override
    public Object create(Class<?> api, String urlName, ClassLoader loader)
            throws MalformedURLException
    {
        if (api == null){
            throw new NullPointerException("api must not be null for HessianProxyFactory.create()");
        }
        InvocationHandler handler = new HessianProxyDynamic(urlName, this, api);
        return Proxy.newProxyInstance(loader, new Class[]{api, HessianRemoteObject.class}, handler);
    }

}
