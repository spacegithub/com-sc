package com.sc.socket.core.udp;

import com.sc.socket.core.Node;
import com.sc.socket.core.udp.intf.UdpHandler;

/**
 *
 * 2017年7月5日 下午3:53:04
 */
public class UdpServerConf extends UdpConf {

	private UdpHandler udpHandler;

	private int readBufferSize = 1024 * 1024;

	public UdpServerConf(int port, UdpHandler udpHandler, int timeout) {
		super(timeout);
		this.setUdpHandler(udpHandler);
		this.setServerNode(new Node(null, port));
	}

	public int getReadBufferSize() {
		return readBufferSize;
	}

	public UdpHandler getUdpHandler() {
		return udpHandler;
	}

	public void setReadBufferSize(int readBufferSize) {
		this.readBufferSize = readBufferSize;
	}

	public void setUdpHandler(UdpHandler udpHandler) {
		this.udpHandler = udpHandler;
	}
}
