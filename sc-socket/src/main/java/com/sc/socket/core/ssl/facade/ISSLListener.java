package com.sc.socket.core.ssl.facade;

import com.sc.socket.core.ssl.SslVo;

import java.nio.ByteBuffer;

public interface ISSLListener
{
	/**
	 * 业务层通过这个方法把SSL加密后的数据发出去
	 * @param sslVo
	 */
    public void onWrappedData(SslVo sslVo);

    /**
     * 业务层通过这个方法把SSL解密后的数据进行业务解包
     * @param plainBuffer
     */
    public void onPlainData(ByteBuffer plainBuffer);
}
