package com.sc.socket.utils.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

/**
 * 自带读写锁的set集合
 * 2017年5月14日 上午9:55:37
 */
public class SetWithLock<T> extends ObjWithLock<Set<T>> {
	private static final long serialVersionUID = -2305909960649321346L;
	private static final Logger log = LoggerFactory.getLogger(SetWithLock.class);

	/**
	 * @param set
	 *
	 */
	public SetWithLock(Set<T> set) {
		super(set);
	}

	/**
	 * @param set
	 * @param lock
	 *
	 */
	public SetWithLock(Set<T> set, ReentrantReadWriteLock lock) {
		super(set, lock);
	}

	/**
	 *
	 * @param t
	 * @return
	 *
	 */
	public boolean add(T t) {
		WriteLock writeLock = this.writeLock();
		writeLock.lock();
		try {
			Set<T> set = this.getObj();
			return set.add(t);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		} finally {
			writeLock.unlock();
		}
		return false;
	}

	/**
	 *
	 *
	 *
	 */
	public void clear() {
		WriteLock writeLock = this.writeLock();
		writeLock.lock();
		try {
			Set<T> set = this.getObj();
			set.clear();
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 *
	 * @param t
	 * @return
	 *
	 */
	public boolean remove(T t) {
		WriteLock writeLock = this.writeLock();
		writeLock.lock();
		try {
			Set<T> set = this.getObj();
			return set.remove(t);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		} finally {
			writeLock.unlock();
		}
		return false;
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
			Set<T> set = this.getObj();
			return set.size();
		} finally {
			readLock.unlock();
		}
	}
}
