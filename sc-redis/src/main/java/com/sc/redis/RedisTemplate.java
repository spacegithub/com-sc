package com.sc.redis;


import com.sc.utils.mapper.JsonMapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


public class RedisTemplate extends RedisCommond {

    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    private static final Long RELEASE_SUCCESS = 1L;


    public RedisTemplate(JedisPool jedisPool) {
        super(jedisPool);
    }

    /**
     * 尝试获取分布式锁
     *
     * @param jedis      Redis客户端
     * @param lockKey    锁
     * @param requestId  请求标识
     * @param expireTime 超期时间
     * @return 是否获取成功
     */
    public static boolean tryGetDistributedLock(Jedis jedis, String lockKey, String requestId, int expireTime) {

        String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);

        if (LOCK_SUCCESS.equals(result)) {
            return true;
        }
        return false;

    }

    /**
     * 释放分布式锁
     *
     * @param jedis     Redis客户端
     * @param lockKey   锁
     * @param requestId 请求标识
     * @return 是否释放成功
     */
    public static boolean releaseDistributedLock(Jedis jedis, String lockKey, String requestId) {

        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));

        if (RELEASE_SUCCESS.equals(result)) {
            return true;
        }
        return false;

    }

    public String get(final String key) {
        return run(new RedisCallback<String>() {
            @Override
            public String doInRedis(Jedis jedis) {
                return jedis.get(key);
            }
        });
    }

    public <T> T get(final String key, final Class<T> clazz) {
        return run(new RedisCallback<T>() {
            @Override
            public T doInRedis(Jedis jedis) {
                String result = jedis.get(key);
                return JsonMapper.nonEmptyMapper().fromJson(result, clazz);
            }
        });
    }

    public <T> List<T> getList(final String key, final Class<T> clazz) {
        return run(new RedisCallback<List<T>>() {
            @Override
            public List<T> doInRedis(Jedis jedis) {
                String result = jedis.get(key);
                return (List<T>) JsonMapper.nonEmptyMapper().fromJson(result, List.class);
            }
        });
    }

    public void set(final String key, final String value) {
        run(new RedisCallback<String>() {
            @Override
            public String doInRedis(Jedis jedis) {
                return jedis.set(key, value);
            }
        });
    }

    public void set(final String key, final Object value) {
        run(new RedisCallback<String>() {
            @Override
            public String doInRedis(Jedis jedis) {
                return jedis.set(key, JsonMapper.nonEmptyMapper().toJson(value));
            }
        });
    }

    public void setEx(final String key, final String value, final int seconds) {
        run(new RedisCallback<String>() {
                @Override
                public String doInRedis(Jedis jedis) {
                    return jedis.setex(key, seconds, value);
                }
            }
        );
    }

    public void del(final String key) {
        run(new RedisCallback<Long>() {
                @Override
                public Long doInRedis(Jedis jedis) {
                    return jedis.del(key);
                }
            }
        );
    }

    public void del(final String... keys) {
        run(new RedisCallback<Long>() {
                @Override
                public Long doInRedis(Jedis jedis) {
                    return jedis.del(keys);
                }
            }
        );
    }

    public void setEx(final String key, final Object value, final int seconds) {
        run(new RedisCallback<String>() {
                @Override
                public String doInRedis(Jedis jedis) {
                    return jedis.setex(key, seconds, JsonMapper.nonEmptyMapper().toJson(value));
                }
            }
        );
    }

    public Set<String> keys(final String pattern) {
        return run(new RedisCallback<Set<String>>() {
            @Override
            public Set<String> doInRedis(Jedis jedis) {
                return jedis.keys(pattern);
            }
        });
    }

    public void hSet(final String key, final String field, final String value) {
        run(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.hset(key, field, value);
            }
        });
    }

    public void hSet(final String key, final Map<String, String> fields) {
        run(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                for (Map.Entry<String, String> entry : fields.entrySet()) {
                    jedis.hset(key, entry.getKey(), entry.getValue());
                }
                return null;
            }
        });
    }

    public Boolean hExists(final String key, final String fileld) {
        return run(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(Jedis jedis) {
                return jedis.hexists(key, fileld);
            }
        });
    }

    public Long hIncrby(final String key, final String fileld, final Long value) {
        return run(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.hincrBy(key, fileld, value);
            }
        });
    }

    public Double hIncrbyFloat(final String key, final String fileld, final Double value) {
        return run(new RedisCallback<Double>() {
            @Override
            public Double doInRedis(Jedis jedis) {
                return jedis.hincrByFloat(key, fileld, value);
            }
        });
    }

    public Long hLen(final String key) {
        return run(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.hlen(key);
            }
        });
    }

    public Set<String> hKeys(final String key) {
        return run(new RedisCallback<Set<String>>() {
            @Override
            public Set<String> doInRedis(Jedis jedis) {
                return jedis.hkeys(key);
            }
        });
    }

    public Long hDel(final String key, final String... field) {
        return run(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.hdel(key, field);
            }
        });
    }

    public Map<String, String> hGetAll(final String key) {
        return run(new RedisCallback<Map<String, String>>() {
            @Override
            public Map<String, String> doInRedis(Jedis jedis) {
                return jedis.hgetAll(key);
            }
        });
    }

    public String hGet(final String key, final String field) {
        return run(new RedisCallback<String>() {
            @Override
            public String doInRedis(Jedis jedis) {
                return jedis.hget(key, field);
            }
        });
    }

    public List<String> hVals(final String key) {
        return run(new RedisCallback<List<String>>() {
            @Override
            public List<String> doInRedis(Jedis jedis) {
                return jedis.hvals(key);
            }
        });
    }

    public Long hSetNx(final String key, final String field, final String value) {
        return run(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                return jedis.hsetnx(key, field, value);
            }
        });
    }

    public List<String> hMget(final String key, final String... fields) {
        return run(new RedisCallback<List<String>>() {
            @Override
            public List<String> doInRedis(Jedis jedis) {
                return jedis.hmget(key, fields);
            }
        });
    }

    public String hMset(final String key, final Map<String, String> fields) {
        return run(new RedisCallback<String>() {
            @Override
            public String doInRedis(Jedis jedis) {
                return jedis.hmset(key, fields);
            }
        });
    }

    /**
     * redis锁,如果设置成功则返回1并设置过期时间,如果已存在(表示此key存在未过期的数值),则返回0
     *
     * @param key     锁key
     * @param seconds 过期时间
     * @param value   锁值
     */
    public Long setnx(final String key, final int seconds, final String value) {
        return (Long) this.run(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(Jedis jedis) {
                Long setnx = jedis.setnx(key, value);
                if (setnx.equals(1L)) {
                    jedis.expire(key, seconds);
                }
                return setnx;
            }
        });
    }


}
