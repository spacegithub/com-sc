package com.sc.socket.utils.cache.guava;

import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;

import com.sc.socket.utils.cache.ICache;
import com.sc.socket.utils.hutool.StrUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sc.socket.utils.guava.GuavaUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 *
 *
 * 2017年8月5日 上午10:16:26
 */
public class GuavaCache implements ICache {
	private static Logger log = LoggerFactory.getLogger(GuavaCache.class);

	public static Map<String, GuavaCache> map = new HashMap<>();
	
	public static GuavaCache getCache(String cacheName) {
		GuavaCache guavaCache = map.get(cacheName);
		if (guavaCache == null) {
			log.error("cacheName[{}]还没注册，请初始化时调用：{}.register(cacheName, timeToLiveSeconds, timeToIdleSeconds)", cacheName, GuavaCache.class.getSimpleName());
		}
		return guavaCache;
	}

	/**
	 * timeToLiveSeconds和timeToIdleSeconds不允许同时为null
	 * @param cacheName
	 * @param timeToLiveSeconds
	 * @param timeToIdleSeconds
	 * @return
	 *
	 */
	public static GuavaCache register(String cacheName, Long timeToLiveSeconds, Long timeToIdleSeconds) {
		GuavaCache guavaCache = register(cacheName, timeToLiveSeconds, timeToIdleSeconds, null);
		return guavaCache;
	}

	public static GuavaCache register(String cacheName, Long timeToLiveSeconds, Long timeToIdleSeconds, RemovalListener<String, Serializable> removalListener) {
		GuavaCache guavaCache = map.get(cacheName);
		if (guavaCache == null) {
			synchronized (GuavaCache.class) {
				guavaCache = map.get(cacheName);
				if (guavaCache == null) {
					Integer concurrencyLevel = 8;
					Integer initialCapacity = 10;
					Integer maximumSize = 5000000;
					boolean recordStats = false;
					LoadingCache<String, Serializable> loadingCache = GuavaUtils.createLoadingCache(concurrencyLevel, timeToLiveSeconds, timeToIdleSeconds, initialCapacity,
							maximumSize, recordStats, removalListener);
					
					Integer temporaryMaximumSize = 500000;
					LoadingCache<String, Serializable> temporaryLoadingCache = GuavaUtils.createLoadingCache(concurrencyLevel, 10L, (Long)null, initialCapacity,
							temporaryMaximumSize, recordStats, removalListener);
					guavaCache = new GuavaCache(loadingCache, temporaryLoadingCache);
					map.put(cacheName, guavaCache);
				}
			}
		}
		return guavaCache;
	}

	//

	private LoadingCache<String, Serializable> loadingCache = null;
	
	private LoadingCache<String, Serializable> temporaryLoadingCache = null;

	private GuavaCache(LoadingCache<String, Serializable> loadingCache, LoadingCache<String, Serializable> temporaryLoadingCache) {
		this.loadingCache = loadingCache;
		this.temporaryLoadingCache = temporaryLoadingCache;
	}

	@Override
	public void clear() {
		loadingCache.invalidateAll();
		temporaryLoadingCache.invalidateAll();
	}

	@Override
	public Serializable get(String key) {
		if (StrUtil.isBlank(key)) {
			return null;
		}
		Serializable ret = loadingCache.getIfPresent(key);
		if (ret == null) {
			ret = temporaryLoadingCache.getIfPresent(key);
		}
		
		return ret;
	}

	@Override
	public Collection<String> keys() {
		ConcurrentMap<String, Serializable> map = loadingCache.asMap();
		return map.keySet();
	}

	@Override
	public void put(String key, Serializable value) {
		if (StrUtil.isBlank(key)) {
			return;
		}
		loadingCache.put(key, value);
	}
	
	@Override
	public void putTemporary(String key, Serializable value) {
		if (StrUtil.isBlank(key)) {
			return;
		}
		temporaryLoadingCache.put(key, value);
	}

	@Override
	public void remove(String key) {
		if (StrUtil.isBlank(key)) {
			return;
		}
		loadingCache.invalidate(key);
		temporaryLoadingCache.invalidate(key);
	}

	/**
	 * 
	 * @return
	 *
	 */
	public ConcurrentMap<String, Serializable> asMap() {
		return loadingCache.asMap();
	}
	
	/**
	 * 
	 * @return
	 *
	 */
	public long size() {
		return loadingCache.size();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String key, Class<T> clazz) {
		return (T)get(key);
	}

	@Override
	public long ttl(String key) {
		throw new RuntimeException("不支持ttl");
	}
}
