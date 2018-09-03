package com.sc.socket.server;

import com.sc.socket.core.stat.GroupStat;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 组相关统计
 */
public class ServerGroupStat extends GroupStat {

    private static final long serialVersionUID = -139100692961946342L;
    /**
     * 接受了多少连接
     */
    public final AtomicLong accepted = new AtomicLong();

    /**
     * 2016年12月3日 下午2:29:28
     */
    public ServerGroupStat() {
    }

    /**
     * @return the accepted
     */
    public AtomicLong getAccepted() {
        return accepted;
    }
}
