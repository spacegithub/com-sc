package com.sc.utils.test;

import com.sc.utils.spring.PropertiesUtils;

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
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class PropertiesUtilsTest {

    @Autowired
    private PropertiesUtils propertiesUtils;

    @Test
    public void getPropertiesTest(){
        String value= propertiesUtils.getPropertiesValue("${test.a}");
        System.out.println(value);
        Assert.assertEquals("aa", value);
    }

    @Test
    public void getPropertiesTest1(){
        String value= propertiesUtils.getPropertiesValue("test.a");
        System.out.println(value);
        Assert.assertEquals("test.a", value);
    }
}
