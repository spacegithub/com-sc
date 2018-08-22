package com.sc.redis.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.ScanResult;

/**
 * Jedis连接池测试
 */
public class JedisPoolTest {

    private JedisPool jedisPool;

    @Before
    public void before() {
        JedisPoolConfig config = new JedisPoolConfig();
        // 设置最大连接数
        config.setMaxTotal(100);
        // 最大空闲连接数
        config.setMaxIdle(5);
        // 最少空闲连接数（初始化连接数）
        config.setMinIdle(1);
        // 连接等待超时时长60秒
        config.setMaxWaitMillis(60 * 1000);
        // 在获取redis连接时，自动测试连接是否可用，保证返回可用的连接
        config.setTestOnBorrow(true);
        // 连接回收到连接池时，自动测试连接是否可用（ping()），如果连接不可用，则不会放回连接池
        config.setTestOnReturn(true);
        // 自动检测空闲连接是否可用
        config.setTestWhileIdle(true);
        // 检测空闲连接是否可用的间隔时间
        config.setTimeBetweenEvictionRunsMillis(60* 1000);

        jedisPool = new JedisPool(config,"192.168.0.201",6384,5000,"yangxin");
    }

    @Test
    public void testString() {
        Jedis resource = jedisPool.getResource();
        resource.sadd("hobbys","吃","喝","玩","乐");
        ScanResult<String> result = resource.sscan("hobbys","0");
        List<String> s = result.getResult();
        System.out.println(s);
        resource.close();   // 回收连接
    }

    @After
    public void after() {
        jedisPool.close();
    }
}