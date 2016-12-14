package com.sc.hessian.test;

import com.sc.hessian.service.Say;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <一句话功能简述>
 * <功能详细描述>
 *
 *
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration(value = "src/main/webapp")
@ContextHierarchy({
        @ContextConfiguration(name = "parent", locations = {"classpath:/applicationContext.xml"}),
        @ContextConfiguration(name = "child", locations = "classpath:/spring-mvc.xml")
})
public class SayTest {

    private AtomicInteger atomicInteger=new AtomicInteger();

    @Value("")
    private String aa;

    @Autowired
    private Say say;

    @Test
    public void sayTest(){
        String aa= say.say("sc");
        System.out.println("===================="+aa);
        Assert.assertEquals("结果一样", "hessian:sc", aa);
    }

    @Test
    public void test() throws MalformedURLException {
        URL url = new URL("http://localhost;http://localhost;");
        System.out.println(url.toString());
    }

    @Test
    public void test1(){
        for (int i=0;i<10;i++){

            int a=  atomicInteger.getAndIncrement();
            System.out.println(a%1);

        }

    }

}
