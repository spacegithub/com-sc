package com.sc.test.hessian;

import com.alibaba.fastjson.JSON;
import com.caucho.hessian.client.HessianProxyFactory;
import com.sc.test.URLExec;

import java.util.UUID;


public class ComHessianUtils {
    /**
     * 带header头的hessian请求
     */
    public static <T, K> void postHessianWithHeader(HeaderInfo headerInfo, URLExec URLExec, K request, Class<T> iservice) {
        HessianProxyFactory factory = new HessianProxyFactory();
        System.out.println("请求头:" + headerInfo);
        System.out.println("请求体:" + JSON.toJSONString(request));
        Object service = null;
        try {
            service = factory.create(iservice, URLExec.toString());//创建IService接口的实例对象
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("响应体:" + JSON.toJSONString(Reflect.on(service).call("call", headerInfo, request).get()));
    }

    /**
     * 直接hessian请求
     */
    public static <T, K> void postHessian(URLExec URLExec, K request, Class<T> iservice) {
        postHessian(URLExec, request, iservice, "838558c7-7d22-4c99-b026-6163e38fc7ec");
    }

    /**
     * 直接hessian请求
     */
    public static <T, K> void postHessian(URLExec URLExec, K request, Class<T> iservice, String userToken) {
        HeaderInfo headerInfo = new HeaderInfo();
        headerInfo.setApplicationCode("SVR-Order");
        headerInfo.setClientId(UUID.randomUUID().toString());
        headerInfo.setSourceId("APP");
        headerInfo.setVersion("1");
        headerInfo.setServiceVersion("1");
        headerInfo.setSubChannel("SVR");
        headerInfo.setChannel("svr");
        headerInfo.setUserToken(userToken);
        postHessianWithHeader(headerInfo, URLExec, request, iservice);
    }


}
