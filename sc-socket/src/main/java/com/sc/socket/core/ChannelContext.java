package com.sc.socket.core;

import com.sc.socket.core.intf.Packet;
import com.sc.socket.core.intf.Packet.Meta;
import com.sc.socket.core.ssl.SslFacadeContext;
import com.sc.socket.core.ssl.SslUtils;
import com.sc.socket.core.stat.ChannelStat;
import com.sc.socket.core.stat.IpStat;
import com.sc.socket.core.task.DecodeRunnable;
import com.sc.socket.core.task.HandlerRunnable;
import com.sc.socket.core.task.SendRunnable;
import com.sc.socket.utils.hutool.StrUtil;
import com.sc.socket.utils.lock.SetWithLock;
import com.sc.socket.utils.prop.MapWithLockPropSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 通道上下文
 * 2017年10月19日 上午9:39:46
 */
public abstract class ChannelContext extends MapWithLockPropSupport {
    public static final String UNKNOWN_ADDRESS_IP = "$UNKNOWN";
    public static final AtomicInteger UNKNOWN_ADDRESS_PORT_SEQ = new AtomicInteger();
    private static final String DEFAULT_ATTUBITE_KEY = "t-io-d-a-k";
    private static Logger log = LoggerFactory.getLogger(ChannelContext.class);

//	public boolean isTraceClient = false;

    //	public boolean isTraceSynPacket = false;
    public final ReentrantReadWriteLock closeLock = new ReentrantReadWriteLock();
    public final ChannelStat stat = new ChannelStat();

    //	private MapWithLock<String, Object> props = null;//
    public boolean isReconnect = false;
    /**
     * 一个packet所需要的字节数（用于应用告诉框架，下一次解码所需要的字节长度，省去冗余解码带来的性能损耗）
     */
    public Integer packetNeededLength = null;
    public GroupContext groupContext = null;
    public DecodeRunnable decodeRunnable = null;
    public HandlerRunnable handlerRunnable = null;
    public SendRunnable sendRunnable = null;
    public WriteCompletionHandler writeCompletionHandler = null;//new WriteCompletionHandler(this);

    public SslFacadeContext sslFacadeContext;
    public String userid;
    public boolean isWaitingClose = false;
    public boolean isClosed = true;
    public boolean isRemoved = false;
    public boolean isVirtual = false;
    /**
     * The asynchronous socket channel.
     */
    public AsynchronousSocketChannel asynchronousSocketChannel;
    public CloseMeta closeMeta = new CloseMeta();
    private ReadCompletionHandler readCompletionHandler = null;//new ReadCompletionHandler(this);
    private int reconnCount = 0;//连续重连次数，连接成功后，此值会被重置0
    private String token;
    private String bsId;
    private String id = null;

    //	private String clientNodeTraceFilename;
    private Node clientNode;

    //	private Logger traceSynPacketLog = LoggerFactory.getLogger("tio-client-trace-syn-log");
    private Node serverNode;
    /**
     * 该连接在哪些组中
     */
    private SetWithLock<String> groups = null;

    /**
     *
     * @param groupContext
     * @param asynchronousSocketChannel
     *
     */
    public ChannelContext(GroupContext groupContext, AsynchronousSocketChannel asynchronousSocketChannel) {
        super();
        init(groupContext, asynchronousSocketChannel);

        if (groupContext.sslConfig != null) {
            try {
                SslFacadeContext sslFacadeContext = new SslFacadeContext(this);
                if (groupContext.isServer()) {
                    sslFacadeContext.beginHandshake();
                }
            } catch (Exception e) {
                log.error("在开始SSL握手时发生了异常", e);
                Sio.close(this, "在开始SSL握手时发生了异常" + e.getMessage());
                return;
            }
        }
    }

    /**
     * 创建一个虚拟ChannelContext，主要用来模拟一些操作，真实场景中用得少
     */
    public ChannelContext(GroupContext groupContext) {
        isVirtual = true;
        this.groupContext = groupContext;
        Node clientNode = new Node("127.0.0.1", 26254);
        this.clientNode = clientNode;
        this.id = groupContext.getTioUuid().uuid();
    }

