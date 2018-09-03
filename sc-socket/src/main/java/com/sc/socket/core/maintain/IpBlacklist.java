package com.sc.socket.core.maintain;

import com.sc.socket.core.Tio;
import com.sc.socket.utils.SystemTimer;
import com.sc.socket.utils.cache.caffeine.CaffeineCache;
import com.sc.socket.utils.time.Time;
import com.sc.socket.core.GroupContext;

import java.util.Collection;

/**
 *
 *
 * 2017年5月22日 下午2:53:47
 */
public class IpBlacklist {
	private String id;

	private final static String CACHE_NAME = "SOCKET_IP_BLACK_LIST";
	private final static Long TIME_TO_LIVE_SECONDS = Time.MINUTE_1 * 120;
	private final static Long TIME_TO_IDLE_SECONDS = null;

	private String cacheName = null;
	private CaffeineCache cache = null;
	
	private GroupContext groupContext;

	public IpBlacklist(String id, GroupContext groupContext) {
		this.id = id;
		this.groupContext = groupContext;
		this.cacheName = CACHE_NAME + this.id;
		this.cache = CaffeineCache.register(this.cacheName, TIME_TO_LIVE_SECONDS, TIME_TO_IDLE_SECONDS, null);
	}

	
	public boolean add(String ip) {
		//先添加到黑名单列表
		cache.put(ip, SystemTimer.currTime);

		//再删除相关连接
		Tio.remove(groupContext, ip, "ip[" + ip + "]被加入了黑名单");
		return true;
	}

	public void clear() {
		cache.clear();
	}

	public Collection<String> getAll() {
		return cache.keys();
	}

	/**
	 * 是否在黑名单中
	 * @param ip
	 * @return
	 *
	 */
	public boolean isInBlacklist(String ip) {
		return cache.get(ip) != null;
	}

	/**
	 * 从黑名单中删除
	 * @param ip
	 * @return
	 *
	 */
	public void remove(String ip) {
		cache.remove(ip);
	}
}
