package com.sc.socket.server;

import com.sc.socket.core.Node;
import com.sc.socket.utils.SysConst;
import com.sc.socket.utils.date.DateUtils;
import com.sc.socket.utils.hutool.StrUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 服务端实例
 */
public class SioServer {
    private static Logger log = LoggerFactory.getLogger(SioServer.class);

    /**
     * 服务端的上下文环境
     */
    private ServerGroupContext serverGroupContext;

    /**
     * 异步服务通道
     */
    private AsynchronousServerSocketChannel serverSocketChannel;

    /**
     * 异步通道组 共享资源
     */
    private AsynchronousChannelGroup channelGroup = null;

    /**
     * 服务器节点   每个服务ip端口的抽象
     */
    private Node serverNode;

    /**
     * 是否等待关闭
     */
    private boolean isWaitingStop = false;

    /**
     * @param serverGroupContext 2017年1月2日 下午5:53:06
     */
    public SioServer(ServerGroupContext serverGroupContext) {
        super();
        this.serverGroupContext = serverGroupContext;
    }

    /**
     * @return the serverGroupContext
     */
    public ServerGroupContext getServerGroupContext() {
        return serverGroupContext;
    }

    /**
     * @param serverGroupContext the serverGroupContext to set
     */
    public void setServerGroupContext(ServerGroupContext serverGroupContext) {
        this.serverGroupContext = serverGroupContext;
    }

    /**
     * @return the serverNode
     */
    public Node getServerNode() {
        return serverNode;
    }

    /**
     * @return the serverSocketChannel
     */
    public AsynchronousServerSocketChannel getServerSocketChannel() {
        return serverSocketChannel;
    }

    /**
     * @return the isWaitingStop
     */
    public boolean isWaitingStop() {
        return isWaitingStop;
    }

    /**
     * @param isWaitingStop the isWaitingStop to set
     */
    public void setWaitingStop(boolean isWaitingStop) {
        this.isWaitingStop = isWaitingStop;
    }

    public void start(String serverIp, int serverPort) throws IOException {
        long start = System.currentTimeMillis();
        this.serverNode = new Node(serverIp, serverPort);
        //注册通道组线程池
        channelGroup = AsynchronousChannelGroup.withThreadPool(serverGroupContext.groupExecutor);
        //初始化
        serverSocketChannel = AsynchronousServerSocketChannel.open(channelGroup);
        //重用地址 对于面向数据报的套接字，套接字选项用于允许多个程序绑定到相同的地址。 当套接字用于互联网协议（IP）组播时，应启用此选项.
        serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        //套接字的大小接收缓冲区
        serverSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 64 * 1024);

        InetSocketAddress listenAddress = null;

        if (StrUtil.isBlank(serverIp)) {
            listenAddress = new InetSocketAddress(serverPort);
        } else {
            listenAddress = new InetSocketAddress(serverIp, serverPort);
        }
        //将通道的套接字绑定到本地地址，并配置套接字以监听连接。
        serverSocketChannel.bind(listenAddress, 0);//挂起连接的最大数 0或负值为默认值

        AcceptCompletionHandler acceptCompletionHandler = serverGroupContext.getAcceptCompletionHandler();
        //接受连接 此方法启动异步操作以接受对该通道的套接字进行的连接
        serverSocketChannel.accept(this, acceptCompletionHandler);

        serverGroupContext.startTime = System.currentTimeMillis();

        String baseStr = "|----------------------------------------------------------------------------------------|";
        int baseLen = baseStr.length();
        StackTraceElement[] ses = Thread.currentThread().getStackTrace();
        StackTraceElement se = ses[ses.length - 1];
        int xxLen = 18;
        int aaLen = baseLen - 3;
        List<String> infoList = new ArrayList<>();
        infoList.add(StrUtil.fillAfter("SOCKET version", ' ', xxLen) + ": " + SysConst.SOCKET_CORE_VERSION);

        infoList.add(StrUtil.fillAfter("-", '-', aaLen));

        infoList.add(StrUtil.fillAfter("GroupContext name", ' ', xxLen) + ": " + serverGroupContext.getName());
        infoList.add(StrUtil.fillAfter("Started at", ' ', xxLen) + ": " + DateUtils.formatDateTime(new Date()));
        infoList.add(StrUtil.fillAfter("Listen on", ' ', xxLen) + ": " + this.serverNode);
        infoList.add(StrUtil.fillAfter("Main Class", ' ', xxLen) + ": " + se.getClassName());

        try {
            RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
            String runtimeName = runtimeMxBean.getName();
            String pid = runtimeName.split("@")[0];
            long startTime = runtimeMxBean.getStartTime();
            long startCost = System.currentTimeMillis() - startTime;
            infoList.add(StrUtil.fillAfter("Jvm start time", ' ', xxLen) + ": " + startCost + " ms");
            infoList.add(StrUtil.fillAfter("Tio start time", ' ', xxLen) + ": " + (System.currentTimeMillis() - start) + " ms");
            infoList.add(StrUtil.fillAfter("Pid", ' ', xxLen) + ": " + pid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //100
        String printStr = "\r\n" + baseStr + "\r\n";
        //		printStr += "|--" + leftStr + " " + info + " " + rightStr + "--|\r\n";
        for (String string : infoList) {
            printStr += "| " + StrUtil.fillAfter(string, ' ', aaLen) + "|\r\n";
        }
        printStr += baseStr + "\r\n";
        if (log.isInfoEnabled()) {
            log.info(printStr);
        } else {
            System.out.println(printStr);
        }
    }

    /**
     *
     * @return
     *
     */
    public boolean stop() {
        isWaitingStop = true;
        boolean ret = true;

        try {
            channelGroup.shutdownNow();
        } catch (Exception e) {
            log.error("channelGroup.shutdownNow()时报错", e);
        }

        try {
            serverSocketChannel.close();
        } catch (Exception e1) {
            log.error("serverSocketChannel.close()时报错", e1);
        }

        try {
            serverGroupContext.groupExecutor.shutdown();
        } catch (Exception e1) {
            log.error(e1.toString(), e1);
        }
        try {
            serverGroupContext.tioExecutor.shutdown();
        } catch (Exception e1) {
            log.error(e1.toString(), e1);
        }

        serverGroupContext.setStopped(true);
        try {
            ret = ret && serverGroupContext.groupExecutor.awaitTermination(6000, TimeUnit.SECONDS);
            ret = ret && serverGroupContext.tioExecutor.awaitTermination(6000, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error(e.getLocalizedMessage(), e);
        }

        log.info(this.serverNode + " stopped");
        return ret;
    }
}
