package com.sc.socket.core.task;

import com.sc.socket.core.ChannelContext;
import com.sc.socket.core.GroupContext;
import com.sc.socket.core.Sio;
import com.sc.socket.core.exception.AioDecodeException;
import com.sc.socket.core.intf.Packet;
import com.sc.socket.core.stat.ChannelStat;
import com.sc.socket.core.stat.IpStat;
import com.sc.socket.core.utils.ByteBufferUtils;
import com.sc.socket.utils.SystemTimer;
import com.sc.socket.utils.thread.pool.AbstractQueueRunnable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * 解码任务对象，一个连接对应一个本对象
 */
public class DecodeRunnable extends AbstractQueueRunnable<ByteBuffer> {
    private static final Logger log = LoggerFactory.getLogger(DecodeRunnable.class);
    private ChannelContext channelContext = null;
    private GroupContext groupContext = null;
    /**
     * 上一次解码剩下的数据
     */
    private ByteBuffer lastByteBuffer = null;
    /**
     * 新收到的数据
     */
    private ByteBuffer newByteBuffer = null;

    /**
     *
     */
    public DecodeRunnable(ChannelContext channelContext, Executor executor) {
        super(executor);
        this.channelContext = channelContext;
        this.groupContext = channelContext.groupContext;
    }

    /**
     *
     * @param packet
     * @param byteCount
     *
     */
    public void handler(Packet packet, int byteCount) {
        switch (groupContext.packetHandlerMode) {//是否使用队列异步线程处理 解码后的结果
            case QUEUE:
                channelContext.handlerRunnable.addMsg(packet);
                channelContext.handlerRunnable.execute();
                break;
            default:
                channelContext.handlerRunnable.handler(packet);
                break;
        }
    }

    /**
     * 清空处理的队列消息
     */
    @Override
    public void clearMsgQueue() {
        super.clearMsgQueue();
        lastByteBuffer = null;
        newByteBuffer = null;
    }

    @Override
    public void runTask() {
        //		int queueSize = msgQueue.size();
        //		if (queueSize == 0) {
        //			return;
        //		}

        while ((newByteBuffer = msgQueue.poll()) != null) {
            decode();
        }
    }

