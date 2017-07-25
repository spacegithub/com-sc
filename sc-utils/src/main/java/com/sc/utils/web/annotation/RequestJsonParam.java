package com.sc.utils.web.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestJsonParam
{
    /**
     * 用于绑定的请求参数名字
     */
    String value() default "" ;
    /**
     * 是否必须，默认是
     */
    boolean required() default true;

    /**
     * 是否是当前json对象
     * 默认false,表示当前josn对象中的某一个值
     * 若是true,则忽略value
     * @return
     */
    boolean current() default false;
}
