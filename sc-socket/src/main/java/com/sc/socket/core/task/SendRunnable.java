package com.sc.socket.core.task;

import com.sc.socket.core.ChannelContext;
import com.sc.socket.core.GroupContext;
import com.sc.socket.core.TcpConst;
import com.sc.socket.core.Tio;
import com.sc.socket.core.WriteCompletionHandler.WriteCompletionVo;
import com.sc.socket.core.intf.AioHandler;
import com.sc.socket.core.intf.Packet;
import com.sc.socket.core.ssl.SslUtils;
import com.sc.socket.core.ssl.SslVo;
import com.sc.socket.core.utils.TioUtils;
import com.sc.socket.utils.thread.pool.AbstractQueueRunnable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

import javax.net.ssl.SSLException;

/**
 * 发送处理,队列异步线程发送
 * 2017年4月4日 上午9:19:18
 */
public class SendRunnable extends AbstractQueueRunnable<Packet> {

    private static final Logger log = LoggerFactory.getLogger(SendRunnable.class);
    private static final int MAX_CAPACITY = TcpConst.MAX_DATA_LENGTH - 1024; //减掉1024是尽量防止溢出的一小部分还分成一个tcp包发出
    /**
     * The msg queue.
     */
    private ConcurrentLinkedQueue<Packet> forSendAfterSslHandshakeCompleted = null;//new ConcurrentLinkedQueue<>();
    private ChannelContext channelContext = null;

    private GroupContext groupContext = null;

    private AioHandler aioHandler = null;

    private boolean isSsl = false;

    //SSL加密锁
    //	private Object sslEncryptLock = new Object();

    /**
     *
     * @param channelContext
     * @param executor
     *
     */
    public SendRunnable(ChannelContext channelContext, Executor executor) {
        super(executor);
        this.channelContext = channelContext;
        this.groupContext = channelContext.groupContext;
        this.aioHandler = groupContext.getAioHandler();
        this.isSsl = SslUtils.isSsl(groupContext);
    }

    /**
     * 新旧值是否进行了切换
     */
    private static boolean swithed(Boolean oldValue, boolean newValue) {
        if (oldValue == null) {
            return false;
        }

        if (Objects.equals(oldValue, newValue)) {
            return false;
        }

        return true;
    }

    public ConcurrentLinkedQueue<Packet> getForSendAfterSslHandshakeCompleted(boolean forceCreate) {
        if (forSendAfterSslHandshakeCompleted == null && forceCreate) {
            synchronized (this) {
                if (forSendAfterSslHandshakeCompleted == null) {
                    forSendAfterSslHandshakeCompleted = new ConcurrentLinkedQueue<>();
                }
            }
        }

        return forSendAfterSslHandshakeCompleted;
    }

    @Override
    public boolean addMsg(Packet packet) {
        if (this.isCanceled()) {
            log.error("{}, 任务已经取消，{}添加到发送队列失败", channelContext, packet);
            return false;
        }
        if (channelContext.sslFacadeContext != null && !channelContext.sslFacadeContext.isHandshakeCompleted() && SslUtils.needSslEncrypt(packet, groupContext)) {
            return this.getForSendAfterSslHandshakeCompleted(true).add(packet);
        } else {
            return msgQueue.add(packet);
        }
    }

    /**
     * 清空消息队列
     */
    @Override
    public void clearMsgQueue() {
        Packet p = null;
        forSendAfterSslHandshakeCompleted = null;
        while ((p = msgQueue.poll()) != null) {
            try {
                channelContext.processAfterSent(p, false);
            } catch (Throwable e) {
                log.error(e.toString(), e);
            }
        }
    }

    private ByteBuffer getByteBuffer(Packet packet) {
        try {
            ByteBuffer byteBuffer = packet.getPreEncodedByteBuffer();
            if (byteBuffer != null) {
                //			byteBuffer = byteBuffer.duplicate();
            } else {
                byteBuffer = aioHandler.encode(packet, groupContext, channelContext);
            }

            if (!byteBuffer.hasRemaining()) {
                byteBuffer.flip();
            }
            return byteBuffer;
        } catch (Exception e) {
            log.error(packet.logstr(), e);
            throw new RuntimeException(e);
        }
    }

    //	private int repeatCount = 0;

