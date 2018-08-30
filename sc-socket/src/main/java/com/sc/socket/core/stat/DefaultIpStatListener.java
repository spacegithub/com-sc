/**
 * 
 */
package com.sc.socket.core.stat;

import com.sc.socket.core.ChannelContext;
import com.sc.socket.core.intf.Packet;
import com.sc.socket.core.GroupContext;

/**
 *
 *
 */
public class DefaultIpStatListener implements IpStatListener {

	public static final DefaultIpStatListener me = new DefaultIpStatListener();

	/**
	 * 
	 */
	private DefaultIpStatListener() {
	}

	/* (non-Javadoc)
	 * @see org.IpStatListener#onExpired(org.GroupContext, org.IpStat)
	 */
	@Override
	public void onExpired(GroupContext groupContext, IpStat ipStat) {
	}

	/* (non-Javadoc)
	 * @see org.IpStatListener#onAfterConnected(org.ChannelContext, boolean, boolean, org.IpStat)
	 */
	@Override
	public void onAfterConnected(ChannelContext channelContext, boolean isConnected, boolean isReconnect, IpStat ipStat) throws Exception {
	}

	/* (non-Javadoc)
	 * @see org.IpStatListener#onDecodeError(org.ChannelContext, org.IpStat)
	 */
	@Override
	public void onDecodeError(ChannelContext channelContext, IpStat ipStat) {
	}

	/* (non-Javadoc)
	 * @see org.IpStatListener#onAfterSent(org.ChannelContext, org.Packet, boolean, org.IpStat)
	 */
	@Override
	public void onAfterSent(ChannelContext channelContext, Packet packet, boolean isSentSuccess, IpStat ipStat) throws Exception {
	}

	/* (non-Javadoc)
	 * @see org.IpStatListener#onAfterDecoded(org.ChannelContext, org.Packet, int, org.IpStat)
	 */
	@Override
	public void onAfterDecoded(ChannelContext channelContext, Packet packet, int packetSize, IpStat ipStat) throws Exception {
	}

	/* (non-Javadoc)
	 * @see org.IpStatListener#onAfterReceivedBytes(org.ChannelContext, int, org.IpStat)
	 */
	@Override
	public void onAfterReceivedBytes(ChannelContext channelContext, int receivedBytes, IpStat ipStat) throws Exception {
	}

	/* (non-Javadoc)
	 * @see org.IpStatListener#onAfterHandled(org.ChannelContext, org.Packet, org.IpStat, long)
	 */
	@Override
	public void onAfterHandled(ChannelContext channelContext, Packet packet, IpStat ipStat, long cost) throws Exception {
	}

}
