package com.sc.socket.core.task;

import com.sc.socket.core.ChannelContext;
import com.sc.socket.core.GroupContext;
import com.sc.socket.core.intf.Packet;
import com.sc.socket.core.stat.IpStat;
import com.sc.socket.utils.SystemTimer;
import com.sc.socket.utils.lock.MapWithLock;
import com.sc.socket.utils.thread.pool.AbstractQueueRunnable;
import com.sc.socket.utils.thread.pool.AbstractSynRunnable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 队列异步线程处理解码后的结果数据
 */
public class HandlerRunnable extends AbstractQueueRunnable<Packet> {
    private static final Logger log = LoggerFactory.getLogger(HandlerRunnable.class);

    private ChannelContext channelContext = null;
    private GroupContext groupContext = null;

    private AtomicLong synFailCount = new AtomicLong();

    public HandlerRunnable(ChannelContext channelContext, Executor executor) {
        super(executor);
        this.channelContext = channelContext;
        groupContext = channelContext.groupContext;
    }

    /**
     * 处理packet
     */
    public void handler(Packet packet) {
        //		int ret = 0;
        long start = SystemTimer.currTime;
        try {
            Integer synSeq = packet.getSynSeq();
            if (synSeq != null && synSeq > 0) {
                MapWithLock<Integer, Packet> syns = groupContext.getWaitingResps();
                Packet initPacket = syns.remove(synSeq);
                if (initPacket != null) {
                    synchronized (initPacket) {
                        syns.put(synSeq, packet);
                        initPacket.notify();
                    }
                } else {
                    log.error("[{}]同步消息失败, synSeq is {}, 但是同步集合中没有对应key值", synFailCount.incrementAndGet(), synSeq);
                }
            } else {
                //处理解码后的结果
                groupContext.getAioHandler().handler(packet, channelContext);
            }
        } catch (Throwable e) {
            log.error(packet.logstr(), e);
        } finally {
            long end = SystemTimer.currTime;
            long iv = end - start;
            //增加统计信息和回调
            if (groupContext.statOn) {
                channelContext.stat.handledPackets.incrementAndGet();
                channelContext.stat.handledBytes.addAndGet(packet.getByteCount());
                channelContext.stat.handledPacketCosts.addAndGet(iv);

                groupContext.groupStat.handledPackets.incrementAndGet();
                groupContext.groupStat.handledBytes.addAndGet(packet.getByteCount());
                groupContext.groupStat.handledPacketCosts.addAndGet(iv);
            }

            if (groupContext.ipStats.durationList != null && groupContext.ipStats.durationList.size() > 0) {
                try {
                    for (Long v : groupContext.ipStats.durationList) {
                        IpStat ipStat = (IpStat) groupContext.ipStats.get(v, channelContext.getClientNode().getIp());
                        ipStat.getHandledPackets().incrementAndGet();
                        ipStat.getHandledBytes().addAndGet(packet.getByteCount());
                        ipStat.getHandledPacketCosts().addAndGet(iv);
                        groupContext.getIpStatListener().onAfterHandled(channelContext, packet, ipStat, iv);
                    }
                } catch (Exception e1) {
                    log.error(e1.toString(), e1);
                }
            }

            if (groupContext.getAioListener() != null) {
                try {
                    groupContext.getAioListener().onAfterHandled(channelContext, packet, iv);
                } catch (Exception e) {
                    log.error(e.toString(), e);
                }
            }

        }
    }

    /**
     * @see AbstractSynRunnable#runTask()
     *
     *
     * 2016年12月5日 下午3:02:49
     */
    @Override
    public void runTask() {
        Packet packet = null;
        while ((packet = msgQueue.poll()) != null) {
            handler(packet);
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ":" + channelContext.toString();
    }

    @Override
    public String logstr() {
        return toString();
    }
}
