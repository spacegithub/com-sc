package com.sc.socket.core.maintain;

import com.sc.socket.core.GroupContext;
import com.sc.socket.core.cache.IpStatRemovalListener;
import com.sc.socket.core.stat.IpStat;
import com.sc.socket.utils.cache.caffeine.CaffeineCache;
import com.sc.socket.utils.hutool.StrUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * 使用方法（注意顺序）：<br>
 *	1、serverGroupContext.setIpStatListener(ShowcaseIpStatListener.me);
	2、serverGroupContext.ipStats.addDuration(Time.MINUTE_1 * 5);
 *
 * 2017年4月15日 下午12:13:19
 */
public class IpStats {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(IpStats.class);

	private final static String CACHE_NAME = "SOCKET_IP_STAT";

	private String groupContextId;
	private GroupContext groupContext;

	/**
	 * key: 时长，单位：秒
	 */
	public final Map<Long, CaffeineCache> cacheMap = new HashMap<>();

	public List<Long> durationList = null;//new ArrayList<>();

	public IpStats(GroupContext groupContext, Long[] durations) {
		this.groupContext = groupContext;
		this.groupContextId = groupContext.getId();
		if (durations != null) {
			addDurations(durations);
		}
	}

	/**
	 * 添加监控时段，不要添加过多的时间段，因为每个时间段都要消耗一份内存，一般加一个时间段就可以了
	 * @param duration 单位：秒
	 *
	 */
	public void addDuration(Long duration) {
		synchronized (this) {
			if (durationList == null) {
				durationList = new ArrayList<>();
			}
			@SuppressWarnings("unchecked")
			CaffeineCache caffeineCache = CaffeineCache.register(getCacheName(duration), duration, null, new IpStatRemovalListener(groupContext, groupContext.getIpStatListener()));
			cacheMap.put(duration, caffeineCache);
			durationList.add(duration);
		}
	}

	/**
	 * 添加监控时段，不要添加过多的时间段，因为每个时间段都要消耗一份内存，一般加一个时间段就可以了
	 * @param durations 单位：秒
	 *
	 */
	public void addDurations(Long[] durations) {
		if (durations != null) {
			for (Long duration : durations) {
				addDuration(duration);
			}
		}
	}

	/**
	 * 删除监控时间段
	 * @param duration
	 *
	 */
	public void removeDuration(Long duration) {
		clear(duration);
		cacheMap.remove(duration);

		if (durationList != null) {
			durationList.remove(duration);
		}
	}

	/**
	 * 
	 * @param duration
	 * @return
	 *
	 */
	public String getCacheName(Long duration) {
		String cacheName = CACHE_NAME + "_" + this.groupContextId + "_";
		return cacheName + duration;
	}

	/**
	 * 清空监控数据
	 *
	 */
	public void clear(Long duration) {
		CaffeineCache caffeineCache = cacheMap.get(duration);
		if (caffeineCache == null) {
			return;
		}
		caffeineCache.clear();
	}

	/**
	 * 根据ip获取IpStat，如果缓存中不存在，则创建
	 * @param duration
	 * @param ip
	 * @return
	 *
	 */
	public IpStat get(Long duration, String ip) {
		return get(duration, ip, true);
	}

	/**
	 * 根据ip获取IpStat，如果缓存中不存在，则根据forceCreate的值决定是否创建
	 * @param duration
	 * @param ip
	 * @param forceCreate
	 * @return
	 *
	 */
	public IpStat get(Long duration, String ip, boolean forceCreate) {
		if (StrUtil.isBlank(ip)) {
			return null;
		}
		CaffeineCache caffeineCache = cacheMap.get(duration);
		if (caffeineCache == null) {
			return null;
		}

		IpStat ipStat = (IpStat) caffeineCache.get(ip);
		if (ipStat == null && forceCreate) {
			synchronized (this) {
				ipStat = (IpStat) caffeineCache.get(ip);
				if (ipStat == null) {
					ipStat = new IpStat(ip, duration);
					caffeineCache.put(ip, ipStat);
				}
			}
		}
		return ipStat;
	}

	/**
	 *
	 * @return
	 *
	 */
	public ConcurrentMap<String, Serializable> map(Long duration) {
		CaffeineCache caffeineCache = cacheMap.get(duration);
		if (caffeineCache == null) {
			return null;
		}
		ConcurrentMap<String, Serializable> map = caffeineCache.asMap();
		return map;
	}

	/**
	 *
	 * @return
	 *
	 */
	public Long size(Long duration) {
		CaffeineCache caffeineCache = cacheMap.get(duration);
		if (caffeineCache == null) {
			return null;
		}
		return caffeineCache.size();
	}

	/**
	 *
	 * @return
	 *
	 */
	public Collection<Serializable> values(Long duration) {
		CaffeineCache caffeineCache = cacheMap.get(duration);
		if (caffeineCache == null) {
			return null;
		}
		Collection<Serializable> set = caffeineCache.asMap().values();
		return set;
	}
}