    @Override
    public void runTask() {
        int queueSize = msgQueue.size();
        if (queueSize == 0) {
            return;
        }

        if (queueSize == 1) {
            //			System.out.println(1);
            sendPacket(msgQueue.poll());
            return;
        }

        int listInitialCapacity = Math.min(queueSize, 200);

        Packet packet = null;
        List<Packet> packets = new ArrayList<>(listInitialCapacity);
        List<ByteBuffer> byteBuffers = new ArrayList<>(listInitialCapacity);
        //		int packetCount = 0;
        int allBytebufferCapacity = 0;
        Boolean needSslEncrypted = null;
        boolean sslSwitched = false;
        while ((packet = msgQueue.poll()) != null) {
            ByteBuffer byteBuffer = getByteBuffer(packet);

            packets.add(packet);
            byteBuffers.add(byteBuffer);
            //			packetCount++;
            allBytebufferCapacity += byteBuffer.limit();

            if (isSsl) {
                if (packet.isSslEncrypted()) {
                    boolean _needSslEncrypted = false;
                    sslSwitched = swithed(needSslEncrypted, _needSslEncrypted);
                    needSslEncrypted = _needSslEncrypted;
                } else {
                    boolean _needSslEncrypted = true;
                    sslSwitched = swithed(needSslEncrypted, _needSslEncrypted);
                    needSslEncrypted = _needSslEncrypted;
                }
            } else { //非ssl，不涉及到加密和不加密的切换
                needSslEncrypted = false;
            }

            if ((allBytebufferCapacity >= MAX_CAPACITY) || sslSwitched) {
                break;
            }
        }

        if (allBytebufferCapacity == 0) {
            return;
        }
        ByteBuffer allByteBuffer = ByteBuffer.allocate(allBytebufferCapacity);
        for (ByteBuffer byteBuffer : byteBuffers) {
            allByteBuffer.put(byteBuffer);
        }

        allByteBuffer.flip();

        if (needSslEncrypted) {
            SslVo sslVo = new SslVo(allByteBuffer, packets);
            try {
                channelContext.sslFacadeContext.getSslFacade().encrypt(sslVo);
                allByteBuffer = sslVo.getByteBuffer();
            } catch (SSLException e) {
                log.error(channelContext.toString() + ", 进行SSL加密时发生了异常", e);
                Tio.close(channelContext, "进行SSL加密时发生了异常");
                return;
            }
        }

        this.sendByteBuffer(allByteBuffer, packets);
        //		queueSize = msgQueue.size();
        //		if (queueSize > 0) {
        //			repeatCount++;
        //			if (repeatCount < 3) {
        //				runTask();
        //				return;
        //			}
        //		}
        //		repeatCount = 0;
    }

    public boolean sendPacket(Packet packet) {
        ByteBuffer byteBuffer = getByteBuffer(packet);

        if (isSsl) {
            if (!packet.isSslEncrypted()) {
                SslVo sslVo = new SslVo(byteBuffer, packet);
                try {
                    channelContext.sslFacadeContext.getSslFacade().encrypt(sslVo);
                    byteBuffer = sslVo.getByteBuffer();
                } catch (SSLException e) {
                    log.error(channelContext.toString() + ", 进行SSL加密时发生了异常", e);
                    Tio.close(channelContext, "进行SSL加密时发生了异常");
                    return false;
                }
            }
        }

        sendByteBuffer(byteBuffer, packet);
        return true;
    }

    /**
     * @param packets Packet or List<Packet>
     */
    public void sendByteBuffer(ByteBuffer byteBuffer, Object packets) {
        if (byteBuffer == null) {
            log.error("{},byteBuffer is null", channelContext);
            return;
        }

        if (!TioUtils.checkBeforeIO(channelContext)) {
            return;
        }

        if (!byteBuffer.hasRemaining()) {
            byteBuffer.flip();
        }

        try {
            //获取写信号量许可
            channelContext.writeCompletionHandler.getWriteSemaphore().acquire();
        } catch (InterruptedException e) {
            log.error(e.toString(), e);
        }

        write(byteBuffer, packets);
    }

    private void write(ByteBuffer byteBuffer, Object packets) {
        WriteCompletionVo writeCompletionVo = new WriteCompletionVo(byteBuffer, packets);
        //通道写消息
        channelContext.asynchronousSocketChannel.write(byteBuffer, writeCompletionVo, channelContext.writeCompletionHandler);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ":" + channelContext.toString();
    }

    /**
     * @return
     *
     */
    @Override
    public String logstr() {
        return toString();
    }

}
