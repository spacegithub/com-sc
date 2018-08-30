package com.sc.socket.core;

import com.sc.socket.core.intf.Packet;
import com.sc.socket.server.intf.ServerAioListener;
import com.sc.socket.client.intf.ClientAioListener;

/**
 *
 *
 */
public class DefaultAioListener implements ClientAioListener, ServerAioListener {
	/**
	 *
	 * @param channelContext
	 * @param throwable
	 * @param remark
	 * @param isRemove
	 *
	 */
//	@Override
//	public void onAfterClose(ChannelContext channelContext, Throwable throwable, String remark, boolean isRemove) {
//	}

	/**
	 *
	 * @param channelContext
	 * @param isConnected
	 * @param isReconnect
	 *
	 */
	@Override
	public void onAfterConnected(ChannelContext channelContext, boolean isConnected, boolean isReconnect) {
	}

	/**
	 *
	 * @param channelContext
	 * @param packet
	 * @param packetSize
	 *
	 */
	@Override
	public void onAfterDecoded(ChannelContext channelContext, Packet packet, int packetSize) {
	}

	/**
	 *
	 * @param channelContext
	 * @param packet
	 * @param isSentSuccess
	 * @throws Exception
	 *
	 */
	@Override
	public void onAfterSent(ChannelContext channelContext, Packet packet, boolean isSentSuccess) throws Exception {
	}

	@Override
	public void onBeforeClose(ChannelContext channelContext, Throwable throwable, String remark, boolean isRemove) {
	}

	@Override
	public void onAfterHandled(ChannelContext channelContext, Packet packet, long cost) throws Exception {
	}

	@Override
	public void onAfterReceivedBytes(ChannelContext channelContext, int receivedBytes) throws Exception {
		
		
	}
}
