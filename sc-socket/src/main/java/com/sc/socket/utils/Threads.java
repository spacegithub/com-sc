/**
 *
 */
package com.sc.socket.utils;

import com.sc.socket.utils.thread.pool.DefaultThreadFactory;
import com.sc.socket.utils.thread.pool.SynThreadPoolExecutor;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 *
 * 2017年7月7日 上午11:12:03
 */
public class Threads {

	public static int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

	public static int CORE_POOL_SIZE = AVAILABLE_PROCESSORS * 1;

	public static final int MAX_POOL_SIZE_FOR_TIO = Math.max(CORE_POOL_SIZE * 3, 64);

	public static final int MAX_POOL_SIZE_FOR_GROUP = Math.max(CORE_POOL_SIZE * 16, 256);

	public static final long KEEP_ALIVE_TIME = 0L;//360000L;

	@SuppressWarnings("unused")
	private static final int QUEUE_CAPACITY = 1000000;

	private static ThreadPoolExecutor groupExecutor = null;

	private static SynThreadPoolExecutor tioExecutor = null;

	/**
	 *
	 * @return
	 *
	 */
	public static ThreadPoolExecutor getGroupExecutor() {
		if (groupExecutor != null) {
			return groupExecutor;
		}

		synchronized (Threads.class) {
			if (groupExecutor != null) {
				return groupExecutor;
			}

			LinkedBlockingQueue<Runnable> groupQueue = new LinkedBlockingQueue<>();
			//			ArrayBlockingQueue<Runnable> groupQueue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
			String groupThreadName = "socket-group";
			DefaultThreadFactory defaultThreadFactory = DefaultThreadFactory.getInstance(groupThreadName, Thread.MAX_PRIORITY);

			groupExecutor = new ThreadPoolExecutor(MAX_POOL_SIZE_FOR_GROUP, MAX_POOL_SIZE_FOR_GROUP, KEEP_ALIVE_TIME, TimeUnit.SECONDS, groupQueue, defaultThreadFactory);
			//			groupExecutor = new ThreadPoolExecutor(AVAILABLE_PROCESSORS * 2, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), defaultThreadFactory);
            //预启动核心线程
			groupExecutor.prestartCoreThread();
//			groupExecutor.prestartAllCoreThreads();
			return groupExecutor;
		}
	}

	/**
	 *
	 * @return
	 *
	 */
	public static SynThreadPoolExecutor getTioExecutor() {
		if (tioExecutor != null) {
			return tioExecutor;
		}

		synchronized (Threads.class) {
			if (tioExecutor != null) {
				return tioExecutor;
			}

			LinkedBlockingQueue<Runnable> tioQueue = new LinkedBlockingQueue<>();
			//			ArrayBlockingQueue<Runnable> tioQueue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
			String tioThreadName = "socket-worker";
			DefaultThreadFactory defaultThreadFactory = DefaultThreadFactory.getInstance(tioThreadName, Thread.MAX_PRIORITY);

			tioExecutor = new SynThreadPoolExecutor(MAX_POOL_SIZE_FOR_TIO, MAX_POOL_SIZE_FOR_TIO, KEEP_ALIVE_TIME, tioQueue, defaultThreadFactory, tioThreadName);
			//			tioExecutor = new SynThreadPoolExecutor(AVAILABLE_PROCESSORS * 2, Integer.MAX_VALUE, 60, new SynchronousQueue<Runnable>(), defaultThreadFactory, tioThreadName);
            //预启动核心线程
			tioExecutor.prestartCoreThread();
//			tioExecutor.prestartAllCoreThreads();
			return tioExecutor;
		}
	}

	/**
	 *
	 */
	private Threads() {
	}
}
