package com.sc.socket.client;

import com.sc.socket.client.intf.ClientAioHandler;
import com.sc.socket.core.ChannelContext;
import com.sc.socket.core.Node;
import com.sc.socket.core.Tio;
import com.sc.socket.core.intf.Packet;
import com.sc.socket.core.ssl.SslFacadeContext;
import com.sc.socket.core.stat.ChannelStat;
import com.sc.socket.utils.SystemTimer;
import com.sc.socket.utils.hutool.StrUtil;
import com.sc.socket.utils.lock.SetWithLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

/**
 * 客户端抽象
 * 2017年4月1日 上午9:29:58
 */
public class TioClient {
    private static Logger log = LoggerFactory.getLogger(TioClient.class);
    private AsynchronousChannelGroup channelGroup;
    private ClientGroupContext clientGroupContext;

    /**
     * @throws IOException
     */
    public TioClient(final ClientGroupContext clientGroupContext) throws IOException {
        super();
        this.clientGroupContext = clientGroupContext;
        this.channelGroup = AsynchronousChannelGroup.withThreadPool(clientGroupContext.groupExecutor);
        //守护线程发送客户端心跳
        startHeartbeatTask();
        //守护线程,客户端断连接后重新尝试连接
        startReconnTask();
    }

    /**
     * @param serverNode
     * @throws Exception
     */
    public void asynConnect(Node serverNode) throws Exception {
        asynConnect(serverNode, null);
    }

    /**
     *
     * @param serverNode
     * @param timeout
     * @throws Exception
     *
     *
     *
     */
    public void asynConnect(Node serverNode, Integer timeout) throws Exception {
        asynConnect(serverNode, null, null, timeout);
    }

    /**
     *
     * @param serverNode
     * @param bindIp
     * @param bindPort
     * @param timeout
     * @throws Exception
     *
     *
     *
     */
    public void asynConnect(Node serverNode, String bindIp, Integer bindPort, Integer timeout) throws Exception {
        connect(serverNode, bindIp, bindPort, null, timeout, false);
    }

    /**
     *
     * @param serverNode
     * @return
     * @throws Exception
     *
     *
     *
     */
    public ClientChannelContext connect(Node serverNode) throws Exception {
        return connect(serverNode, null);
    }

    /**
     *
     * @param serverNode
     * @param timeout
     * @return
     * @throws Exception
     *
     */
    public ClientChannelContext connect(Node serverNode, Integer timeout) throws Exception {
        return connect(serverNode, null, 0, timeout);
    }

    /**
     * @param timeout 超时时间，单位秒
     */
    public ClientChannelContext connect(Node serverNode, String bindIp, Integer bindPort, ClientChannelContext initClientChannelContext, Integer timeout) throws Exception {
        return connect(serverNode, bindIp, bindPort, initClientChannelContext, timeout, true);
    }

    /**
     * @param timeout 超时时间，单位秒
     * @param isSyn   true: 同步, false: 异步
     */
    private ClientChannelContext connect(Node serverNode, String bindIp, Integer bindPort, ClientChannelContext initClientChannelContext, Integer timeout, boolean isSyn)
            throws Exception {

        AsynchronousSocketChannel asynchronousSocketChannel = null;
        ClientChannelContext channelContext = null;
        boolean isReconnect = initClientChannelContext != null;
        //		ClientAioListener clientAioListener = clientGroupContext.getClientAioListener();

        long start = SystemTimer.currTime;
        asynchronousSocketChannel = AsynchronousSocketChannel.open(channelGroup);
        long end = SystemTimer.currTime;
        long iv = end - start;
        if (iv >= 100) {
            log.error("{}, open 耗时:{} ms", channelContext, iv);
        }

        asynchronousSocketChannel.setOption(StandardSocketOptions.TCP_NODELAY, true);
        asynchronousSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        asynchronousSocketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

        InetSocketAddress bind = null;
        if (bindPort != null && bindPort > 0) {
            if (false == StrUtil.isBlank(bindIp)) {
                bind = new InetSocketAddress(bindIp, bindPort);
            } else {
                bind = new InetSocketAddress(bindPort);
            }
        }

        if (bind != null) {
            asynchronousSocketChannel.bind(bind);
        }

        channelContext = initClientChannelContext;

        start = SystemTimer.currTime;

        InetSocketAddress inetSocketAddress = new InetSocketAddress(serverNode.getIp(), serverNode.getPort());

        ConnectionCompletionVo attachment = new ConnectionCompletionVo(channelContext, this, isReconnect, asynchronousSocketChannel, serverNode, bindIp, bindPort);

        if (isSyn) {
            Integer realTimeout = timeout;
            if (realTimeout == null) {
                realTimeout = 5;
            }

            CountDownLatch countDownLatch = new CountDownLatch(1);
            attachment.setCountDownLatch(countDownLatch);
            //连接并处理连接后的回调
            asynchronousSocketChannel.connect(inetSocketAddress, attachment, clientGroupContext.getConnectionCompletionHandler());
            boolean f = countDownLatch.await(realTimeout, TimeUnit.SECONDS);
            if (f) {
                return attachment.getChannelContext();
            } else {
                log.error("countDownLatch.await(realTimeout, TimeUnit.SECONDS) 返回false ");
                return attachment.getChannelContext();
            }
        } else {
            asynchronousSocketChannel.connect(inetSocketAddress, attachment, clientGroupContext.getConnectionCompletionHandler());
            return null;
        }
    }

