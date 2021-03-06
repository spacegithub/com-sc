package com.sc.utils.web.wapper;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class RequestBodyThreadLocalInterceptor extends HandlerInterceptorAdapter{

    public static ThreadLocal<String>  RequestBodyThreadLocal=new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        return super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception
    {
        
        RequestBodyThreadLocal.remove();
        super.afterCompletion(request, response, handler, ex);
    }

}
