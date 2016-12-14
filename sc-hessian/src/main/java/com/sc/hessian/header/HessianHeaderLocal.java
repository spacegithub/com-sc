

package com.sc.hessian.header;

import com.caucho.hessian.client.HessianConnection;
import com.fg.commons.mapper.JsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * Hessian
 * 头部信息缓存
 *
 *
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public abstract class HessianHeaderLocal {

    private static Logger logger= LoggerFactory.getLogger(HessianHeaderLocal.class);

    private static ThreadLocal<HeaderInfo> HEADERLOCAL =new ThreadLocal<>();

    /**
     * 获取当前头信息
     * @return
     */
    public static HeaderInfo getHeaderInfo(){
        return HEADERLOCAL.get();
    }

    /**
     * 获取Header头部信息
     * @param httpServletRequest
     */
    public static void getHeaderInfo(HttpServletRequest httpServletRequest){
        HeaderInfo headerInfo=new HeaderInfo();
        headerInfo.setId(httpServletRequest.getHeader(HeaderCommons.id));
        headerInfo.setApplicationCode(httpServletRequest.getHeader(HeaderCommons.applicationCode));
        headerInfo.setChannel(httpServletRequest.getHeader(HeaderCommons.channel));
        headerInfo.setClientId(httpServletRequest.getHeader(HeaderCommons.clientId));
        headerInfo.setServiceVersion(httpServletRequest.getHeader(HeaderCommons.serviceVersion));
        headerInfo.setSourceId(httpServletRequest.getHeader(HeaderCommons.sourceId));
        headerInfo.setSubChannel(httpServletRequest.getHeader(HeaderCommons.subChannel));
        headerInfo.setUserToken(httpServletRequest.getHeader(HeaderCommons.userToken));
        headerInfo.setVersion(httpServletRequest.getHeader(HeaderCommons.version));
        HEADERLOCAL.set(headerInfo);
        logger.info("[Hesssain Header]:{}", JsonMapper.nonEmptyMapper().toJson(headerInfo));
    }

    /**
     * 在httpheader中新增头部信息
     * @param hessianConnection
     */
    public static void addHeader(HessianConnection hessianConnection){
        HeaderInfo headerInfo=getHeaderInfo();
        if (null!=headerInfo){
            hessianConnection.addHeader(HeaderCommons.id,headerInfo.getId());
            hessianConnection.addHeader(HeaderCommons.applicationCode,headerInfo.getApplicationCode());
            hessianConnection.addHeader(HeaderCommons.channel,headerInfo.getChannel());
            hessianConnection.addHeader(HeaderCommons.clientId,headerInfo.getClientId());
            hessianConnection.addHeader(HeaderCommons.serviceVersion,headerInfo.getServiceVersion());
            hessianConnection.addHeader(HeaderCommons.sourceId,headerInfo.getSourceId());
            hessianConnection.addHeader(HeaderCommons.subChannel,headerInfo.getSubChannel());
            hessianConnection.addHeader(HeaderCommons.userToken,headerInfo.getUserToken());
            hessianConnection.addHeader(HeaderCommons.version,headerInfo.getVersion());
        }else {
            logger.warn("[Hesssain Header]: null header");
        }

    }


    /**
     * 删除头部信息
     */
    public static void delHeader(){
        HEADERLOCAL.remove();
    }
}
