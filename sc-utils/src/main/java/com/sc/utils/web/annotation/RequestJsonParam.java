package com.sc.utils.web.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestJsonParam
{
    
    String value() default "" ;
    
    boolean required() default true;

    
    boolean current() default false;
}
