package com.sc.socket.utils.cache.redis;

import com.sc.socket.utils.SystemTimer;
import com.sc.socket.utils.cache.ICache;

import org.redisson.api.RBucket;
import org.redisson.api.RKeys;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sc.socket.utils.hutool.StrUtil;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 *
 * 2017年8月10日 下午1:35:01
 */
public class RedisCache implements ICache {
	private static Logger log = LoggerFactory.getLogger(RedisCache.class);
	private static Map<String, RedisCache> map = new HashMap<>();

	public static String cacheKey(String cacheName, String key) {
		return keyPrefix(cacheName) + key;
	}

	public static RedisCache getCache(String cacheName) {
		RedisCache redisCache = map.get(cacheName);
		if (redisCache == null) {
			log.error("cacheName[{}]还没注册，请初始化时调用：{}.register(redisson, cacheName, timeToLiveSeconds, timeToIdleSeconds)", cacheName, RedisCache.class.getSimpleName());
		}
		return redisCache;
	}

	public static String keyPrefix(String cacheName) {
		return cacheName + ":";
	}

	/**
	 * timeToLiveSeconds和timeToIdleSeconds不允许同时为null
	 * @param cacheName
	 * @param timeToLiveSeconds
	 * @param timeToIdleSeconds
	 * @return
	 *
	 */
	public static RedisCache register(RedissonClient redisson, String cacheName, Long timeToLiveSeconds, Long timeToIdleSeconds) {
		RedisExpireUpdateTask.start();

		RedisCache redisCache = map.get(cacheName);
		if (redisCache == null) {
			synchronized (RedisCache.class) {
				redisCache = map.get(cacheName);
				if (redisCache == null) {
					redisCache = new RedisCache(redisson, cacheName, timeToLiveSeconds, timeToIdleSeconds);
					map.put(cacheName, redisCache);
				}
			}
		}
		return redisCache;
	}

	private RedissonClient redisson = null;

	private String cacheName = null;

	private Long timeToLiveSeconds = null;

	private Long timeToIdleSeconds = null;

	private Long timeout = null;

	private RedisCache(RedissonClient redisson, String cacheName, Long timeToLiveSeconds, Long timeToIdleSeconds) {
		this.redisson = redisson;
		this.cacheName = cacheName;
		this.timeToLiveSeconds = timeToLiveSeconds;
		this.timeToIdleSeconds = timeToIdleSeconds;
		this.timeout = this.timeToLiveSeconds == null ? this.timeToIdleSeconds : this.timeToLiveSeconds;

	}

	@Override
	public void clear() {
		long start = SystemTimer.currTime;

		RKeys keys = redisson.getKeys();

		//		keys.deleteByPattern(keyPrefix(cacheName) + "*");
		keys.deleteByPatternAsync(keyPrefix(cacheName) + "*");

		long end = SystemTimer.currTime;
		long iv = end - start;
		log.info("clear cache {}, cost {}ms", cacheName, iv);
	}

	@Override
	public Serializable get(String key) {
		if (StrUtil.isBlank(key)) {
			return null;
		}
		RBucket<Serializable> bucket = getBucket(key);
		if (bucket == null) {
			log.error("bucket is null, key:{}", key);
			return null;
		}
		Serializable ret = bucket.get();
		if (timeToIdleSeconds != null) {
			if (ret != null) {
				//				bucket.expire(timeout, TimeUnit.SECONDS);
				RedisExpireUpdateTask.add(cacheName, key, timeout);
			}
		}
		return ret;
	}

	public RBucket<Serializable> getBucket(String key) {
		key = cacheKey(cacheName, key);
		RBucket<Serializable> bucket = redisson.getBucket(key);
		return bucket;
	}

	public String getCacheName() {
		return cacheName;
	}

	public RedissonClient getRedisson() {
		return redisson;
	}

	public Long getTimeout() {
		return timeout;
	}

	public Long getTimeToIdleSeconds() {
		return timeToIdleSeconds;
	}

	public Long getTimeToLiveSeconds() {
		return timeToLiveSeconds;
	}

	@Override
	public Iterable<String> keys() {
		RKeys keys = redisson.getKeys();
		Iterable<String> allkey = keys.getKeysByPattern(keyPrefix(cacheName) + "*");//.findKeysByPattern(keyPrefix(cacheName) + "*");
		return allkey;
	}

	@Override
	public void put(String key, Serializable value) {
		if (StrUtil.isBlank(key)) {
			return;
		}
		RBucket<Serializable> bucket = getBucket(key);

		long _timeout = timeout;
		if (timeToLiveSeconds != null && timeToLiveSeconds > 0) { //是按timeToLiveSeconds来的
			long ttl = ttl(key);
			if (ttl > 0) {
				_timeout = ttl / 1000;
			}
		}

		bucket.set(value, _timeout, TimeUnit.SECONDS);
	}

	@Override
	public void putTemporary(String key, Serializable value) {
		if (StrUtil.isBlank(key)) {
			return;
		}
		RBucket<Serializable> bucket = getBucket(key);
		bucket.set(value, 10, TimeUnit.SECONDS);
	}

	@Override
	public void remove(String key) {
		if (StrUtil.isBlank(key)) {
			return;
		}
		RBucket<Serializable> bucket = getBucket(key);
		bucket.delete();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String key, Class<T> clazz) {
		return (T) get(key);
	}

	@Override
	public long ttl(String key) {
		RBucket<Serializable> bucket = getBucket(key);
		if (bucket == null) {
			return -2L;
		}
		long remainTimeToLive = bucket.remainTimeToLive();
		return remainTimeToLive;
	}

	//	@Override
	//	public void update(String key, Serializable value) {
	//		
	//	}
}
