package com.sc.socket.core.udp;

import com.sc.socket.core.Node;
import com.sc.socket.core.udp.intf.UdpHandler;
import com.sc.socket.core.udp.task.UdpHandlerRunnable;
import com.sc.socket.core.udp.task.UdpSendRunnable;
import com.sc.socket.utils.hutool.StrUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * 2017年7月5日 下午2:47:16
 */
public class UdpServer {
	private static Logger log = LoggerFactory.getLogger(UdpServer.class);

	/**
	 * @param args
	 *
	 */
	public static void main(String[] args) throws IOException {
		final AtomicLong count = new AtomicLong();
		UdpServer udpServer = null;
		UdpHandler udpHandler = new UdpHandler() {
			@Override
			public void handler(UdpPacket udpPacket, DatagramSocket datagramSocket) {
				byte[] data = udpPacket.getData();
				String msg = new String(data);
				Node remote = udpPacket.getRemote();
				long c = count.incrementAndGet();
				if (c % 10000 == 0) {
					String str = "【" + msg + "】 from " + remote;
					log.error(str);
				}

//				log.info("收到来自{}的消息:【{}】", remote, msg);
				DatagramPacket datagramPacket = new DatagramPacket(data, data.length, new InetSocketAddress(remote.getIp(), remote.getPort()));
				try {
					datagramSocket.send(datagramPacket);
				} catch (Throwable e) {
					log.error(e.toString(), e);
				}

			}
		};
		UdpServerConf udpServerConf = new UdpServerConf(3000, udpHandler, 5000);

		udpServer = new UdpServer(udpServerConf);

		udpServer.start();
	}

	private LinkedBlockingQueue<UdpPacket> handlerQueue = new LinkedBlockingQueue<>();

	private LinkedBlockingQueue<DatagramPacket> sendQueue = new LinkedBlockingQueue<>();

	private DatagramSocket datagramSocket = null;

	private byte[] readBuf = null;

	private boolean isStopped = false;

	private UdpHandlerRunnable udpHandlerRunnable;

	private UdpSendRunnable udpSendRunnable = null;

	private UdpServerConf udpServerConf;

	/**
	 *
	 *
	 * @throws SocketException
	 */
	public UdpServer(UdpServerConf udpServerConf) throws SocketException {
		this.udpServerConf = udpServerConf;
		datagramSocket = new DatagramSocket(this.udpServerConf.getServerNode().getPort());
		readBuf = new byte[this.udpServerConf.getReadBufferSize()];
		udpHandlerRunnable = new UdpHandlerRunnable(udpServerConf.getUdpHandler(), handlerQueue, datagramSocket);

		udpSendRunnable = new UdpSendRunnable(sendQueue, udpServerConf, datagramSocket);
	}

	public void send(byte[] data, Node remoteNode) {
		InetSocketAddress inetSocketAddress = new InetSocketAddress(remoteNode.getIp(), remoteNode.getPort());
		DatagramPacket datagramPacket = new DatagramPacket(data, data.length, inetSocketAddress);
		sendQueue.add(datagramPacket);
	}

	public void send(String str, Node remoteNode) {
		send(str, null, remoteNode);
	}

	public void send(String data, String charset, Node remoteNode) {
		if (StrUtil.isBlank(data)) {
			return;
		}
		try {
			if (StrUtil.isBlank(charset)) {
				charset = udpServerConf.getCharset();
			}
			byte[] bs = data.getBytes(charset);
			send(bs, remoteNode);
		} catch (UnsupportedEncodingException e) {
			log.error(e.toString(), e);
		}
	}

	public void start() {
		startListen();
		startHandler();
		startSend();
	}

	private void startHandler() {
		Thread thread = new Thread(udpHandlerRunnable, "tio-udp-server-handler");
		thread.setDaemon(false);
		thread.start();
	}

	private void startListen() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				String startLog = "started tio udp server: " + udpServerConf.getServerNode();
				if (log.isInfoEnabled()) {
					log.info(startLog);
				} else {
					System.out.println(startLog);
				}

				while (!isStopped) {
					try {
						DatagramPacket datagramPacket = new DatagramPacket(readBuf, readBuf.length);
						datagramSocket.receive(datagramPacket);

						byte[] data = new byte[datagramPacket.getLength()];
						System.arraycopy(readBuf, 0, data, 0, datagramPacket.getLength());

						String remoteip = datagramPacket.getAddress().getHostAddress();
						int remoteport = datagramPacket.getPort();
						Node remote = new Node(remoteip, remoteport);
						UdpPacket udpPacket = new UdpPacket(data, remote);

						handlerQueue.put(udpPacket);
					} catch (Throwable e) {
						log.error(e.toString(), e);
					}
				}
			}
		};

		Thread thread = new Thread(runnable, "tio-udp-server-listen");
		thread.setDaemon(false);
		thread.start();
	}

	private void startSend() {
		Thread thread = new Thread(udpSendRunnable, "tio-udp-client-send");
		thread.setDaemon(false);
		thread.start();
	}

	public void stop() {
		isStopped = true;
		datagramSocket.close();
		udpHandlerRunnable.stop();
	}
}
