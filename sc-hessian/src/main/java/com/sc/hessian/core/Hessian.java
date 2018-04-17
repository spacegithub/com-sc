package com.sc.hessian.core;


public @interface Hessian {
    String description() default "";

    boolean overloadEnabled() default false; 

    String uri(); 

    String context(); 
}
