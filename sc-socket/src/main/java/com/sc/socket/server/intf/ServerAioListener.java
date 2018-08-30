package com.sc.socket.server.intf;

import com.sc.socket.core.intf.AioListener;

/**
 *
 *
 *
 */
public interface ServerAioListener extends AioListener {

	/**
	 * 建立连接后触发的方法
	 * @param asynchronousSocketChannel
	 * @param tioServer
	 * @return false: 表示拒绝这个连接, true: 表示接受这个连接
	 *
	 *
	 * 2016年12月20日 上午10:10:56
	 *
	 */
	//	void onAfterAccepted(AsynchronousSocketChannel asynchronousSocketChannel, TioServer tioServer);
}
