package com.sc.socket.core;

import com.sc.socket.core.intf.SioUuid;

/**
 *
 * 2017年6月5日 上午10:31:40
 */
public class DefaultSioUuid implements SioUuid {

	/**
	 *
	 *
	 */
	public DefaultSioUuid() {
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