    private void assignAnUnknownClientNode() {
        Node clientNode = new Node(UNKNOWN_ADDRESS_IP, UNKNOWN_ADDRESS_PORT_SEQ.incrementAndGet());
        setClientNode(clientNode);
    }

    /**
     * 创建Node
     */
    public abstract Node createClientNode(AsynchronousSocketChannel asynchronousSocketChannel) throws IOException;

    /**
     *
     * @param obj
     * @return
     *
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ChannelContext other = (ChannelContext) obj;
        return Objects.equals(other.hashCode(), this.hashCode());
    }

    public Object getAttribute() {
        return getAttribute(DEFAULT_ATTUBITE_KEY);
    }

    /**
     * 设置默认属性
     */
    public void setAttribute(Object value) {
        setAttribute(DEFAULT_ATTUBITE_KEY, value);
    }

//	/**
//	 * @return the clientNodeTraceFilename
//	 */
//	public String getClientNodeTraceFilename() {
//		return clientNodeTraceFilename;
//	}

    /**
     * @return the remoteNode
     */
    public Node getClientNode() {
        return clientNode;
    }

    /**
     * @param remoteNode the remoteNode to set
     */
    private void setClientNode(Node clientNode) {
        if (!this.groupContext.isShortConnection) {
            if (this.clientNode != null) {
                groupContext.clientNodes.remove(this);
            }
        }

        this.clientNode = clientNode;
        if (this.groupContext.isShortConnection) {
            return;
        }

        if (this.clientNode != null && !Objects.equals(UNKNOWN_ADDRESS_IP, this.clientNode.getIp())) {
            groupContext.clientNodes.put(this);
//			clientNodeTraceFilename = StrUtil.replaceAll(clientNode.toString(), ":", "_");
        }
    }

    public SetWithLock<String> getGroups() {
        return groups;
    }

