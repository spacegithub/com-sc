package com.sc.hessian.filter;

import com.sc.hessian.header.HessianHeaderLocal;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * 基于ServletFilter
 * 如果是hessian协议则做header拦截
 *
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class HessianHeaderServletFilter implements Filter {

//    private static String IGNORE="ignore";
//
//    private String[] ignore;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
//        String ignoreParameter=filterConfig.getInitParameter(IGNORE);
//        ignore=StringUtils.split(ignoreParameter,"");
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
