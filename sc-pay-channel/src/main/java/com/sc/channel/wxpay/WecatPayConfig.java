package com.sc.channel.wxpay;

import com.google.common.io.ByteSource;

import java.io.IOException;
import java.io.InputStream;

public class WecatPayConfig {
    private String apiKey;

    private String appId;
    private String mchId;
    private String notifyUrl;
    private ByteSource KeyByteSource;

    public WecatPayConfig(String apiKey, String appId, String mchId,  String notifyUrl, byte[] keyByteArray) {
        this.apiKey = apiKey;

        this.appId = appId;
        this.mchId = mchId;
        this.notifyUrl = notifyUrl;
        this.KeyByteSource = ByteSource.wrap(keyByteArray);
    }

    public String getApiKey() {
        return apiKey;
    }



    public String getAppId() {
        return appId;
    }

    public String getMchId() {
        return mchId;
    }



    public String getNotifyUrl() {
        return notifyUrl;
    }


    public ByteSource getKeyByteSource() {
        return KeyByteSource;
    }

    public void setKeyByteSource(ByteSource keyByteSource) {
        KeyByteSource = keyByteSource;
    }

    public InputStream getKeyInput() throws IOException {
        return KeyByteSource.openStream();
    }
}
