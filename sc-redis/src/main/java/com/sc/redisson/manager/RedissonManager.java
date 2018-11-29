package com.sc.redisson.manager;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class RedissonManager {


    private volatile static RedissonManager instance;
    private static RedissonClient redissonClient;

    private RedissonManager() {
        try {
            Config config = new Config();
            //config.setTransportMode(TransportMode.EPOLL);
            config.useSingleServer()
                    .setAddress("redis://:fatredis02@10.1.41.177:6379/3").setPassword("fatredis02");
            redissonClient = Redisson.create(config);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static RedissonManager getInstance() {
        if (instance == null) {
            synchronized (RedissonManager.class) {
                if (instance == null) {
                    instance = new RedissonManager();
                }
            }
        }
        return instance;
    }


    public Redisson getRedissonClient() {
        return (Redisson) redissonClient;
    }

    public static void main(String[] args) {
        Redisson redisson = RedissonManager.getInstance().getRedissonClient();
        RLock lock = redisson.getLock("1000");
        lock.lock();
        System.out.println("redisson = " + redisson);
        lock.unlock();
        redisson.shutdown();
    }


}
