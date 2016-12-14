package com.sc.hessian.core;

/**
 *Hessian注解
 *
 *
 */
public @interface Hessian {
    String description() default "";

    boolean overloadEnabled() default false; // 是否支持方法重载

    String uri(); // 用于服务端bean名称，也是客户端访问链接的后半部分 配置。如: /talentService

    String context(); // 客户端访问链接前半部分配置 如 http://localhost:8004/remote
}
