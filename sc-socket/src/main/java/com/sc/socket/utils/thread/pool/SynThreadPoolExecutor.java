package com.sc.socket.utils.thread.pool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 *
 * 异步线程池,复写异步线程提交execute 和 submit
 *
 * 2017年4月26日 下午2:18:30
 */
public class SynThreadPoolExecutor extends ThreadPoolExecutor {
    /**
     * 线程名称
     */
	private String name = null;

	/**
	 *
	 * @param corePoolSize
	 * @param maximumPoolSize
	 * @param keepAliveTime 单位: 秒
	 * @param runnableQueue
	 * @param threadFactory
	 * @param name
	 *
	 */
	public SynThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, BlockingQueue<Runnable> runnableQueue, ThreadFactory threadFactory, String name) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, runnableQueue, threadFactory);
		this.name = name;
	}

	/**
	 * 检查异步线程是否已经被提交
	 * @param runnable
	 * @return
	 *
	 */
	private boolean checkBeforeExecute(Runnable runnable) {
		if (runnable instanceof AbstractSynRunnable) {
			AbstractSynRunnable synRunnable = (AbstractSynRunnable) runnable;
			if (synRunnable.isExecuted()) {
				return false;
			}
			ReadWriteLock runningLock = synRunnable.runningLock();
			Lock writeLock = runningLock.writeLock();
			boolean tryLock = writeLock.tryLock();
			try {
//			tryLock = writeLock.tryLock();
				if (tryLock) {
					if (synRunnable.isExecuted()) {
//						synRunnable.avoidRepeatExecuteCount.incrementAndGet();
						return false;
					}
					synRunnable.executeCount.incrementAndGet();
//					System.out.println(synRunnable.logstr() + ", 提交成功次数" + synRunnable.executeCount);
					synRunnable.setExecuted(true);
					
				} else {
//					synRunnable.avoidRepeatExecuteCount.incrementAndGet();
				}
				return tryLock;
			} finally {
				if (tryLock) {
					writeLock.unlock();
				}
			}
		} else {
			return true;
		}

	}

	@Override
	public void execute(Runnable runnable) {
		if (checkBeforeExecute(runnable)) {
			execute1(runnable);
		}  
	}
	
	private void execute1(Runnable runnable) {
		super.execute(runnable);
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public <R> Future<R> submit(Runnable runnable, R result) {
		if (checkBeforeExecute(runnable)) {
			Future<R> ret = super.submit(runnable, result);
			return ret;
		} else {
			return null;
		}
	}

}
