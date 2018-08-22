package com.sc.redis.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

/**
 * Jedis Cluster 测试
 */
public class JedisClusterTest {

    private JedisCluster jc;

    @Before
    public void before() {
        Set<HostAndPort> clusters = new HashSet<HostAndPort>();
        clusters.add(new HostAndPort("192.168.0.201",6379));
        clusters.add(new HostAndPort("192.168.0.201",6380));
        clusters.add(new HostAndPort("192.168.0.201",6381));
        clusters.add(new HostAndPort("192.168.0.201",16379));
        clusters.add(new HostAndPort("192.168.0.201",16380));
        clusters.add(new HostAndPort("192.168.0.201",16381));
        jc = new JedisCluster(clusters);

    }

    @Test
    public void testCluster() {
        jc.set("name","yangxin");
        jc.set("age","27");

        String name = jc.get("name");
        Assert.assertEquals(name,"yangxin");
        String age = jc.get("age");
        Assert.assertEquals(age,"27");
    }

    // 无法操作多个key
    @Test
    public void testMGet() {
        List<String> values = jc.mget("name","age");
        System.out.println(values);
    }

    @Test
    public void testBatchCluster() {
        // Jedis cluster不支持pipleline
        long begin = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            jc.set("key" + i, "value" + i);
        }
        long time = System.currentTimeMillis() - begin;
        System.out.println(time);
    }

    @After
    public void after() {
        try {
            jc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}