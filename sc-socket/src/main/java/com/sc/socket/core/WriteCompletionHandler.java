package com.sc.socket.core;

import com.sc.socket.core.WriteCompletionHandler.WriteCompletionVo;
import com.sc.socket.core.intf.Packet;
import com.sc.socket.core.stat.IpStat;
import com.sc.socket.utils.SystemTimer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * 写消息处理
 */
public class WriteCompletionHandler implements CompletionHandler<Integer, WriteCompletionVo> {

    private static Logger log = LoggerFactory.getLogger(WriteCompletionHandler.class);
    private ChannelContext channelContext = null;
    //写信号量 1表示只能一个线程同时访问
    private Semaphore writeSemaphore = new Semaphore(1);

    public WriteCompletionHandler(ChannelContext channelContext) {
        this.channelContext = channelContext;
    }

    @Override
    public void completed(Integer result, WriteCompletionVo writeCompletionVo) {
        //		Object attachment = writeCompletionVo.getObj();
        ByteBuffer byteBuffer = writeCompletionVo.byteBuffer;  //[pos=212 lim=212 cap=212]
        if (byteBuffer.hasRemaining()) {//判断是否还有剩余未写
            //			int iv = byteBuffer.capacity() - byteBuffer.position();
            log.info("{} {}/{} has sent", channelContext, byteBuffer.position(), byteBuffer.limit());
            channelContext.asynchronousSocketChannel.write(byteBuffer, writeCompletionVo, this);//如果未写完会继续写,相当于回调
            channelContext.stat.latestTimeOfSentByte = SystemTimer.currTime;
        } else {
            channelContext.stat.latestTimeOfSentPacket = SystemTimer.currTime;
            handle(result, null, writeCompletionVo);
        }

    }

    @Override
    public void failed(Throwable throwable, WriteCompletionVo writeCompletionVo) {
        //		Object attachment = writeCompletionVo.getObj();
        handle(0, throwable, writeCompletionVo);
    }

    /**
     * @return the writeSemaphore
     */
    public Semaphore getWriteSemaphore() {
        return writeSemaphore;
    }

    /**
     *
     * @param result
     * @param throwable
     * @param writeCompletionVo
     *
     */
    public void handle(Integer result, Throwable throwable, WriteCompletionVo writeCompletionVo) {
        //释放写信号量
        this.writeSemaphore.release();
        Object attachment = writeCompletionVo.obj;//();
        GroupContext groupContext = channelContext.groupContext;
        boolean isSentSuccess = result > 0;

        if (isSentSuccess) {
            if (groupContext.statOn) {
                groupContext.groupStat.sentBytes.addAndGet(result);
                channelContext.stat.sentBytes.addAndGet(result);
            }

            if (groupContext.ipStats.durationList != null && groupContext.ipStats.durationList.size() > 0) {
                for (Long v : groupContext.ipStats.durationList) {
                    IpStat ipStat = (IpStat) channelContext.groupContext.ipStats.get(v, channelContext.getClientNode().getIp());
                    ipStat.getSentBytes().addAndGet(result);
                }
            }
        }

        try {
            boolean isPacket = attachment instanceof Packet;
            if (isPacket) {
                if (isSentSuccess) {
                    if (groupContext.ipStats.durationList != null && groupContext.ipStats.durationList.size() > 0) {
                        for (Long v : groupContext.ipStats.durationList) {
                            IpStat ipStat = (IpStat) channelContext.groupContext.ipStats.get(v, channelContext.getClientNode().getIp());
                            ipStat.getSentPackets().incrementAndGet();
                        }
                    }
                }
                handleOne(result, throwable, (Packet) attachment, isSentSuccess);
            } else {
                List<?> ps = (List<?>) attachment;
                for (Object obj : ps) {
                    handleOne(result, throwable, (Packet) obj, isSentSuccess);
                }
            }

            if (!isSentSuccess) {
                Tio.close(channelContext, throwable, "写数据返回:" + result);
            }
        } catch (Throwable e) {
            log.error(e.toString(), e);
        } finally {

        }
    }

    /**
     *
     * @param result
     * @param throwable
     * @param packet
     * @param isSentSuccess
     *
     */
    public void handleOne(Integer result, Throwable throwable, Packet packet, Boolean isSentSuccess) {
        Packet.Meta meta = packet.getMeta();

        if (meta != null) {
            meta.setIsSentSuccess(isSentSuccess);
        }

        try {
            channelContext.processAfterSent(packet, isSentSuccess);
        } catch (Throwable e) {
            log.error(e.toString(), e);
        }

    }

    public static class WriteCompletionVo {
        private ByteBuffer byteBuffer = null;

        private Object obj = null;

        /**
         * @param byteBuffer
         * @param obj
         *
         */
        public WriteCompletionVo(ByteBuffer byteBuffer, Object obj) {
            super();
            this.byteBuffer = byteBuffer;  //[pos=0 lim=212 cap=212]
            this.obj = obj;
        }
    }

}
