package com.sc.socket.core.udp;

import com.sc.socket.core.Node;
import com.sc.socket.core.udp.task.UdpSendRunnable;
import com.sc.socket.utils.SystemTimer;
import com.sc.socket.utils.hutool.StrUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * 2017年7月5日 下午2:54:12
 */
public class UdpClient {
	private static Logger log = LoggerFactory.getLogger(UdpClient.class);

	//	private static final int TIMEOUT = 5000; //设置接收数据的超时时间


	private LinkedBlockingQueue<DatagramPacket> queue = new LinkedBlockingQueue<>();

	private UdpClientConf udpClientConf = null;

	/**
	 * 服务器地址
	 */
	private InetSocketAddress inetSocketAddress = null;

	private UdpSendRunnable udpSendRunnable = null;

	public UdpClient(UdpClientConf udpClientConf) {
		super();
		this.udpClientConf = udpClientConf;
		Node node = this.udpClientConf.getServerNode();
		inetSocketAddress = new InetSocketAddress(node.getIp(), node.getPort());
		udpSendRunnable = new UdpSendRunnable(queue, udpClientConf, null);
	}

	public void send(byte[] data) {
		DatagramPacket datagramPacket = new DatagramPacket(data, data.length, inetSocketAddress);
		queue.add(datagramPacket);
	}

	public void send(String str) {
		send(str, null);
	}

	public void send(String data, String charset) {
		if (StrUtil.isBlank(data)) {
			return;
		}
		try {
			if (StrUtil.isBlank(charset)) {
				charset = udpClientConf.getCharset();
			}
			byte[] bs = data.getBytes(charset);
			send(bs);
		} catch (UnsupportedEncodingException e) {
			log.error(e.toString(), e);
		}
	}

	public void start() {
		Thread thread = new Thread(udpSendRunnable, "tio-udp-client-send");
		thread.setDaemon(false);
		thread.start();
	}
}
