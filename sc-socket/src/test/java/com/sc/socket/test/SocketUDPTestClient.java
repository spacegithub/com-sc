package com.sc.socket.test;

import com.sc.socket.core.udp.UdpClient;
import com.sc.socket.core.udp.UdpClientConf;

/**
 * <一句话功能简述>
 * <功能详细描述>
 *
 *
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class SocketUDPTestClient {
    public static void main(String[] args) {
        UdpClientConf udpClientConf = new UdpClientConf("127.0.0.1", 8996, 0);
        UdpClient udpClient = new UdpClient(udpClientConf);
        udpClient.start();
        udpClient.send("HELLO Server!");
    }
}
