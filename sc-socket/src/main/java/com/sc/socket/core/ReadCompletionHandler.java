package com.sc.socket.core;

import com.sc.socket.core.stat.IpStat;
import com.sc.socket.core.utils.ByteBufferUtils;
import com.sc.socket.core.utils.SioUtils;
import com.sc.socket.utils.SystemTimer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

/**
 * 读取通道数据处理
 * 2017年4月4日 上午9:22:04
 */
public class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
    private static Logger log = LoggerFactory.getLogger(ReadCompletionHandler.class);
    private ChannelContext channelContext = null;
    private ByteBuffer readByteBuffer;


    /**
     *
     * @param channelContext
     *
     */
    public ReadCompletionHandler(ChannelContext channelContext) {
        this.channelContext = channelContext;
        this.readByteBuffer = ByteBuffer.allocate(channelContext.groupContext.getReadBufferSize());
    }

    @Override
    public void completed(Integer result, ByteBuffer byteBuffer) {
        if (result > 0) {//有数据读取到
            GroupContext groupContext = channelContext.groupContext;

            if (groupContext.statOn) {//记录统计信息
                groupContext.groupStat.receivedBytes.addAndGet(result);
                groupContext.groupStat.receivedTcps.incrementAndGet();
                channelContext.stat.receivedBytes.addAndGet(result);
                channelContext.stat.receivedTcps.incrementAndGet();
            }

            channelContext.stat.latestTimeOfReceivedByte = SystemTimer.currTime;

            if (groupContext.ipStats.durationList != null && groupContext.ipStats.durationList.size() > 0) {
                try {
                    for (Long v : groupContext.ipStats.durationList) {
                        IpStat ipStat = groupContext.ipStats.get(v, channelContext.getClientNode().getIp());
                        ipStat.getReceivedBytes().addAndGet(result);
                        ipStat.getReceivedTcps().incrementAndGet();
                        groupContext.getIpStatListener().onAfterReceivedBytes(channelContext, result, ipStat);
                    }
                } catch (Exception e1) {
                    log.error(channelContext.toString(), e1);
                }
            }

            if (groupContext.getAioListener() != null) {
                try {
                    groupContext.getAioListener().onAfterReceivedBytes(channelContext, result);
                } catch (Exception e) {
                    log.error("", e);
                }
            }


            readByteBuffer.flip();
            if (channelContext.sslFacadeContext == null) {//调用解码handle
                if (groupContext.useQueueDecode) {//队列异步线程进行解码,异步线程最终还是会调用decodeRunnable.decode()  非阻塞,提高效率
                    channelContext.decodeRunnable.addMsg(ByteBufferUtils.copy(readByteBuffer));
                    channelContext.decodeRunnable.execute();
                } else {//直接解码,直接调用decodeRunnable.decode() 进行解码
                    channelContext.decodeRunnable.setNewByteBuffer(readByteBuffer);
                    channelContext.decodeRunnable.decode();
                }
            } else {
                ByteBuffer copiedByteBuffer = null;
                try {
                    copiedByteBuffer = ByteBufferUtils.copy(readByteBuffer);
                    log.debug("{}, 丢给SslFacade解密:{}", channelContext, copiedByteBuffer);
                    channelContext.sslFacadeContext.getSslFacade().decrypt(copiedByteBuffer);
                } catch (Exception e) {
                    log.error(channelContext + ", " + e.toString() + copiedByteBuffer, e);
                    Sio.close(channelContext, e, e.toString());
                }
            }

            if (SioUtils.checkBeforeIO(channelContext)) {
                read();
            }

        } else if (result == 0) {
            log.error("{}, 读到的数据长度为0", channelContext);
            Sio.close(channelContext, null, "读到的数据长度为0");
            return;
        } else if (result < 0) {
            if (result == -1) {
                Sio.close(channelContext, null, "对方关闭了连接");
                return;
            } else {
                Sio.close(channelContext, null, "读数据时返回" + result);
                return;
            }
        }
    }

    //如果连接未关闭且还有待读字节,则继续读取,相当于递归调用ReadCompletionHandler.completed
    private void read() {
        readByteBuffer.position(0);
        readByteBuffer.limit(readByteBuffer.capacity());//填充缓冲区
        channelContext.asynchronousSocketChannel.read(readByteBuffer, readByteBuffer, this);//继续调用
    }

    /**
     *
     * @param exc
     * @param byteBuffer
     *
     */
    @Override
    public void failed(Throwable exc, ByteBuffer byteBuffer) {
        Sio.close(channelContext, exc, "读数据时发生异常");
    }

    /**
     *
     * @return
     *
     */
    public ByteBuffer getReadByteBuffer() {
        return readByteBuffer;
    }
}
