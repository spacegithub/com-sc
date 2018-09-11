package com.sc.threadpool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class DefaultThreadPool<Job extends Runnable> implements ThreadPool<Job> {
    //线程池最大限制数
    private static final int MAX_WORKER_NUMBERS = 10;
    //线程池默认数量
    private static final int DEFAULT_WORKER_NUMBER = 5;
    //线程池最小数量
    private static final int MIN_WORKER_NUMBER = 1;
    //这是一个工作列表，将会向里面插入工作
    private final LinkedList<Job> jobs = new LinkedList<Job>();
    //工作者列表
    //Collections.synchronizedList使非同步的集合变成同步的（我理解这样在多线程对该集合进行操作的时候就不用手动加锁了）。
    private final List<Worker> workers = Collections.synchronizedList(new ArrayList<Worker>());
    //工作者线程的数量
    private int workerNum = DEFAULT_WORKER_NUMBER;
    //线程编号生成
    private AtomicLong threadNum = new AtomicLong();

    public DefaultThreadPool() {
        initializeWorkers(DEFAULT_WORKER_NUMBER);
    }

    public DefaultThreadPool(int num) {
        workerNum = num > MAX_WORKER_NUMBERS ? MAX_WORKER_NUMBERS : num < MIN_WORKER_NUMBER ? MIN_WORKER_NUMBER : num;
        initializeWorkers(workerNum);
    }

    //初始化线程工作者
    private void initializeWorkers(int num) {
        for (int i = 0; i < num; i++) {
            Worker worker = new Worker();
            workers.add(worker);
            Thread thread = new Thread(worker, "ThreadPool-Worker-" + threadNum.incrementAndGet());//incrementAndGet原子的方式加一
            thread.start();
        }
    }

    @Override
    public void execute(Job job) {
        if (job != null) {
            //添加一个工作，然后进行通知
            synchronized (jobs) {
                jobs.add(job);
                jobs.notifyAll();
            }
        }
    }

    @Override
    public void shutdown() {
        for (Worker worker : workers) {
            worker.shutdown();
        }
    }

    @Override
    public void addWorkers(int num) {
        synchronized (jobs) {//这个加锁有必要用吗？我觉得这里不需要。
            //限制新增的worker数量不能超过最大值
            if (num + this.workerNum > MAX_WORKER_NUMBERS) {
                num = MAX_WORKER_NUMBERS - this.workerNum;
            }
            initializeWorkers(num);
            this.workerNum += num;
        }
    }

    @Override
    public void removeWorker(int num) {
        synchronized (jobs) {
            if (num >= this.workerNum) {
                throw new IllegalArgumentException("beyond worknum.");
            }
            //按照给定的数量停止worker
            int count = 0;
            while (count < num) {
                Worker worker = workers.get(count);
                worker.shutdown();
                count++;
            }
            this.workerNum -= num;
        }
    }

    @Override
    public int getJobSize() {
        return jobs.size();
    }

    class Worker implements Runnable {
        //是否工作
        private volatile boolean running = true;

        public void run() {
            while (running) {
                Job job = null;
                synchronized (jobs) {
                    //如果工作者列表是空的，那么久wait
                    while (jobs.isEmpty()) {
                        try {
                            jobs.wait();
                        } catch (InterruptedException e) {
                            //感知到外部对workerThread的中断操作，返回
                            Thread.currentThread().interrupt();
                            e.printStackTrace();
                        }
                    }
                    //取出一个job
                    job = jobs.removeFirst();
                }
                if (job != null) {
                    System.out.println(Thread.currentThread().getName() + "处理一个job。");
                    job.run();
                }
            }
        }

        public void shutdown() {
            running = false;
        }
    }
}

