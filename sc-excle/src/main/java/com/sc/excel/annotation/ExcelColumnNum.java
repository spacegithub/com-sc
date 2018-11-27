package com.sc.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ExcelColumnNum {

    /**
     * col num
     * @return
     */
    int value();

    /**
     *
     * Default @see com.sc.excel.util.TypeUtil
     * if default is not  meet you can set format
     *
     * @return
     */
    String format() default "";
}
