package com.sc.socket.utils.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

/**
 *
 * 2017年5月14日 上午9:55:37
 */
public class MapWithLock<K, V> extends ObjWithLock<Map<K, V>> {
	private static final long serialVersionUID = -652862323697152866L;
	private static final Logger log = LoggerFactory.getLogger(MapWithLock.class);

	public MapWithLock() {
		this(new HashMap<K,V>());
	}

	/**
	 * @param cacheMap
	 *
	 */
	public MapWithLock(Map<K, V> map) {
		super(map);
	}

	/**
	 * @param cacheMap
	 * @param lock
	 *
	 */
	public MapWithLock(Map<K, V> map, ReentrantReadWriteLock lock) {
		super(map, lock);
	}

	/**
	 * 
	 * @param key
	 * @param value
	 * @return
	 *
	 */
	public V put(K key, V value) {
		WriteLock writeLock = this.writeLock();
		writeLock.lock();
		try {
			Map<K, V> map = this.getObj();
			return map.put(key, value);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		} finally {
			writeLock.unlock();
		}
		return null;
	}

	/**
	 * 如果key值已经存在，则不会把新value put进去
	 * 如果key值不存在，此方法同put(key, value)
	 * @param key
	 * @param value
	 * @return
	 *
	 */
	public V putIfAbsent(K key, V value) {
		WriteLock writeLock = this.writeLock();
		writeLock.lock();
		try {
			Map<K, V> map = this.getObj();
			V oldValue = map.putIfAbsent(key, value);
			if (oldValue == null) {
				return value;
			} else {
				return oldValue;
			}
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		} finally {
			writeLock.unlock();
		}
		return null;
	}

	/**
	 * 
	 * @param otherMap
	 *
	 */
	public void putAll(Map<K, V> otherMap) {
		if (otherMap == null || otherMap.isEmpty()) {
			return;
		}

		WriteLock writeLock = this.writeLock();
		writeLock.lock();
		try {
			Map<K, V> map = this.getObj();
			map.putAll(otherMap);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * 
	 * @param key
	 * @return
	 *
	 */
	public V remove(K key) {
		WriteLock writeLock = this.writeLock();
		writeLock.lock();
		try {
			Map<K, V> map = this.getObj();
			return map.remove(key);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		} finally {
			writeLock.unlock();
		}
		return null;
	}

	/**
	 * clear
	 *
	 */
	public void clear() {
		WriteLock writeLock = this.writeLock();
		writeLock.lock();
		try {
			Map<K, V> map = this.getObj();
			map.clear();
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * 
	 * @param key
	 * @return
	 *
	 */
	public V get(K key) {
		ReadLock readLock = this.readLock();
		readLock.lock();
		try {
			Map<K, V> map = this.getObj();
			return map.get(key);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		} finally {
			readLock.unlock();
		}
		return null;
	}

	/**
	 * 
	 * @return
	 *
	 */
	public int size() {
		ReadLock readLock = this.readLock();
		readLock.lock();
		try {
			Map<K, V> map = this.getObj();
			return map.size();
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * 
	 * @return 如果没值，则返回null，否则返回一个新map
	 *
	 */
	public Map<K, V> copy() {
		ReadLock readLock = readLock();
		readLock.lock();
		try {
			if (this.getObj().size() > 0) {
				return new HashMap<>(getObj());
			}
			return null;
		} finally {
			readLock.unlock();
		}
	}

}
