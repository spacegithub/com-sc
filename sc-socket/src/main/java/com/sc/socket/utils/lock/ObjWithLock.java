package com.sc.socket.utils.lock;

import java.io.Serializable;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

/**
 * 自带读写锁的对象.
 */
public class ObjWithLock<T> implements Serializable {
	/**
	 * 
	 */
	private T obj = null;

	/**
	 * 
	 */
	private ReentrantReadWriteLock lock = null;

	/**
	 * 
	 * @param obj
	 *
	 */
	public ObjWithLock(T obj) {
		this(obj, new ReentrantReadWriteLock());
	}

	/**
	 * 
	 * @param obj
	 * @param lock
	 *
	 */
	public ObjWithLock(T obj, ReentrantReadWriteLock lock) {
		super();
		this.obj = obj;
		this.lock = lock;
	}

	/**
	 * 
	 * @return
	 *
	 */
	public ReentrantReadWriteLock getLock() {
		return lock;
	}
	
	/**
	 * 获取写锁
	 * @return
	 */
	public WriteLock writeLock() {
		return lock.writeLock();
	}
	
	/**
	 * 获取读锁
	 * @return
	 */
	public ReadLock readLock() {
		return lock.readLock();
	}

	/**
	 * 
	 * @return
	 *
	 */
	public T getObj() {
		return obj;
	}

	/**
	 * 
	 * @param obj
	 *
	 */
	public void setObj(T obj) {
		this.obj = obj;
	}
	
	private static final long serialVersionUID = -3048283373239453901L;
}
