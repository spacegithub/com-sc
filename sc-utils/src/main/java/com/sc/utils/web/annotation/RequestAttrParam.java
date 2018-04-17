package com.sc.utils.web.annotation;

import java.lang.annotation.*;


@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestAttrParam
{
    
    String value() default "" ;
    
    boolean required() default true;
}
