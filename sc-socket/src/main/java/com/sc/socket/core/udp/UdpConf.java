package com.sc.socket.core.udp;

import com.sc.socket.core.Node;

/**
 * udp配置抽象
 * 2017年7月5日 下午2:53:38
 */
public class UdpConf {

    private int timeout = 5000;

    private Node serverNode = null;

    private String charset = "utf-8";

    /**
     * udp超时时间
     */
    public UdpConf(int timeout) {
        this.setTimeout(timeout);
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public Node getServerNode() {
        return serverNode;
    }

    public void setServerNode(Node serverNode) {
        this.serverNode = serverNode;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
