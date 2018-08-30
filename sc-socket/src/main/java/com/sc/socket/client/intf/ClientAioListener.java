package com.sc.socket.client.intf;

import com.sc.socket.core.intf.AioListener;

/**
 *
 *
 * 2017年4月1日 上午9:15:04
 */
public interface ClientAioListener extends AioListener {

	/**
	 * 重连后触发本方法
	 * @param channelContext
	 * @param isConnected true: 表示重连成功，false: 表示重连失败
	 * @return
	 *
	 *
	 *
	 */
	//	void onAfterReconnected(ChannelContext channelContext, boolean isConnected) throws Exception;

	//	/**
	//	 * 连接失败后触发的方法
	//	 * @param channelContext
	//	 * @param isReconnect 是否是重连
	//	 * @param throwable 有可能是null
	//	 *

	//	 *
	//	 */
	//	void onFailConnected(ChannelContext channelContext, boolean isReconnect, java.lang.Throwable throwable);
}