    /**
     * @param timeout 超时时间，单位秒
     */
    public ClientChannelContext connect(Node serverNode, String bindIp, Integer bindPort, Integer timeout) throws Exception {
        return connect(serverNode, bindIp, bindPort, null, timeout);
    }

    /**
     * @return the channelGroup
     */
    public AsynchronousChannelGroup getChannelGroup() {
        return channelGroup;
    }

    /**
     * @return the clientGroupContext
     */
    public ClientGroupContext getClientGroupContext() {
        return clientGroupContext;
    }

    /**
     * @param clientGroupContext the clientGroupContext to set
     */
    public void setClientGroupContext(ClientGroupContext clientGroupContext) {
        this.clientGroupContext = clientGroupContext;
    }

    /**
     *
     * @param channelContext
     * @param timeout
     * @return
     * @throws Exception
     *
     *
     *
     */
    public void reconnect(ClientChannelContext channelContext, Integer timeout) throws Exception {
        connect(channelContext.getServerNode(), channelContext.getBindIp(), channelContext.getBindPort(), channelContext, timeout);
    }

    /**
     * 定时任务：发心跳
     */
    private void startHeartbeatTask() {
        final ClientGroupStat clientGroupStat = (ClientGroupStat) clientGroupContext.groupStat;
        final ClientAioHandler aioHandler = clientGroupContext.getClientAioHandler();

        final String id = clientGroupContext.getId();
        //新守护线程发送心跳数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!clientGroupContext.isStopped()) {
//					final long heartbeatTimeout = clientGroupContext.heartbeatTimeout;
                    if (clientGroupContext.heartbeatTimeout <= 0) {
                        log.warn("用户取消了框架层面的心跳定时发送功能，请用户自己去完成心跳机制");
                        break;
                    }
                    SetWithLock<ChannelContext> setWithLock = clientGroupContext.connecteds;
                    ReadLock readLock = setWithLock.readLock();
                    readLock.lock();
                    try {
                        Set<ChannelContext> set = setWithLock.getObj();
                        long currtime = SystemTimer.currTime;
                        for (ChannelContext entry : set) {
                            ClientChannelContext channelContext = (ClientChannelContext) entry;
                            if (channelContext.isClosed || channelContext.isRemoved) {
                                continue;
                            }

                            ChannelStat stat = channelContext.stat;
                            long compareTime = Math.max(stat.latestTimeOfReceivedByte, stat.latestTimeOfSentPacket);
                            long interval = currtime - compareTime;
                            if (interval >= clientGroupContext.heartbeatTimeout / 2) {
                                Packet packet = aioHandler.heartbeatPacket();
                                if (packet != null) {
                                    if (log.isInfoEnabled()) {
                                        log.info("{}发送心跳包", channelContext.toString());
                                    }
                                    Tio.send(channelContext, packet);
                                }
                            }
                        }
                        if (log.isInfoEnabled()) {
                            log.info("[{}]: curr:{}, closed:{}, received:({}p)({}b), handled:{}, sent:({}p)({}b)", id, set.size(), clientGroupStat.closed.get(),
                                    clientGroupStat.receivedPackets.get(), clientGroupStat.receivedBytes.get(), clientGroupStat.handledPackets.get(),
                                    clientGroupStat.sentPackets.get(), clientGroupStat.sentBytes.get());
                        }

                    } catch (Throwable e) {
                        log.error("", e);
                    } finally {
                        try {
                            readLock.unlock();
                            Thread.sleep(clientGroupContext.heartbeatTimeout / 4);
                        } catch (Throwable e) {
                            log.error(e.toString(), e);
                        } finally {

                        }
                    }
                }
            }
        }, "tio-timer-heartbeat" + id).start();
    }

    /**
     * 启动重连任务
     */
    private void startReconnTask() {
        final ReconnConf reconnConf = clientGroupContext.getReconnConf();
        if (reconnConf == null || reconnConf.getInterval() <= 0) {
            return;
        }

        final String id = clientGroupContext.getId();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!clientGroupContext.isStopped()) {
                    //log.info("准备重连");
                    LinkedBlockingQueue<ChannelContext> queue = reconnConf.getQueue();
                    ClientChannelContext channelContext = null;
                    try {
                        channelContext = (ClientChannelContext) queue.take();
                    } catch (InterruptedException e1) {
                        log.error(e1.toString(), e1);
                    }
                    if (channelContext == null) {
                        continue;
                        //						return;
                    }

                    if (channelContext.isRemoved) //已经删除的，不需要重新再连
                    {
                        continue;
                    }

                    SslFacadeContext sslFacadeContext = channelContext.sslFacadeContext;
                    if (sslFacadeContext != null) {
                        sslFacadeContext.setHandshakeCompleted(false);
                    }

                    long sleeptime = reconnConf.getInterval() - (SystemTimer.currTime - channelContext.stat.timeInReconnQueue);
                    //log.info("sleeptime:{}, closetime:{}", sleeptime, timeInReconnQueue);
                    if (sleeptime > 0) {
                        try {
                            Thread.sleep(sleeptime);
                        } catch (InterruptedException e) {
                            log.error(e.toString(), e);
                        }
                    }

                    if (channelContext.isRemoved || !channelContext.isClosed) //已经删除的和已经连上的，不需要重新再连
                    {
                        continue;
                    }
                    ReconnRunnable runnable = new ReconnRunnable(channelContext, TioClient.this);
                    reconnConf.getThreadPoolExecutor().execute(runnable);
                }
            }
        });
        thread.setName("tio-timer-reconnect-" + id);
        thread.setDaemon(true);
        thread.start();

    }

    /**
     *
     * @return
     *
     */
    public boolean stop() {
        boolean ret = true;
        try {
            clientGroupContext.groupExecutor.shutdown();
        } catch (Exception e1) {
            log.error(e1.toString(), e1);
        }
        try {
            clientGroupContext.tioExecutor.shutdown();
        } catch (Exception e1) {
            log.error(e1.toString(), e1);
        }


        clientGroupContext.setStopped(true);
        try {
            ret = ret && clientGroupContext.groupExecutor.awaitTermination(6000, TimeUnit.SECONDS);
            ret = ret && clientGroupContext.tioExecutor.awaitTermination(6000, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        log.info("client resource has released");
        return ret;
    }

    /**
     * 自动重连任务
     */
    private static class ReconnRunnable implements Runnable {
        ClientChannelContext channelContext = null;
        TioClient tioClient = null;

        //		private static Map<Node, Long> cacheMap = new HashMap<>();

        public ReconnRunnable(ClientChannelContext channelContext, TioClient tioClient) {
            this.channelContext = channelContext;
            this.tioClient = tioClient;
        }

        /**
         * @see Runnable#run()
         *
         *
         * 2017年2月2日 下午8:24:40
         */
        @Override
        public void run() {
            ReentrantReadWriteLock closeLock = channelContext.closeLock;
            WriteLock writeLock = closeLock.writeLock();
            writeLock.lock();
            try {
                if (!channelContext.isClosed) //已经连上了，不需要再重连了
                {
                    return;
                }
                long start = SystemTimer.currTime;
                tioClient.reconnect(channelContext, 2);
                long end = SystemTimer.currTime;
                long iv = end - start;
                if (iv >= 100) {
                    log.error("{},重连耗时:{} ms", channelContext, iv);
                } else {
                    log.info("{},重连耗时:{} ms", channelContext, iv);
                }

                if (channelContext.isClosed) {
                    channelContext.setReconnCount(channelContext.getReconnCount() + 1);
                    //					cacheMap.put(channelContext.getServerNode(), SystemTimer.currTime);
                    return;
                }
            } catch (Throwable e) {
                log.error(e.toString(), e);
            } finally {
                writeLock.unlock();
            }

        }
    }
}
