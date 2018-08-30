/**
 * 
 */
package com.sc.socket.core.ssl;

import com.sc.socket.core.ChannelContext;
import com.sc.socket.core.intf.Packet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sc.socket.core.ssl.facade.IHandshakeCompletedListener;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 *
 */
public class SslHandshakeCompletedListener implements IHandshakeCompletedListener {
	private static Logger log = LoggerFactory.getLogger(SslHandshakeCompletedListener.class);

	private ChannelContext channelContext;

	/**
	 * 
	 */
	public SslHandshakeCompletedListener(ChannelContext channelContext) {
		this.channelContext = channelContext;
	}

	@Override
	public void onComplete() {
		log.info("{}, 完成SSL握手", channelContext);
		channelContext.sslFacadeContext.setHandshakeCompleted(true);

		if (channelContext.groupContext.getAioListener() != null) {
			try {
				channelContext.groupContext.getAioListener().onAfterConnected(channelContext, true, channelContext.isReconnect);
			} catch (Exception e) {
				log.error(e.toString(), e);
			}
		}

		ConcurrentLinkedQueue<Packet> forSendAfterSslHandshakeCompleted = channelContext.sendRunnable.getForSendAfterSslHandshakeCompleted(false);
		if (forSendAfterSslHandshakeCompleted == null || forSendAfterSslHandshakeCompleted.size() == 0) {
			return;
		}

		log.info("{} 业务层在SSL握手前就有{}条数据待发送", channelContext, forSendAfterSslHandshakeCompleted.size());
		while (true) {
			Packet packet = forSendAfterSslHandshakeCompleted.poll();
			if (packet != null) {
				if (channelContext.groupContext.useQueueSend) {
					channelContext.sendRunnable.addMsg(packet);
				} else {
					channelContext.sendRunnable.sendPacket(packet);
				}

			} else {
				break;
			}
		}
		if (channelContext.groupContext.useQueueSend) {
			channelContext.sendRunnable.execute();
		}
	}

}
