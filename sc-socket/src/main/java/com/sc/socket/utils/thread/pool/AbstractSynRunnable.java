package com.sc.socket.utils.thread.pool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 *异步线程池线程执行抽象
 *
 */
public abstract class AbstractSynRunnable implements Runnable {

    private static Logger log = LoggerFactory.getLogger(AbstractSynRunnable.class);
    /**
     * 提交成功次数
     */
    public AtomicInteger executeCount = new AtomicInteger();

    /**
     * 避免重复提交次数
     */
    public AtomicInteger avoidRepeatExecuteCount = new AtomicInteger();

    /**
     * 被循环执行的次数
     */
    public AtomicInteger loopCount = new AtomicInteger();

    /**
     * 运行次数
     */
    public AtomicInteger runCount = new AtomicInteger();
    public Executor executor;
    /**
     * 是否已经提交到线程池了
     */
    private boolean executed = false;
    private ReadWriteLock runningLock = new ReentrantReadWriteLock();
    private boolean isCanceled = false;

    /**
     * Instantiates a new abstract syn runnable.
     */
    protected AbstractSynRunnable(Executor executor) {
        this.executor = executor;
    }

    /**
     * 把本任务对象提交到线程池去执行
     */
    public void execute() {
        executor.execute(this);
    }

    public abstract boolean isNeededExecute();

    public boolean isCanceled() {
        return isCanceled;
    }

    public void setCanceled(boolean isCanceled) {
        this.isCanceled = isCanceled;
    }

    @Override
    public final void run() {
        Lock writeLock = runningLock().writeLock();
        writeLock.lock();
        try {
            runCount.incrementAndGet();
            if (isCanceled()) //任务已经被取消
            {
                return;
            }
            loopCount.set(0);
            runTask();
            //如果队列还存在且队列小于10则执行队列里的run
            while (isNeededExecute() && loopCount.incrementAndGet() <= 10) {
                runTask();
            }

        } catch (Throwable e) {
            log.error(e.toString(), e);
        } finally {
            setExecuted(false);
            writeLock.unlock();
            //下面这段代码一定要在unlock()后面，别弄错了 ^_^
            if (isNeededExecute()) {
                execute();
            }
        }
    }
    public abstract void runTask();

    /**
     *
     * @return
     *
     */
    public ReadWriteLock runningLock() {
        return runningLock;
    }

    /**
     * 是否已经提交到线程池了
     *
     * @return the executed
     */
    public boolean isExecuted() {
        return executed;
    }

    /**
     * 是否已经提交到线程池了
     *
     * @param executed the executed to set
     */
    public void setExecuted(boolean executed) {
        this.executed = executed;
    }

    public String logstr() {
        return this.getClass().getName();
    }


}