    public void setGroups(SetWithLock<String> groups) {
        this.groups = groups;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the readCompletionHandler
     */
    public ReadCompletionHandler getReadCompletionHandler() {
        return readCompletionHandler;
    }

    /**
     * @return the reConnCount
     */
    public int getReconnCount() {
        return reconnCount;
    }

    /**
     * @param reConnCount the reConnCount to set
     */
    public void setReconnCount(int reconnCount) {
        this.reconnCount = reconnCount;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    /**
     * @return the writeCompletionHandler
     */
    public WriteCompletionHandler getWriteCompletionHandler() {
        return writeCompletionHandler;
    }

    /**
     *
     * @return
     *
     */
    @Override
    public int hashCode() {
        if (StrUtil.isNotBlank(id)) {
            return this.id.hashCode();
        } else {
            return super.hashCode();
        }
    }

    public void init(GroupContext groupContext, AsynchronousSocketChannel asynchronousSocketChannel) {
        id = groupContext.getTioUuid().uuid();
        this.setGroupContext(groupContext);
        groupContext.ids.bind(this);
        this.setAsynchronousSocketChannel(asynchronousSocketChannel);
        this.readCompletionHandler = new ReadCompletionHandler(this);
        this.writeCompletionHandler = new WriteCompletionHandler(this);
    }

    /**
     *
     * @param packet
     * @param isSentSuccess
     *
     */
    public void processAfterSent(Packet packet, Boolean isSentSuccess) {
        isSentSuccess = isSentSuccess == null ? false : isSentSuccess;
        Meta meta = packet.getMeta();
        if (meta != null) {
            CountDownLatch countDownLatch = meta.getCountDownLatch();
//			traceBlockPacket(SynPacketAction.BEFORE_DOWN, packet, countDownLatch, null);
            countDownLatch.countDown();
        }

        try {
            if (log.isDebugEnabled()) {
                log.debug("{} 已经发送 {}", this, packet.logstr());
            }

            //非SSL or SSL已经握手
            if (this.sslFacadeContext == null || this.sslFacadeContext.isHandshakeCompleted()) {
                if (groupContext.getAioListener() != null) {
                    try {
                        groupContext.getAioListener().onAfterSent(this, packet, isSentSuccess);
                    } catch (Exception e) {
                        log.error(e.toString(), e);
                    }
                }

                if (groupContext.statOn) {
                    groupContext.groupStat.sentPackets.incrementAndGet();
                    stat.sentPackets.incrementAndGet();
                }

                if (groupContext.ipStats.durationList != null && groupContext.ipStats.durationList.size() > 0) {
                    try {
                        for (Long v : groupContext.ipStats.durationList) {
                            IpStat ipStat = groupContext.ipStats.get(v, getClientNode().getIp());
                            ipStat.getSentPackets().incrementAndGet();
                            groupContext.getIpStatListener().onAfterSent(this, packet, isSentSuccess, ipStat);
                        }
                    } catch (Exception e) {
                        log.error(e.toString(), e);
                    }
                }
            }
        } catch (Throwable e) {
            log.error(e.toString(), e);
        }

        if (packet.getPacketListener() != null) {
            try {
                packet.getPacketListener().onAfterSent(this, packet, isSentSuccess);
            } catch (Throwable e) {
                log.error(e.toString(), e);
            }
        }

    }

    /**
     * @param asynchronousSocketChannel the asynchronousSocketChannel to set
     */
    public void setAsynchronousSocketChannel(AsynchronousSocketChannel asynchronousSocketChannel) {
        this.asynchronousSocketChannel = asynchronousSocketChannel;

        if (asynchronousSocketChannel != null) {
            try {
                Node clientNode = createClientNode(asynchronousSocketChannel);
                setClientNode(clientNode);
            } catch (IOException e) {
                log.info(e.toString(), e);
                assignAnUnknownClientNode();
            }
        } else {
            assignAnUnknownClientNode();
        }
    }

    /**
     * @param isClosed the isClosed to set
     */
    public void setClosed(boolean isClosed) {
        this.isClosed = isClosed;
        if (isClosed) {
            if (clientNode == null || !UNKNOWN_ADDRESS_IP.equals(clientNode.getIp())) {
                String before = this.toString();
                assignAnUnknownClientNode();
                log.info("关闭前{}, 关闭后{}", before, this);
            }
        }
    }

    public void setPacketNeededLength(Integer packetNeededLength) {
        this.packetNeededLength = packetNeededLength;
    }

    public void setReconnect(boolean isReconnect) {
        this.isReconnect = isReconnect;
    }

    /**
     * @param isRemoved the isRemoved to set
     */
    public void setRemoved(boolean isRemoved) {
        this.isRemoved = isRemoved;
    }

    public void setSslFacadeContext(SslFacadeContext sslFacadeContext) {
        this.sslFacadeContext = sslFacadeContext;
    }

    /**
     * @param userid the userid to set 给框架内部用的，用户请勿调用此方法
     */
    public void setUserid(String userid) {
        this.userid = userid;
    }


    @Override
    public String toString() {
        if (isVirtual) {
            return this.getClientNode().toString();
        }

        if (SslUtils.isSsl(groupContext)) {
            return this.getClientNode().toString() + ", SslShakehanded:" + this.sslFacadeContext.isHandshakeCompleted();
        } else {
            return this.getClientNode().toString();
        }
    }

    /**
     * @return the bsId
     */
    public String getBsId() {
        return bsId;
    }


    /**
     * @param bsId the bsId to set
     */
    public void setBsId(String bsId) {
        this.bsId = bsId;
    }

    public GroupContext getGroupContext() {
        return groupContext;
    }

    /**
     * @param groupContext the groupContext to set
     */
    public void setGroupContext(GroupContext groupContext) {
        this.groupContext = groupContext;

        if (groupContext != null) {
            decodeRunnable = new DecodeRunnable(this, groupContext.tioExecutor);
            handlerRunnable = new HandlerRunnable(this, groupContext.tioExecutor);
            sendRunnable = new SendRunnable(this, groupContext.tioExecutor);
            groupContext.connections.add(this);
        }
    }

    /**
     * 是否是服务器端
     */
    public abstract boolean isServer();

    /**
     * 关闭元信息
     */
    public static class CloseMeta {
        public Throwable throwable;
        public String remark;
        public boolean isNeedRemove;

        public Throwable getThrowable() {
            return throwable;
        }

        public void setThrowable(Throwable throwable) {
            this.throwable = throwable;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public boolean isNeedRemove() {
            return isNeedRemove;
        }

        public void setNeedRemove(boolean isNeedRemove) {
            this.isNeedRemove = isNeedRemove;
        }


    }
}
