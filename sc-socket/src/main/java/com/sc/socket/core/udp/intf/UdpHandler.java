package com.sc.socket.core.udp.intf;

import com.sc.socket.core.udp.UdpPacket;

import java.net.DatagramSocket;

/**
 *
 * 2017年7月5日 下午2:46:47
 */
public interface UdpHandler {

	/**
	 *
	 * @param udpPacket
	 * @param datagramSocket
	 *
	 */
	public void handler(UdpPacket udpPacket, DatagramSocket datagramSocket);
}
