package com.sc.hessian.filter;

import com.sc.base.api.header.HessianHeaderLocal;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;


public class HessianHeaderServletFilter implements Filter {





    @Override
    public void init(FilterConfig filterConfig) throws ServletException {


    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HessianHeaderLocal.getHeaderInfo((HttpServletRequest)request);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        HessianHeaderLocal.delHeader();
    }
}
