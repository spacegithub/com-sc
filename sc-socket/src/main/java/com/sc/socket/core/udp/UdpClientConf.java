package com.sc.socket.core.udp;

import com.sc.socket.core.Node;

/**
 * udp客户端配置
 * 2017年7月5日 下午3:53:20
 */
public class UdpClientConf extends UdpConf {
    /**
     * UDP客户端配置
     */
    public UdpClientConf(String serverip, int serverport, int timeout) {
        super(timeout);
        Node node = new Node(serverip, serverport);
        this.setServerNode(node);
        this.setTimeout(timeout);
    }

}
