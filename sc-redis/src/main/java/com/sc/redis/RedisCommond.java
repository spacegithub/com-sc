package com.sc.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * redis 命令行
 *
 * @auth:mingfly
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public abstract class RedisCommond {

    private JedisPool jedisPool;

    public RedisCommond(JedisPool jedisPool){
        this.jedisPool=jedisPool;
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public <T> T run(RedisCallback<T> redisCallback){
        Jedis jedis=jedisPool.getResource();
        try{
          return redisCallback.doInRedis(jedis);
        }finally {
            jedis.close();
        }
    }

    public Jedis getNativeJedis(){
        return jedisPool.getResource();
    }

    public void close(Jedis jedis){
        jedisPool.returnResourceObject(jedis);
    }
}
