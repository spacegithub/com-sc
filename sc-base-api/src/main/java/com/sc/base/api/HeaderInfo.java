package com.sc.base.api;

import com.alibaba.fastjson.JSON;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.UUID;

public class HeaderInfo implements Serializable {

    /**
     * 请求主键(UUID)
     * 建立hessian请求之前会被创建,如有其他hessian调用,则将传递该Id.
     */
    private String id;

    /**
     * 用户Id
     */
    private String userId;

    /**
     * 用户的标记，登陆成功后返回，算法需要讨论
     */
    private String userToken;
    /**
     * 应用编号
     */
    private String applicationCode;
    /**
     * 设备编号，唯一区别每一个设备，客户端产生
     */
    private String clientId;

    /**
     * 下载渠道号
     */
    private String sourceId;

    /**
     * 客户端版本号
     */
    private String version;

    /**
     * 服务版本
     */
    private String serviceVersion;

    /**
     * 主渠道号
     */
    private String channel;

    /**
     * 子渠道号
     */
    private String subChannel;

    /**
     * 用户Id
     */
    private String serviceCode;

    /**
     * 编码类型,v3
     */
    private String contentType;

    /**
     * 幂等Id,v3
     */
    private String requestId;

    /**
     * 推广渠道,v3
     */
    private String exSourceId;

    public HeaderInfo() {
        this.id = UUID.randomUUID().toString().replaceAll("-", "");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (StringUtils.isNoneBlank(id)) {
            this.id = id;
        }
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getApplicationCode() {
        return applicationCode;
    }

    public void setApplicationCode(String applicationCode) {
        this.applicationCode = applicationCode;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getSubChannel() {
        return subChannel;
    }

    public void setSubChannel(String subChannel) {
        this.subChannel = subChannel;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getExSourceId() {
        return exSourceId;
    }

    public void setExSourceId(String exSourceId) {
        this.exSourceId = exSourceId;
    }

    public boolean isEmpty() {
        return StringUtils.isBlank(this.getApplicationCode())
                && StringUtils.isBlank(this.getChannel())
                && StringUtils.isBlank(this.getSubChannel())
                && StringUtils.isBlank(this.getVersion())
                && StringUtils.isBlank(this.getServiceVersion())
                && StringUtils.isBlank(this.getSourceId())
                ;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}