package com.sc.socket.server;

import com.sc.socket.core.ReadCompletionHandler;
import com.sc.socket.core.Sio;
import com.sc.socket.core.ssl.SslUtils;
import com.sc.socket.core.stat.IpStat;
import com.sc.socket.utils.SystemTimer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * 服务端异步IO结果处理
 * 2017年4月4日 上午9:27:45
 */
public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, SioServer> {

    private static Logger log = LoggerFactory.getLogger(AcceptCompletionHandler.class);

    public AcceptCompletionHandler() {
    }

    /**
     * 操作完成
     */
    @Override
    public void completed(AsynchronousSocketChannel asynchronousSocketChannel, SioServer tioServer) {
        try {
            ServerGroupContext serverGroupContext = tioServer.getServerGroupContext();
            InetSocketAddress inetSocketAddress = (InetSocketAddress) asynchronousSocketChannel.getRemoteAddress();
            String clientIp = inetSocketAddress.getHostString();


            if (Sio.IpBlacklist.isInBlacklist(serverGroupContext, clientIp)) {
                log.warn("[{}]在黑名单中", clientIp);
                asynchronousSocketChannel.close();
                return;
            }

            if (serverGroupContext.statOn) {
                ((ServerGroupStat) serverGroupContext.groupStat).accepted.incrementAndGet();
            }


            asynchronousSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            asynchronousSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 64 * 1024);
            asynchronousSocketChannel.setOption(StandardSocketOptions.SO_SNDBUF, 64 * 1024);
            asynchronousSocketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

            ServerChannelContext channelContext = new ServerChannelContext(serverGroupContext, asynchronousSocketChannel);
            channelContext.setClosed(false);
            channelContext.stat.setTimeFirstConnected(SystemTimer.currTime);
            channelContext.setServerNode(tioServer.getServerNode());


            serverGroupContext.ips.bind(channelContext);

            boolean isConnected = true;
            boolean isReconnect = false;
            if (serverGroupContext.getServerAioListener() != null) {
                if (!SslUtils.isSsl(channelContext.groupContext)) {
                    try {
                        serverGroupContext.getServerAioListener().onAfterConnected(channelContext, isConnected, isReconnect);
                    } catch (Throwable e) {
                        log.error(e.toString(), e);
                    }
                }
            }


            if (serverGroupContext.ipStats.durationList != null && serverGroupContext.ipStats.durationList.size() > 0) {
                try {
                    for (Long v : serverGroupContext.ipStats.durationList) {
                        IpStat ipStat = (IpStat) serverGroupContext.ipStats.get(v, clientIp);
                        ipStat.getRequestCount().incrementAndGet();
                        serverGroupContext.getIpStatListener().onAfterConnected(channelContext, isConnected, isReconnect, ipStat);
                    }
                } catch (Exception e) {
                    log.error(e.toString(), e);
                }
            }


            if (!tioServer.isWaitingStop()) {
                ReadCompletionHandler readCompletionHandler = channelContext.getReadCompletionHandler();
                ByteBuffer readByteBuffer = readCompletionHandler.getReadByteBuffer();//ByteBuffer.allocateDirect(channelContext.groupContext.getReadBufferSize());
                readByteBuffer.position(0);
                readByteBuffer.limit(readByteBuffer.capacity());
                //读取通道数据,交由后续读取处理
                asynchronousSocketChannel.read(readByteBuffer, readByteBuffer, readCompletionHandler);
            }
        } catch (Throwable e) {
            log.error("", e);
        } finally {
            if (tioServer.isWaitingStop()) {
                log.info("{}即将关闭服务器，不再接受新请求", tioServer.getServerNode());
            } else {
                AsynchronousServerSocketChannel serverSocketChannel = tioServer.getServerSocketChannel();
                //继续监听
                serverSocketChannel.accept(tioServer, this);
            }
        }
    }

    /**
     * 操作失败
     */
    @Override
    public void failed(Throwable exc, SioServer tioServer) {
        AsynchronousServerSocketChannel serverSocketChannel = tioServer.getServerSocketChannel();
        //继续监听
        serverSocketChannel.accept(tioServer, this);

        log.error("[" + tioServer.getServerNode() + "]监听出现异常", exc);

    }

}
