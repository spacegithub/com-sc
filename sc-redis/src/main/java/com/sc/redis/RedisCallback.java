package com.sc.redis;

import redis.clients.jedis.Jedis;


public interface RedisCallback<T> {

    T doInRedis(Jedis jedis);

}
