package com.sc.hessian.server;

import com.sc.base.api.header.HessianHeaderLocal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.remoting.caucho.HessianServiceExporter;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class HessianHeaderServiceExporter  extends HessianServiceExporter {


    private Logger logger= LoggerFactory.getLogger(HessianHeaderServiceExporter.class);

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try{
            
            HessianHeaderLocal.getHeaderInfo(request);
            super.handleRequest(request, response);
        }finally {
            
            HessianHeaderLocal.delHeader();
        }


    }

}
