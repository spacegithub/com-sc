package com.sc.socket.core;

import com.sc.socket.core.intf.AioHandler;
import com.sc.socket.core.intf.AioListener;
import com.sc.socket.core.intf.GroupListener;
import com.sc.socket.core.intf.Packet;
import com.sc.socket.core.intf.SioUuid;
import com.sc.socket.core.maintain.BsIds;
import com.sc.socket.core.maintain.ClientNodes;
import com.sc.socket.core.maintain.Groups;
import com.sc.socket.core.maintain.Ids;
import com.sc.socket.core.maintain.IpBlacklist;
import com.sc.socket.core.maintain.IpStats;
import com.sc.socket.core.maintain.Ips;
import com.sc.socket.core.maintain.Tokens;
import com.sc.socket.core.maintain.Users;
import com.sc.socket.core.ssl.SslConfig;
import com.sc.socket.core.stat.DefaultIpStatListener;
import com.sc.socket.core.stat.GroupStat;
import com.sc.socket.core.stat.IpStatListener;
import com.sc.socket.core.task.CloseRunnable;
import com.sc.socket.utils.SystemTimer;
import com.sc.socket.utils.Threads;
import com.sc.socket.utils.lock.MapWithLock;
import com.sc.socket.utils.lock.SetWithLock;
import com.sc.socket.utils.prop.MapWithLockPropSupport;
import com.sc.socket.utils.thread.pool.SynThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 客户端和服务端的上下文抽象包括一些常用的配置和统计信息
 *
 * 2016年10月10日 下午5:25:43
 */
public abstract class GroupContext extends MapWithLockPropSupport {
	static Logger log = LoggerFactory.getLogger(GroupContext.class);
	/**
	 * 默认的接收数据的buffer size,如果系统中有default.read.buffer.size的设置则取系统中的value,否则为默认值(第二个参数)
	 */
	public static final int READ_BUFFER_SIZE = Integer.getInteger("default.read.buffer.size", 2048);

    /**
     * 全局线程ID自增
     */
	private final static AtomicInteger ID_ATOMIC = new AtomicInteger();

    /**
     * 默认为大端排序
     */
	private ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;

    /**
     * 是否为端连接
     */
	public boolean isShortConnection = false;
    /**
     * SSL连接配置
     */
	public SslConfig sslConfig = null;

	public boolean debug = false;
    /**
     * 组统计
     */
	public GroupStat groupStat = null;

    /**
     * 默认记录消息组信息
     */
	public boolean statOn = true;

	/**
	 * 启动时间
	 */
	public long startTime = SystemTimer.currTime;

	/**
	 * 是否用队列发送
	 */
	public boolean useQueueSend = true;

	/**
	 *  是否用队列解码（系统初始化时确定该值，中途不要变更此值，否则在切换的时候可能导致消息丢失）
	 */
	public boolean useQueueDecode = false;

	/**
	 * 心跳超时时间(单位: 毫秒)，如果用户不希望框架层面做心跳相关工作，请把此值设为0或负数
	 */
	public long heartbeatTimeout = 1000 * 120;


    /**
     * 消息处理模式
     * 1.SINGLE_THREAD  处理消息与解码在同一个线程中处理
     * 2.QUEUE 把packet丢到一个队列中，让线程池去处理
     */
	public PacketHandlerMode packetHandlerMode = PacketHandlerMode.SINGLE_THREAD;//.queue;

	/**
	 * 接收数据的buffer size
	 */
	private int readBufferSize = READ_BUFFER_SIZE;

    /**
     * 消息组监听器
     */
	private GroupListener groupListener = null;

	private SioUuid tioUuid = new DefaultSioUuid();
    /**
     * 异步线程池
     */
	public SynThreadPoolExecutor tioExecutor = null;

    /**
     * 关闭连接任务
     */
	public CloseRunnable closeRunnable;

	public ThreadPoolExecutor groupExecutor = null;
    /**
     * 客户端节点
     */
	public final ClientNodes clientNodes = new ClientNodes();

    /**
     * 自带读写锁的set集合
     */
	public final SetWithLock<ChannelContext> connections = new SetWithLock<ChannelContext>(new HashSet<ChannelContext>());
    /**
     * 组,多对多
     */
	public final Groups groups = new Groups();
    /**
     * 用户,一对多
     */
	public final Users users = new Users();
    /**
     * 凭证,一对多
     */
	public final Tokens tokens = new Tokens();
    /**
     *编号,-对-
     */
	public final Ids ids = new Ids();
    /**
     * 业务编号,一对一
     */
	public final BsIds bsIds = new BsIds();
    /**
     * ip,一对多  一个id有哪些客户端,改只在服务端维护
     */
	public final Ips ips = new Ips();
    /**
     * ip统计
     */
	public IpStats ipStats = null;

	/**
	 * ip黑名单
	 */
	public IpBlacklist ipBlacklist = null;//new IpBlacklist();
    /**
     * 等待包
     */
	public final MapWithLock<Integer, Packet> waitingResps = new MapWithLock<Integer, Packet>(new HashMap<Integer, Packet>());

	/**
	 * packet编码成bytebuffer时，是否与ChannelContext相关，false: packet编码与ChannelContext无关
	 */
	//	private boolean isEncodeCareWithChannelContext = true;

	protected String id;

	/**
	 * 解码异常多少次就把ip拉黑
	 */
	protected int maxDecodeErrorCountForIp = 10;

	protected String name = "未命名GroupContext";
    /**
     * ip统计监听器
     */
	private IpStatListener ipStatListener = DefaultIpStatListener.me;

