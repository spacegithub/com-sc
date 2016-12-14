package com.sc.web.annotation;

import java.lang.annotation.*;


@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestAttrParam
{
    /**
     * 用于绑定的请求参数名字
     */
    String value() default "" ;
    /**
     * 是否必须，默认是
     */
    boolean required() default true;
}
