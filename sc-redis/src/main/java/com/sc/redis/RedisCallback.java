package com.sc.redis;

import redis.clients.jedis.Jedis;

/**
 * redis 命令执行回调
 *
 * @auth:mingfly
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public interface RedisCallback<T> {

    T doInRedis(Jedis jedis);

}
