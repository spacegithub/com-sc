package com.sc.socket.test;

import com.sc.socket.core.Node;
import com.sc.socket.core.udp.UdpPacket;
import com.sc.socket.core.udp.UdpServer;
import com.sc.socket.core.udp.UdpServerConf;
import com.sc.socket.core.udp.intf.UdpHandler;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * <一句话功能简述>
 * <功能详细描述>
 *
 * @auth:qiss
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class SocketUDPTestServer {
    public static void main(String[] args) throws Exception {
        UdpServerConf udpServerConf = new UdpServerConf(8996, new UdpHandler() {
            @Override
            public void handler(UdpPacket udpPacket, DatagramSocket datagramSocket) {
                byte[] data = udpPacket.getData();
                String msg = new String(data);
                Node remote = udpPacket.getRemote();
                String str = "【" + msg + "】 from " + remote;
                System.out.println("-->" + str);

                DatagramPacket datagramPacket = new DatagramPacket(data, data.length, new InetSocketAddress(remote.getIp(), remote.getPort()));
                try {
                    datagramSocket.send(datagramPacket);
                } catch (Throwable e) {
                    e.printStackTrace();
                }


            }
        }, 0);

        UdpServer udpServer = new UdpServer(udpServerConf);

        udpServer.start();

    }
}
