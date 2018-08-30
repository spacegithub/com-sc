package com.sc.socket.client.intf;

import com.sc.socket.core.intf.AioHandler;
import com.sc.socket.core.intf.Packet;

/**
 *
 *
 * 2017年4月1日 上午9:14:24
 */
public interface ClientAioHandler extends AioHandler {
	/**
	 * 创建心跳包
	 * @return
	 *
	 */
	Packet heartbeatPacket();
}
