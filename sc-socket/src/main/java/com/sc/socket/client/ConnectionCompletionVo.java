package com.sc.socket.client;

import com.sc.socket.core.Node;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.CountDownLatch;

/**
 * 连接对象抽象
 * 2017年4月1日 上午9:32:17
 */
public class ConnectionCompletionVo {

    private ClientChannelContext channelContext = null;

    private TioClient tioClient = null;

    private boolean isReconnect = false;

    private AsynchronousSocketChannel asynchronousSocketChannel;

    private Node serverNode;

    private String bindIp;

    private Integer bindPort;

    private CountDownLatch countDownLatch = null;

    /**
     *
     *
     */
    public ConnectionCompletionVo() {

    }

    /**
     * @param channelContext
     * @param tioClient
     * @param isReconnect
     * @param asynchronousSocketChannel
     * @param serverNode
     * @param bindIp
     * @param bindPort
     *
     *
     *
     */
    public ConnectionCompletionVo(ClientChannelContext channelContext, TioClient tioClient, boolean isReconnect, AsynchronousSocketChannel asynchronousSocketChannel,
                                  Node serverNode, String bindIp, Integer bindPort) {
        super();
        this.channelContext = channelContext;
        this.tioClient = tioClient;
        this.isReconnect = isReconnect;
        this.asynchronousSocketChannel = asynchronousSocketChannel;
        this.serverNode = serverNode;
        this.bindIp = bindIp;
        this.bindPort = bindPort;
    }

    /**
     * @return the tioClient
     */
    public TioClient getTioClient() {
        return tioClient;
    }

    /**
     * @param tioClient the tioClient to set
     */
    public void setTioClient(TioClient tioClient) {
        this.tioClient = tioClient;
    }

    /**
     * @return the asynchronousSocketChannel
     */
    public AsynchronousSocketChannel getAsynchronousSocketChannel() {
        return asynchronousSocketChannel;
    }

    /**
     * @param asynchronousSocketChannel the asynchronousSocketChannel to set
     */
    public void setAsynchronousSocketChannel(AsynchronousSocketChannel asynchronousSocketChannel) {
        this.asynchronousSocketChannel = asynchronousSocketChannel;
    }

    /**
     * @return the bindIp
     */
    public String getBindIp() {
        return bindIp;
    }

    /**
     * @param bindIp the bindIp to set
     */
    public void setBindIp(String bindIp) {
        this.bindIp = bindIp;
    }

    /**
     * @return the bindPort
     */
    public Integer getBindPort() {
        return bindPort;
    }

    /**
     * @param bindPort the bindPort to set
     */
    public void setBindPort(Integer bindPort) {
        this.bindPort = bindPort;
    }

    /**
     * @return the channelContext
     */
    public ClientChannelContext getChannelContext() {
        return channelContext;
    }

    /**
     * @param channelContext the channelContext to set
     */
    public void setChannelContext(ClientChannelContext channelContext) {
        this.channelContext = channelContext;
    }

    /**
     * @return the countDownLatch
     */
    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    /**
     * @param countDownLatch the countDownLatch to set
     */
    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    /**
     * @return the serverNode
     */
    public Node getServerNode() {
        return serverNode;
    }

    /**
     * @param serverNode the serverNode to set
     */
    public void setServerNode(Node serverNode) {
        this.serverNode = serverNode;
    }

    /**
     * @return the isReconnect
     */
    public boolean isReconnect() {
        return isReconnect;
    }

    /**
     * @param isReconnect the isReconnect to set
     */
    public void setReconnect(boolean isReconnect) {
        this.isReconnect = isReconnect;
    }

}
