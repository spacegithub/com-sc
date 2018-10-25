package com.sc.test.dubbo;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.sc.test.dubbo.test.TestService;

/**
 * <一句话功能简述>
 * <功能详细描述>
 *
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class DubboClientUtils {
    public static <T> T invokeDubbo(String version, String zookeeperUrl, Class<T> t) {
        ApplicationConfig application = new ApplicationConfig();
        application.setName("dubbo-client");
        RegistryConfig registry = new RegistryConfig();
        registry.setProtocol("zookeeper");
        registry.setAddress(zookeeperUrl);
        ReferenceConfig<T> reference = new ReferenceConfig<T>();
        reference.setApplication(application);
        reference.setRegistry(registry);
        reference.setInterface(t);
        reference.setVersion(version);
        T xt = reference.get();
        return xt;
    }

    public static void main(String[] args) {
        TestService testService = invokeDubbo("1.0.1", "207.246.117.90:2181", TestService.class);
        System.out.println("-->" + testService.helloWorld("stest"));

    }
}
