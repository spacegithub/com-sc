package com.sc.socket.core.intf;

import com.sc.socket.core.ChannelContext;

/**
 *
 * 2017年5月8日 下午1:14:08
 */
public interface PacketListener {
	/**
	 *
	 * @param channelContext
	 * @param packet
	 * @param isSentSuccess
	 * @throws Exception
	 *
	 */
	void onAfterSent(ChannelContext channelContext, Packet packet, boolean isSentSuccess) throws Exception;

}
