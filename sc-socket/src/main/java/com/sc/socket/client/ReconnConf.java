package com.sc.socket.client;

import com.sc.socket.core.ChannelContext;
import com.sc.socket.utils.SystemTimer;
import com.sc.socket.utils.thread.pool.DefaultThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 客户端重新连接配置
 * 2017年4月1日 上午9:33:00
 */
public class ReconnConf {
    private static Logger log = LoggerFactory.getLogger(ChannelContext.class);
    LinkedBlockingQueue<ChannelContext> queue = new LinkedBlockingQueue<>();
    /**
     * 重连的间隔时间，单位毫秒
     */
    private long interval = 5000;
    /**
     * 连续重连次数，当连续重连这么多次都失败时，不再重连。0和负数则一直重连
     */
    private int retryCount = 0;
    /**
     * 用来重连的线程池
     */
    private ThreadPoolExecutor threadPoolExecutor = null;

    public ReconnConf() {
        if (threadPoolExecutor == null) {
            synchronized (ReconnConf.class) {
                if (threadPoolExecutor == null) {
                    threadPoolExecutor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors(), 60L, TimeUnit.SECONDS,
                            new LinkedBlockingQueue<Runnable>(), DefaultThreadFactory.getInstance("tio-client-reconn"));
                }
            }

        }

    }


    public ReconnConf(long interval) {
        this();
        this.setInterval(interval);
    }


    public ReconnConf(long interval, int retryCount) {
        this();
        this.interval = interval;
        this.retryCount = retryCount;
    }

    public static boolean isNeedReconn(ClientChannelContext clientChannelContext, boolean putIfTrue) {
        ClientGroupContext clientGroupContext = (ClientGroupContext) clientChannelContext.groupContext;
        ReconnConf reconnConf = clientGroupContext.getReconnConf();
        if (reconnConf != null && reconnConf.getInterval() > 0) {
            if (reconnConf.getRetryCount() <= 0 || reconnConf.getRetryCount() >= clientChannelContext.getReconnCount()) {
                if (putIfTrue) {
                    clientChannelContext.stat.timeInReconnQueue = SystemTimer.currTime;
                    reconnConf.getQueue().add(clientChannelContext);
                }
                return true;
            } else {
                log.info("不需要重连{}", clientChannelContext);
                return false;
            }
        }

        return false;
    }

    public static void put(ClientChannelContext clientChannelContext) {
        isNeedReconn(clientChannelContext, true);
    }

    /**
     * @return the interval
     */
    public long getInterval() {
        return interval;
    }

    /**
     * @param interval the interval to set
     */
    public void setInterval(long interval) {
        this.interval = interval;
    }

    /**
     * @return the queue
     */
    public LinkedBlockingQueue<ChannelContext> getQueue() {
        return queue;
    }

    /**
     * @return the retryCount
     */
    public int getRetryCount() {
        return retryCount;
    }

    /**
     * @param retryCount the retryCount to set
     */
    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    /**
     * @return the threadPoolExecutor
     */
    public ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }


}