	private boolean isStopped = false;



	public GroupContext() {
		this(null, null);
	}

	/**
	 *
	 * @param tioExecutor
	 * @param groupExecutor
	 *
	 */
	public GroupContext(SynThreadPoolExecutor tioExecutor, ThreadPoolExecutor groupExecutor) {
		super();
		this.id = ID_ATOMIC.incrementAndGet() + "";
		this.ipBlacklist = new IpBlacklist(id, this);
		this.ipStats = new IpStats(this, null);


		this.tioExecutor = tioExecutor;
		if (this.tioExecutor == null) {
			this.tioExecutor = Threads.getTioExecutor();
		}

		this.groupExecutor = groupExecutor;
		if (this.groupExecutor == null) {
			this.groupExecutor = Threads.getGroupExecutor();
		}

		closeRunnable = new CloseRunnable(this.tioExecutor);
	}


//	/**
//	 * 
//	 * @param tioClusterConfig
//	 * @param tioExecutor
//	 * @param groupExecutor
//	 *
//	 */
//	public GroupContext(TioClusterConfig tioClusterConfig, SynThreadPoolExecutor tioExecutor, ThreadPoolExecutor groupExecutor) {
//		this(tioExecutor, groupExecutor);
//		this.setTioClusterConfig(tioClusterConfig);
//	}

	/**
	 * 获取AioHandler对象
	 * @return
	 *
	 */
	public abstract AioHandler getAioHandler();

	/**
	 * 获取AioListener对象
	 * @return
	 *
	 */
	public abstract AioListener getAioListener();



	/**
	 *
	 * @return
	 *
	 */
	public ByteOrder getByteOrder() {
		return byteOrder;
	}

	/**
	 * @return the groupListener
	 */
	public GroupListener getGroupListener() {
		return groupListener;
	}

//	/**
//	 * 获取GroupStat对象
//	 * @return
//	 *
//	 */
//	public abstract GroupStat groupStat;

	/**
	 *
	 * @return
	 *
	 */
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}



	/**
	 * @return the tioUuid
	 */
	public SioUuid getTioUuid() {
		return tioUuid;
	}

	/**
	 * @return the syns
	 */
	public MapWithLock<Integer, Packet> getWaitingResps() {
		return waitingResps;
	}

	/**
	 * @return the isEncodeCareWithChannelContext
	 */
	//	public boolean isEncodeCareWithChannelContext() {
	//		return isEncodeCareWithChannelContext;
	//	}

//	/**
//	 * @return the isShortConnection
//	 */
//	public boolean isShortConnection {
//		return isShortConnection;
//	}

	/**
	 * @return the isStop
	 */
	public boolean isStopped() {
		return isStopped;
	}

	/**
	 *
	 * @param byteOrder
	 *
	 */
	public void setByteOrder(ByteOrder byteOrder) {
		this.byteOrder = byteOrder;
	}

	/**
	 * @param isEncodeCareWithChannelContext the isEncodeCareWithChannelContext to set
	 */
	//	public void setEncodeCareWithChannelContext(boolean isEncodeCareWithChannelContext) {
	//		this.isEncodeCareWithChannelContext = isEncodeCareWithChannelContext;
	//	}

	/**
	 * @param groupListener the groupListener to set
	 */
	public void setGroupListener(GroupListener groupListener) {
		this.groupListener = groupListener;
	}

	/**
	 * @param heartbeatTimeout the heartbeatTimeout to set
	 */
	public void setHeartbeatTimeout(long heartbeatTimeout) {
		this.heartbeatTimeout = heartbeatTimeout;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param packetHandlerMode the packetHandlerMode to set
	 */
	public void setPacketHandlerMode(PacketHandlerMode packetHandlerMode) {
		this.packetHandlerMode = packetHandlerMode;
	}

	/**
	 * @param readBufferSize the readBufferSize to set
	 */
	public void setReadBufferSize(int readBufferSize) {
		this.readBufferSize = Math.min(readBufferSize, TcpConst.MAX_DATA_LENGTH);
	}

	/**
	 * @param isShortConnection the isShortConnection to set
	 */
	public void setShortConnection(boolean isShortConnection) {
		this.isShortConnection = isShortConnection;
	}


	public void setStopped(boolean isStopped) {
		this.isStopped = isStopped;
	}

	/**
	 * @param tioUuid the tioUuid to set
	 */
	public void setTioUuid(SioUuid tioUuid) {
		this.tioUuid = tioUuid;
	}


	public void setSslConfig(SslConfig sslConfig) {
		this.sslConfig = sslConfig;
	}

	public IpStatListener getIpStatListener() {
		return ipStatListener;
	}

	public void setIpStatListener(IpStatListener ipStatListener) {
		this.ipStatListener = ipStatListener;
		//		this.ipStats.setIpStatListener(ipStatListener);
	}

	public GroupStat getGroupStat() {
		return groupStat;
	}

	/**
	 * 是否用队列解码（系统初始化时确定该值，中途不要变更此值，否则在切换的时候可能导致消息丢失
	 * @param useQueueDecode
	 *
	 */
	public void setUseQueueDecode(boolean useQueueDecode) {
		this.useQueueDecode = useQueueDecode;
	}

	/**
	 * 是否用队列发送，可以随时切换
	 * @param useQueueSend
	 *
	 */
	public void setUseQueueSend(boolean useQueueSend) {
		this.useQueueSend = useQueueSend;
	}

	/**
	 * 是服务器端还是客户端
	 * @return
	 *
	 */
	public abstract boolean isServer();

	public int getReadBufferSize() {
		return readBufferSize;
	}
}
