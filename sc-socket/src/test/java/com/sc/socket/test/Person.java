package com.sc.socket.test;

import java.io.Serializable;

/**
 * <一句话功能简述>
 * <功能详细描述>
 *
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class Person implements Serializable {
    private String name;
    private Integer age;

    public Person(String name, Integer age) {

        this.name = name;
        this.age = age;

    }

    public Person() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "用户名称:" + name + "==>用户年龄:" + age;
    }
}
