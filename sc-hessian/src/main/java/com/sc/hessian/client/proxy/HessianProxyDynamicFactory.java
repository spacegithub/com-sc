package com.sc.hessian.client.proxy;

import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.io.HessianRemoteObject;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;

/**
 * 动态Url,在分布式环境中轮循操作
 *
 *
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
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
