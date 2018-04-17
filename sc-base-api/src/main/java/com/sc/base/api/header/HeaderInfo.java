package com.sc.base.api.header;


import com.alibaba.fastjson.JSON;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.UUID;


public class HeaderInfo implements Serializable {

    
    private String id;

    
    private Long userId;

    
    private String userToken;
    
    private String applicationCode;
    
    private String clientId;

    
    private String sourceId;

    
    private String version;

    
    private String serviceVersion;

    
    private String channel;

    
    private String subChannel;

    public HeaderInfo() {
        this.id = UUID.randomUUID().toString().replaceAll("-", "");
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (StringUtils.isNoneBlank(id)) {
            this.id = id;
        }
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

    @Override
    public String toString() {
        return JSON.toJSONString(this);
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
}
