package com.sc.socket.server;

import com.sc.socket.core.ChannelContext;
import com.sc.socket.core.GroupContext;
import com.sc.socket.core.Node;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;

/**
 *
 *
 *
 */
public class ServerChannelContext extends ChannelContext {

	/**
	 * @param groupContext
	 * @param asynchronousSocketChannel
	 *
	 *
	 * 2016年12月6日 下午12:17:59
	 *
	 */
	public ServerChannelContext(GroupContext groupContext, AsynchronousSocketChannel asynchronousSocketChannel) {
		super(groupContext, asynchronousSocketChannel);
	}
	
	/**
	 * 创建一个虚拟ChannelContext，主要用来模拟一些操作，真实场景中用得少
	 * @param groupContext
	 */
	public ServerChannelContext(GroupContext groupContext) {
		super(groupContext);
	}

	/**
	 * @see ChannelContext#createClientNode(AsynchronousSocketChannel)
	 *
	 * @param asynchronousSocketChannel
	 * @return
	 * @throws IOException
	 *
	 * 2016年12月6日 下午12:18:08
	 *
	 */
	@Override
	public Node createClientNode(AsynchronousSocketChannel asynchronousSocketChannel) throws IOException {
		InetSocketAddress inetSocketAddress = (InetSocketAddress) asynchronousSocketChannel.getRemoteAddress();
		Node clientNode = new Node(inetSocketAddress.getHostString(), inetSocketAddress.getPort());
		return clientNode;
	}

	/** 
	 * @return
	 *
	 */
	@Override
	public boolean isServer() {
		return true;
	}

}
