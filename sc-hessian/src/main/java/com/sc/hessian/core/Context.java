package com.sc.hessian.core;

/**
 * 客服端url链接前半部分配置
 *
 * 配置文件中增加：api.remote.url=http://localhost:8080/hessian

 */
public enum Context {
    API("api.remote.url");

    private String remoteUrlConfigKey;

    private Context(String remoteUrlConfigKey) {
        this.remoteUrlConfigKey = remoteUrlConfigKey;
    }

    public String getRemoteUrl() {
        return System.getProperty(remoteUrlConfigKey, "http://127.0.0.1/hessian");
    }
}
