package com.sc.redisson.util;

import com.sc.redisson.manager.RedissonManager;

import org.redisson.Redisson;
import org.redisson.api.RLock;

import java.util.concurrent.TimeUnit;


public class DistributedLockUtil {

    private static Redisson redisson = RedissonManager.getInstance().getRedissonClient();

    private static final String LOCK_FLAG = "redissonlock_";

    /**
     * 根据name对进行上锁操作，redissonLock 阻塞事的，采用的机制发布/订阅
     *
     * @param lockname
     */
    public static void lock(String lockname) {
        String key = LOCK_FLAG + lockname;
        RLock lock = redisson.getLock(key);
        //lock提供带timeout参数，timeout结束强制解锁，防止死锁 ：1分钟
        lock.lock(1, TimeUnit.MINUTES);
    }

    /**
     * 根据name对进行解锁操作
     *
     * @param lockname
     */
    public static void unlock(String lockname) {
        String key = LOCK_FLAG + lockname;
        RLock lock = redisson.getLock(key);
        lock.unlock();
    }


}
