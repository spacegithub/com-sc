package com.sc.test.dubbo.test;

import com.alibaba.dubbo.common.utils.LogUtil;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * test provider
 */

public class TestDubboProvider {
    private static AtomicBoolean running = new AtomicBoolean(false);

    public static void main(String[] args) {
        while (true) {
            if (!running.get()) {
                // 服务实现
                TestService testService = new TestServiceImpl();
                // 当前应用配置
                ApplicationConfig application = new ApplicationConfig();
                application.setName("dubbo-api-test");
                // 连接注册中心配置
                RegistryConfig registry = new RegistryConfig();
                registry.setProtocol("zookeeper");
                registry.setAddress("207.246.117.90:2181");
                // 服务提供者协议配置
                ProtocolConfig protocol = new ProtocolConfig();
                protocol.setName("dubbo");
                protocol.setPort(12345);
                protocol.setThreads(100);
                // 注意：ServiceConfig为重对象，内部封装了与注册中心的连接，以及开启服务端口
                // 服务提供者暴露服务配置
                // 此实例很重，封装了与注册中心的连接，请自行缓存，否则可能造成内存和连接泄漏
                ServiceConfig<TestService> service = new ServiceConfig<TestService>();
                service.setApplication(application);
                // 多个注册中心可以用setRegistries()
                service.setRegistry(registry);
                // 多个协议可以用setProtocols()
                service.setProtocol(protocol);
                service.setInterface(TestService.class);
                service.setRef(testService);
                service.setVersion("1.0.1");
                // 暴露及注册服务
                service.export();
                running.set(true);
            }
            LogUtil.printList(new ArrayList<Object>());
        }
    }

}
