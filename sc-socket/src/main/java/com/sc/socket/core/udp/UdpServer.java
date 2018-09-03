package com.sc.socket.core.udp;

import com.sc.socket.core.Node;
import com.sc.socket.core.udp.task.UdpHandlerRunnable;
import com.sc.socket.core.udp.task.UdpSendRunnable;
import com.sc.socket.utils.hutool.StrUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * UDP服务端
 * 2017年7月5日 下午2:47:16
 */
public class UdpServer {
    private static Logger log = LoggerFactory.getLogger(UdpServer.class);

    /**
     * 服务端处理队列
     */
    private LinkedBlockingQueue<UdpPacket> handlerQueue = new LinkedBlockingQueue<>();

    /**
     * 服务端发送队列
     */
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
        Thread thread = new Thread(udpHandlerRunnable, "socket-udp-server-handler");
        thread.setDaemon(false);
        thread.start();
    }

    private void startListen() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String startLog = "started socket udp server: " + udpServerConf.getServerNode();
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

        Thread thread = new Thread(runnable, "socket-udp-server-listen");
        thread.setDaemon(false);
        thread.start();
    }

    private void startSend() {
        Thread thread = new Thread(udpSendRunnable, "socket-udp-client-send");
        thread.setDaemon(false);
        thread.start();
    }

    public void stop() {
        isStopped = true;
        datagramSocket.close();
        udpHandlerRunnable.stop();
    }
}
