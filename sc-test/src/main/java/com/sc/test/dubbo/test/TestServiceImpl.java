package com.sc.test.dubbo.test;

/**
 * <一句话功能简述>
 * <功能详细描述>
 *
 * @auth:qiss
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class TestServiceImpl implements TestService {
    @Override
    public String helloWorld(String str) {
        System.out.println("-->" + str);
        return "hello dubbo and" + str;
    }
}
