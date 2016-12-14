package com.sc.hessian.server;

import com.sc.hessian.header.HessianHeaderLocal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.remoting.caucho.HessianServiceExporter;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Hessianheader
 *
 *
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class HessianHeaderServiceExporter  extends HessianServiceExporter {


    private Logger logger= LoggerFactory.getLogger(HessianHeaderServiceExporter.class);

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try{
            //保存Hessian header信息
            HessianHeaderLocal.getHeaderInfo(request);
            super.handleRequest(request, response);
        }finally {
            //请求处理完成清除审计用户信息
            HessianHeaderLocal.delHeader();
        }


    }

}
