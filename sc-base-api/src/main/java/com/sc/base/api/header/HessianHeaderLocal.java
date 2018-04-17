package com.sc.base.api.header;

import com.alibaba.fastjson.JSONObject;
import com.caucho.hessian.client.HessianConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

public abstract class HessianHeaderLocal {

    private static Logger logger = LoggerFactory.getLogger(HessianHeaderLocal.class);

    private static ThreadLocal<HeaderInfo> HEADERLOCAL = new ThreadLocal<>();

    
    public static HeaderInfo getHeaderInfo() {
        return HEADERLOCAL.get();
    }

    
    public static HeaderInfo getHeaderInfo(HttpServletRequest httpServletRequest) {
        HeaderInfo headerInfo = new HeaderInfo();

        
        String header = httpServletRequest.getHeader("header");
        if (header != null && !header.isEmpty()) {
            try {
                headerInfo = (HeaderInfo) JSONObject.parseObject(header, HeaderInfo.class);
                if (headerInfo.getUserToken() == null || headerInfo.getUserToken().isEmpty()) {
                    headerInfo.setUserToken(httpServletRequest.getHeader(HeaderCommons.userToken));
                }
            } catch (Exception ex) {
                logger.error("[Hesssain Header err]", ex);
            }
        } else { 
            headerInfo.setApplicationCode(httpServletRequest.getHeader(HeaderCommons.applicationCode));
            headerInfo.setChannel(httpServletRequest.getHeader(HeaderCommons.channel));
            headerInfo.setClientId(httpServletRequest.getHeader(HeaderCommons.clientId));
            headerInfo.setServiceVersion(httpServletRequest.getHeader(HeaderCommons.serviceVersion));
            headerInfo.setSourceId(httpServletRequest.getHeader(HeaderCommons.sourceId));
            headerInfo.setSubChannel(httpServletRequest.getHeader(HeaderCommons.subChannel));
            headerInfo.setUserToken(httpServletRequest.getHeader(HeaderCommons.userToken));
            headerInfo.setVersion(httpServletRequest.getHeader(HeaderCommons.version));
        }
        
        headerInfo.setId(httpServletRequest.getHeader(HeaderCommons.id));
        HEADERLOCAL.set(headerInfo);
        if (!headerInfo.isEmpty()) {

        }
        return headerInfo;
    }

    
    public static void addHeader(HessianConnection hessianConnection) {
        HeaderInfo headerInfo = getHeaderInfo();
        if (null != headerInfo) {
            hessianConnection.addHeader(HeaderCommons.id, headerInfo.getId());
            hessianConnection.addHeader(HeaderCommons.applicationCode, headerInfo.getApplicationCode());
            hessianConnection.addHeader(HeaderCommons.channel, headerInfo.getChannel());
            hessianConnection.addHeader(HeaderCommons.clientId, headerInfo.getClientId());
            hessianConnection.addHeader(HeaderCommons.serviceVersion, headerInfo.getServiceVersion());
            hessianConnection.addHeader(HeaderCommons.sourceId, headerInfo.getSourceId());
            hessianConnection.addHeader(HeaderCommons.subChannel, headerInfo.getSubChannel());
            hessianConnection.addHeader(HeaderCommons.userToken, headerInfo.getUserToken());
            hessianConnection.addHeader(HeaderCommons.version, headerInfo.getVersion());
        } else {
            logger.warn("[Hesssain Header]: null header");
        }

    }

    
    public static void delHeader() {
        HEADERLOCAL.remove();
    }
}
