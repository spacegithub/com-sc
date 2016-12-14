package com.sc.hessian.test;

import com.sc.hessian.service.Say;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * <一句话功能简述>
 * <功能详细描述>
 *
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/applicationContext-Client.xml"})
public class SayClientTest {

    @Autowired
    private Say say;

    @Test
    public void sayTest(){
        String aa= say.say("sc");
        System.out.println("===================="+aa);
        Assert.assertEquals("结果一样", "hessian:sc", aa);
    }
}
