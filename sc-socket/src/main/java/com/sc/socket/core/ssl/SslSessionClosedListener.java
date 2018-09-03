package com.sc.socket.core.ssl;

import com.sc.socket.core.ChannelContext;
import com.sc.socket.core.Sio;
import com.sc.socket.core.ssl.facade.ISessionClosedListener;

public class SslSessionClosedListener implements ISessionClosedListener {
	private ChannelContext channelContext;

	public SslSessionClosedListener(ChannelContext channelContext) {
		this.channelContext = channelContext;
	}

	@Override
	public void onSessionClosed() {
//		log.info("{} onSessionClosed", channelContext);
		Sio.close(channelContext, "SSL SessionClosed");
	}

}
