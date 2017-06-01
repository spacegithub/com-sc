package com.sc.redis;


import com.sc.mapper.JsonMapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * redis模板方法
 *

 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class RedisTemplate extends RedisCommond{

    public RedisTemplate(JedisPool jedisPool) {
        super(jedisPool);
    }

    /**
     * 获取一个值
     *
     * @param key 键
     *
     * @return 值
     */
    public String get(final String key) {
        return run(new RedisCallback<String>() {
            public String doInRedis(Jedis jedis) {
                return jedis.get(key);
            }
        });
    }

    public <T> T get(final String key,final Class<T> clazz){
        return run(new RedisCallback<T>() {
            public T doInRedis(Jedis jedis) {
                String result= jedis.get(key);
                return JsonMapper.nonEmptyMapper().fromJson(result, clazz);
            }
        });
    }

    public <T> List<T> getList(final String key,final Class<T> clazz){
        return run(new RedisCallback<List<T>>() {
            public List<T> doInRedis(Jedis jedis) {
                String result= jedis.get(key);
                return (List<T>)JsonMapper.nonEmptyMapper().fromJson(result, List.class);
            }
        });
    }

    /**
     * 设置一个值
     *
     * @param key   键
     * @param value 值
     */
    public void set(final String key, final String value) {
        run(new RedisCallback<String>() {
            public String doInRedis(Jedis jedis) {
               return jedis.set(key, value);
            }
        });
    }

    /**
     * 设置一个值
     *
     * @param key   键
     * @param value 值
     */
    public void set(final String key, final Object value) {
        run(new RedisCallback<String>() {
            public String doInRedis(Jedis jedis) {
                return jedis.set(key, JsonMapper.nonEmptyMapper().toJson(value));
            }
        });
    }

    /**
     * 设置值带过期时间
     * @param key
     * @param value
     * @param seconds
     */
    public void setEx(final String key,final String value,final int seconds){
        run(new RedisCallback<String>() {
                @Override
                public String doInRedis(Jedis jedis) {
                    return jedis.setex(key, seconds, value);
                }
            }
        );
    }

    /**
     * 删除key
     * @param key
     */
    public void del(final String key){
        run(new RedisCallback<Long>() {
                @Override
                public Long doInRedis(Jedis jedis) {
                    return jedis.del(key);
                }
            }
        );
    }

    /**
     * 批量删除key
     * @param keys
     */
    public void del(final String ... keys){
        run(new RedisCallback<Long>() {
                @Override
                public Long doInRedis(Jedis jedis) {
                    return jedis.del(keys);
                }
            }
        );
    }

    /**
     * 设置值带过期时间
     * @param key
     * @param value
     * @param seconds
     */
    public void setEx(final String key,final Object value,final int seconds){
        run(new RedisCallback<String>() {
                @Override
                public String doInRedis(Jedis jedis) {
                    return jedis.setex(key, seconds, JsonMapper.nonEmptyMapper().toJson(value));
                }
            }
        );
    }

    /**
     * 按照模糊查询Key
     * @param pattern
     * @return
     */
    public Set<String> keys(final String pattern){
       return run(new RedisCallback<Set<String>>() {
            @Override
            public Set<String> doInRedis(Jedis jedis) {
                return jedis.keys(pattern);
            }
        });
    }

    /**
     * HSet新增
     * @param key
     * @param field
     * @param value
     */
    public void hSet(final String key,final String field, final String value){
         run(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.hset(key,field,value);
            }
        });
    }

    /**
     * 设置hash里面一个字段的值
     * @param key
     * @param fields
     */
    public void hSet(final String key, final Map<String,String> fields){
        run(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                for (Map.Entry<String,String> entry:fields.entrySet()){
                    jedis.hset(key,entry.getKey(),entry.getValue());
                }
                return null;
            }
        });
    }

    /**
     * hExists 判断给定域是否存在于哈希集中
     * @param key
     * @param fileld
     */
    public Boolean hExists(final String key, final String fileld){
      return run(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(Jedis jedis) {
              return jedis.hexists(key, fileld);
            }
        });
    }

    /**
     * HINCRBY 将哈希集中指定域的值增加给定的数字
     * @param key
     * @param fileld
     */
    public Long hIncrby(final String key, final String fileld,final Long value){
        return run(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.hincrBy(key, fileld, value);
            }
        });
    }

    /**
     * hIncrbyFloat 将哈希集中指定域的值增加给定的浮点数
     * @param key
     * @param fileld
     * @param value
     */
    public Double hIncrbyFloat(final String key, final String fileld,final Double value){
        return run(new RedisCallback<Double>() {
            @Override
            public Double doInRedis(Jedis jedis) {
                return jedis.hincrByFloat(key, fileld, value);
            }
        });
    }

    /**
     * hLen 将哈希集中指定域的值增加给定的浮点数
     * @param key
     */
    public Long hLen(final String key){
        return run(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.hlen(key);
            }
        });
    }

    /**
     * hKeys 获取hash的所有字段
     * @param key
     */
    public Set<String> hKeys(final String key){
        return run(new RedisCallback<Set<String>>() {
            @Override
            public Set<String> doInRedis(Jedis jedis) {
                return jedis.hkeys(key);
            }
        });
    }

    /**
     * HDEL  获取hash的所有字段
     * @param key
     * @param field
     */
    public Long hDel(final String key,final String ... field){
        return run(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.hdel(key, field);
            }
        });
    }

    /**
     * HGETALL  从哈希集中读取全部的域和值
     * @param key
     */
    public Map<String,String>  hGetAll(final String key){
        return run(new RedisCallback<Map<String,String> >() {
            @Override
            public Map<String,String> doInRedis(Jedis jedis) {
                return jedis.hgetAll(key);
            }
        });
    }

    /**
     * HGET 读取哈希域的的值
     * @param key
     */
    public String  hGet(final String key,final String field){
        return run(new RedisCallback<String>() {
            @Override
            public String doInRedis(Jedis jedis) {
                return jedis.hget(key, field);
            }
        });
    }


    /**
     * HVALS  获得hash的所有值
     * @param key
     */
    public List<String>  hVals(final String key){
        return run(new RedisCallback<List<String>>() {
            @Override
            public List<String> doInRedis(Jedis jedis) {
                return jedis.hvals(key);
            }
        });
    }

    /**
     * HVALS  设置hash的一个字段，只有当这个字段不存在时有效
     * @param key
     */
    public Long hSetNx(final String key,final String field, final String value){
        return run(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.hsetnx(key, field, value);
            }
        });
    }

    /**
     * HMGET  获取hash里面指定字段的值
     * @param key
     */
    public List<String> hMget(final String key,final String ... fields){
        return run(new RedisCallback<List<String>>() {
            @Override
            public List<String> doInRedis(Jedis jedis) {
                return jedis.hmget(key,fields);
            }
        });
    }

    /**
     * HMSET  设置hash字段值
     * @param key
     */
    public String hMset(final String key,final Map<String,String> fields){
        return run(new RedisCallback<String>() {
            @Override
            public String doInRedis(Jedis jedis) {
                return jedis.hmset(key, fields);
            }
        });
    }



}
