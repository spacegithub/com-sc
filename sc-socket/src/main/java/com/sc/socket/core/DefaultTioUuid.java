package com.sc.socket.core;

import com.sc.socket.core.intf.TioUuid;

/**
 *
 * 2017年6月5日 上午10:31:40
 */
public class DefaultTioUuid implements TioUuid {

	/**
	 *
	 *
	 */
	public DefaultTioUuid() {
	}

	/**
	 * @return
	 *
	 */
	@Override
	public String uuid() {
		return java.util.UUID.randomUUID().toString();
	}
}