    /**
     * @see Runnable#run()
     *
     *
     * 2017年3月21日 下午4:26:39
     */
    public void decode() {
        ByteBuffer byteBuffer = newByteBuffer;
        if (lastByteBuffer != null) {
            byteBuffer = ByteBufferUtils.composite(lastByteBuffer, byteBuffer);
            lastByteBuffer = null;
        }

        label_2:
        while (true) {
            try {
                int initPosition = byteBuffer.position();
                int limit = byteBuffer.limit();
                int readableLength = limit - initPosition;
                Packet packet = null;
                if (channelContext.packetNeededLength != null) {
                    log.info("{}, 解码所需长度:{}", channelContext, channelContext.packetNeededLength);
                    if (readableLength >= channelContext.packetNeededLength) {
                        packet = groupContext.getAioHandler().decode(byteBuffer, limit, initPosition, readableLength, channelContext);
                    }
                } else {
                    try {
                        packet = groupContext.getAioHandler().decode(byteBuffer, limit, initPosition, readableLength, channelContext);
                    } catch (BufferUnderflowException e) {
                        //log.error(e.toString(), e);
                        //数据不够读
                    }
                }

                if (packet == null)// 数据不够，解不了码
                {
                    //					lastByteBuffer = ByteBufferUtils.copy(byteBuffer, initPosition, limit);
                    if (groupContext.useQueueDecode || (byteBuffer != newByteBuffer)) {
                        byteBuffer.position(initPosition);
                        byteBuffer.limit(limit);
                        lastByteBuffer = byteBuffer;
                    } else {
                        lastByteBuffer = ByteBufferUtils.copy(byteBuffer, initPosition, limit);
                    }
                    ChannelStat channelStat = channelContext.stat;
                    channelStat.decodeFailCount++;
                    //					int len = byteBuffer.limit() - initPosition;
                    log.debug("{} 本次解码失败, 已经连续{}次解码失败，参与解码的数据长度共{}字节", channelContext, channelStat.decodeFailCount, readableLength);
                    if (channelStat.decodeFailCount > 5) {
                        if (channelContext.packetNeededLength == null) {
                            log.info("{} 本次解码失败, 已经连续{}次解码失败，参与解码的数据长度共{}字节", channelContext, channelStat.decodeFailCount, readableLength);
                        }

                        //检查慢包攻击（只有自用版才有）
                        if (channelStat.decodeFailCount > 10) {
                            //							int capacity = lastByteBuffer.capacity();
                            int per = readableLength / channelStat.decodeFailCount;
                            if (per < Math.min(groupContext.getReadBufferSize() / 2, 256)) {
                                throw new AioDecodeException("连续解码" + channelStat.decodeFailCount + "次都不成功，并且平均每次接收到的数据为" + per + "字节，有慢攻击的嫌疑");
                            }
                        }
                    }
                    return;
                } else //解码成功
                {
                    channelContext.setPacketNeededLength(null);
                    channelContext.stat.latestTimeOfReceivedPacket = SystemTimer.currTime;
                    channelContext.stat.decodeFailCount = 0;

                    int len = byteBuffer.position() - initPosition;
                    packet.setByteCount(len);

                    if (groupContext.statOn) {
                        groupContext.groupStat.receivedPackets.incrementAndGet();
                        channelContext.stat.receivedPackets.incrementAndGet();
                    }

                    if (groupContext.ipStats.durationList != null && groupContext.ipStats.durationList.size() > 0) {
                        try {
                            for (Long v : groupContext.ipStats.durationList) {
                                IpStat ipStat = groupContext.ipStats.get(v, channelContext.getClientNode().getIp());
                                ipStat.getReceivedPackets().incrementAndGet();
                                groupContext.getIpStatListener().onAfterDecoded(channelContext, packet, len, ipStat);
                            }
                        } catch (Exception e1) {
                            log.error(packet.logstr(), e1);
                        }
                    }

                    if (groupContext.getAioListener() != null) {
                        try {
                            groupContext.getAioListener().onAfterDecoded(channelContext, packet, len);
                        } catch (Throwable e) {
                            log.error(e.toString(), e);
                        }
                    }

                    if (log.isDebugEnabled()) {
                        log.debug("{}, 解包获得一个packet:{}", channelContext, packet.logstr());
                    }

                    handler(packet, len);

                    if (byteBuffer.hasRemaining())//组包后，还剩有数据
                    {
                        if (log.isDebugEnabled()) {
                            log.debug("{},组包后，还剩有数据:{}", channelContext, byteBuffer.remaining());
                        }
                        continue label_2;
                    } else//组包后，数据刚好用完
                    {
                        lastByteBuffer = null;
                        log.debug("{},组包后，数据刚好用完", channelContext);
                        return;
                    }
                }
            } catch (Throwable e) {
                channelContext.setPacketNeededLength(null);
                log.error(channelContext + ", " + byteBuffer + ", 解码异常:" + e.toString(), e);

                if (e instanceof AioDecodeException) {
                    List<Long> list = groupContext.ipStats.durationList;
                    if (list != null && list.size() > 0) {
                        try {
                            for (Long v : list) {
                                IpStat ipStat = groupContext.ipStats.get(v, channelContext.getClientNode().getIp());
                                ipStat.getDecodeErrorCount().incrementAndGet();
                                groupContext.getIpStatListener().onDecodeError(channelContext, ipStat);
                            }
                        } catch (Exception e1) {
                            log.error(e1.toString(), e1);
                        }
                    }
                }

                Sio.close(channelContext, e, "解码异常:" + e.getMessage());
                return;
            }
        }
    }

    /**
     * @param newByteBuffer the newByteBuffer to set
     */
    public void setNewByteBuffer(ByteBuffer newByteBuffer) {
        this.newByteBuffer = newByteBuffer;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ":" + channelContext.toString();
    }

    @Override
    public String logstr() {
        return toString();
    }

    @SuppressWarnings("unused")
    private static class LastByteBufferInfo {
        /**
         * 数据长度
         */
        int len = 0;
        ByteBuffer data = null;
        int tempFileIndex = 0;

        //org.MaintainUtils.tempReceivedFile(ChannelContext)
    }
}
