package com.sc.redis.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Transaction;

/**
 * 字符串、Set、List、Hash、publish/subscribe
 */
public class JedisTest {

    private Jedis jedis;

    @Before
    public void before() {
        jedis = new Jedis("192.168.0.201",6384);
        jedis.auth("senssic");
    }

    /**
     * 字符串
     * 文档：127.0.0.1:6384> help @string
     */
    @Test
    public void testString() {
        jedis.flushDB();    // 清空当前数据的所有数据
        // 保存一个字符串
        jedis.set("language","java");
        // 在原来key对应的值追加一个字符串，如果key不存在，则创建一个新的key
        jedis.append("language","c++");
        // 获取一个字符串
        String s = jedis.get("language");
        Assert.assertEquals("javac++",s);
        // 自增1，redis会转换成整型，再进行加操作。等价于count++。默认为0
        jedis.incr("count");
        // 在原有值的基础增加指定的值，等价于count+=10;
        jedis.incrBy("count",10);
        // 浮点数自增
        Double price = jedis.incrByFloat("price",1.2);
        Assert.assertTrue(price == 1.2);
        // 自减1，等价于count--
        jedis.decr("count");
        // 减指定数量的值,等价于count-=5
        jedis.decrBy("count",5);
        Assert.assertEquals(jedis.get("count"),"5");
        // 获取key旧的值，并设置一个新的值
        s = jedis.getSet("count","100");
        Assert.assertEquals(s,"5");
        // 获取指定范围的字符串，0：起始偏移  3：结束位置
        s = jedis.getrange("language",0,3);
        Assert.assertEquals(s, "java");
        // 替换，0：替换起始位置，php：从0开始替换的字符串
        jedis.setrange("language",0,"php");
        Assert.assertEquals(jedis.get("language"),"phpac++");
        // 只有key不存在才保存,key不存在返回1，存在返回0
        long r = jedis.setnx("count","200");
        Assert.assertTrue(r == 0);
        // 获取字符串的长度
        long len = jedis.strlen("language");
        Assert.assertTrue("phpac++".length() == len);

        // 同时设置多个key的值
        jedis.mset("language","java", "count","2000");

        // 获取多个key的值
        List<String> list = jedis.mget("language","count");
        System.out.println(list);
        Assert.assertTrue(list.size() == 2 && list.get(0).equals("java") && list.get(1).equals("2000"));

        // 同时设置多个key，且key不存在
        r = jedis.msetnx("auth","yangxin","age","18");
        Assert.assertTrue(r == 1);

        // 保存带有效期的key，单位为毫秒
        jedis.psetex("expir_key",2000,"abcde");
        try {
            Thread.sleep(2100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        boolean b = jedis.exists("expir_key");
        Assert.assertTrue(!b);

        // 保存带有效期的key，单位为秒
        jedis.setex("expir_key2",3,"test");

    }

    /**
     * 集合
     * 1> 元素没有顺序
     * 2> 元素不可重复
     * 文档：127.0.0.1:6384> help @set
     */
    @Test
    public void testSet() {
        jedis.flushDB();
        // 添加元素，返回添加的元素个数
        long r = jedis.sadd("hobbys","吃","喝","玩","乐","看书","旅游");
        Assert.assertTrue(r == 6);

        // 查询集合元数数量
        r = jedis.scard("hobbys");
        Assert.assertTrue(r == 6);

        // 取多个集合的差集
        r = jedis.sadd("hobbys2","吃","旅游");
        Assert.assertTrue(r == 2);
        Set<String> s = jedis.sdiff("hobbys","hobbys2");
        System.out.println(s);
        Assert.assertTrue(s.size() == 4);

        // 取hoobys和hobbys2的差集，将结果存在hobbys3集合中
        r = jedis.sdiffstore("hobbys3","hobbys","hobbys2");
        Assert.assertTrue(jedis.scard("hobbys3") == r);

        // 取多个集合的交集
        s = jedis.sinter("hobbys","hobbys2");
        System.out.println(s);
        Assert.assertTrue(s.size() == 2);

        // 取多个集合的交集，并将结果存储到一个新的key中
        r = jedis.sinterstore("hobbys4","hobbys","hobbys2");
        Assert.assertTrue(jedis.scard("hobbys4") == r);

        // 判断某个元素是否在集合当中
        boolean b = jedis.sismember("hobbys","吃");
        Assert.assertTrue(b);

        // 获取集合中的的元素
        s = jedis.smembers("hobbys");
        System.out.println(s);
        Assert.assertTrue(s.size() == 6);

        // 从一个集合中移动指定的元素到另外一个集合当中
        r = jedis.smove("hobbys","hobbys2","乐");
        Assert.assertTrue(r == 1);
        Assert.assertTrue(jedis.sismember("hobbys2","乐"));

        // 从集合中随机获取2个元素
        List<String> list = jedis.srandmember("hobbys",2);
        System.out.println("随机获取2个元素：" + list);
        Assert.assertTrue(list.size() == 2);

        // 从集合中随机移除一个或多个元素，返回移除的元素。因为集合中的元素是没有顺序的
        s = jedis.spop("hobbys",2);
        System.out.println(s);
        Assert.assertTrue(s.size() == 2);

        // 从集合中删除一个或多个元素，返回删除的个数
        r = jedis.srem("hobbys","吃","旅游");
        System.out.println(r);
        Assert.assertTrue(!jedis.sismember("hobbys","吃") && !jedis.sismember("hobbys","旅游"));

        // 根据查询关键搜索集合中的元素，0：搜索开始位置，scanParams：搜索规则
        ScanParams scanParams = new ScanParams();
        jedis.sadd("hobbys","java","javascript","php","c++","c","objective-c","node.js","python");
        scanParams.match("java*");
        ScanResult<String> sr = jedis.sscan("hobbys",0,scanParams);
        list = sr.getResult();
        System.out.println(list);
        Assert.assertTrue(list.size() == 2);

        // 取多个集合的并集，如果元素相同，则会覆盖
        Set<String> us = jedis.sunion("hobbys2","hobbys3");
        System.out.println(us);

        // 将多个集合的并集存储到一个新的集合中
        r = jedis.sunionstore("hobbys5","hobbys2","hobbys3");
        Assert.assertTrue(r == jedis.scard("hobbys5"));
        System.out.println(jedis.smembers("hobbys5"));
    }

    /**
     * 自动排序的集合
     * 1> 元素不可重复
     * 2> 元素有序（根据score排序）
     * score值越大，排名越靠后
     */
    @Test
    public void testSortedSet() {
        jedis.flushDB();
        // 添加元素（不需要设置同样的类型）
        jedis.zadd("price",1,"1");
        jedis.zadd("price",5,"10");
        jedis.zadd("price",2,"5.2");
        jedis.zadd("price",6,"6");
        jedis.zadd("price",7,"3");
        jedis.zadd("price",10,"五百");

        // 获取元素列表
        Set<String> set = jedis.zrange("price",0,-1);
        System.out.println(set);

        // 获取指定分值范围的元素数量
        long r = jedis.zcount("price",1,8);
        System.out.println(r);
        Assert.assertTrue(r == 5);

        // 给指定元素的增加分值，返回新的分值
        double s = jedis.zincrby("price",5.5,"3");
        System.out.println(s);
        Assert.assertTrue(s == 12.5);

        // 指定键在集合的分值排名，从0开始算
        r = jedis.zrank("price","3");
        System.out.println(r);
        Assert.assertTrue(r == 5);  // 最高分，默认按升序排列，第最后一名

        // 删除元素，删除成功返回删除的元素数量，失败返回0
        r = jedis.zrem("price","五百","3");
        Assert.assertTrue(r == 2);

        // 获取元素的排名分值
        s = jedis.zscore("price","6");
        Assert.assertTrue(s == 6);

        // 获取元素在集合中的逆序名次（倒数第几名）,从0开始计算
        s = jedis.zrevrank("price","6");
        Assert.assertTrue(s == 0);

        // 获取指定范围的后几个元素(这里是后3个元素)
        set = jedis.zrevrange("price", 0, 2);
        System.out.println(set);    // [6, 10, 5.2]

        // 获取指定分值范围的后几个元素，依移1个元素，取后2个
        set = jedis.zrevrangeByScore("price",5,1,1,2);
        System.out.println(set);    // [5.2, 1]

        /**
         * 模拟搜索
         * 区间必须包含(、[符号：（代表不包含，[代表包含
         * -、+ ： 代表最少和最大范围
         * @see http://redisdoc.com/sorted_set/zrangebylex.html
         */
        jedis.zadd("alpha",0,"a");
        jedis.zadd("alpha",0,"b");
        jedis.zadd("alpha",0,"c");
        jedis.zadd("alpha",0,"d");
        jedis.zadd("alpha",0,"e");
        jedis.zadd("alpha",0,"f");
        jedis.zadd("alpha",0,"k");
        jedis.zadd("alpha",0,"z");

        // 取a~k范围的元素，包含a，不包含k。从第2个开始取3个
        set = jedis.zrangeByLex("alpha","[a","(k",1,3);
        System.out.println(set);    // [b, c, d]

        // 获取指定范围的元素个数
        r = jedis.zlexcount("alpha","[a","[f");
        System.out.println(r);
        Assert.assertTrue(r == 6);

        // 倒着取指定范围的元素
        set = jedis.zrevrangeByLex("alpha","[z","[d",0,3);
        System.out.println(set);    // [z, k, f]

    }

    /**
     * 队列
     * 文档：127.0.0.1:6384> help @list
     */
    @Test
    public void testList() {
        jedis.flushDB();
        // 向头添加元素，返回链表的长度
        long r = jedis.lpush("users","yangxin","yangli","yangfang","huge","guodegang");
        System.out.println(r);
        Assert.assertTrue(r == 5);

        // 向尾添加元素，返回链表的长度
        r = jedis.rpush("users","likaifu","mayun","mahuateng");
        System.out.println(r);
        Assert.assertTrue(r == 8);

        // 返回链表长度
        r = jedis.llen("users");
        System.out.println(r);
        Assert.assertTrue(r == 8);

        // 获取链表元素,0:开始索引，-1：结束索引，-1表示取链表长度
        List<String> list = jedis.lrange("users",0,-1);
        System.out.println(list);
        Assert.assertTrue(list.size() == 8);

        // 删除指定的相同元素数量，返回删除元素个数
        jedis.rpush("users","mahuateng","mahuateng");
        r = jedis.lrem("users",2,"mahuateng");
        System.out.println(r);
        Assert.assertTrue(r == 2);

        // 弹出列表中第一个元素（删除并返回第一个元素）
        String s = jedis.lpop("users");
        System.out.println(s);
        Assert.assertEquals(s,"guodegang");

        // 弹出列表中最后一个元素
        s = jedis.rpop("users");
        System.out.println(s);
        Assert.assertEquals(s,"mahuateng");

        // 往队列头部插入元素，列表必须存在
        r = jedis.lpushx("users2","laoyang");
        System.out.println(r == 0);

        // 修改队列中指定索引的值
        s = jedis.lset("users",0,"yangxin");
        System.out.println(s);
        Assert.assertEquals(s,"OK");

        // 截取并保存链表中指定范围的元素
        s = jedis.ltrim("users",0,5);
        System.out.println(s);

        // 返回指定索引的元素
        s = jedis.lindex("users",0);
        System.out.println(s);
        Assert.assertEquals(s,"yangxin");

        System.out.println("users: " + jedis.lrange("users",0,-1));

        // 弹出users最后一个元素，保存到user2链表中，并返回
        s = jedis.rpoplpush("users","users2");
        System.out.println(jedis.lrange("users2",0,-1));

        // 同上，只是有阻塞超时时长，单位：毫秒
        s = jedis.brpoplpush("users","users2",1);
        System.out.println(jedis.lrange("users2",0,-1));
    }

    /**
     * hash表
     * 文档：127.0.0.1:6384> help @hash
     */
    @Test
    public void testHash() {
        jedis.flushDB();
        // 添加元素，如果key存在则修改，且返回0.如果key不存在，则添加，返回1
        long r = jedis.hset("user", "name","yangxin");
        jedis.hset("user","age","18");  // 18显得我年轻~
        Assert.assertEquals(r,1);

        // 如果key不存在则添加，否则操作失败。key存在返回0,否则返回1
        r = jedis.hsetnx("user","name","郭德纲");
        Assert.assertTrue(r == 0);

        // 获取某一key的值
        String s = jedis.hget("user","name");
        System.out.println(s);
        Assert.assertEquals(s,"yangxin");

        // 获取所有值
        Map<String, String> map = jedis.hgetAll("user");
        for (Map.Entry<String,String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }
        Assert.assertTrue(map.size() == 2);

        // 判断某个key的字段是否存在
        boolean b = jedis.hexists("user","sex");
        Assert.assertTrue(!b);

        // 对某个field累加指定的整数值，返回累加后的值
        r = jedis.hincrBy("user","age",10); // 还是回归现实吧~^^
        System.out.println(r);
        Assert.assertTrue(r == 28);

        // 对某个field累加指定的浮点数值，返回累加后的值
        double f = jedis.hincrByFloat("user","height",1.75);
        System.out.println(f);
        Assert.assertTrue(f == 1.75);

        // 获取hash的字段数量
        r = jedis.hlen("user");
        Assert.assertEquals(r,3);

        // 获取多个字段的值
        List<String> list = jedis.hmget("user","name","age");
        System.out.println(list);
        Assert.assertTrue(list.size() == 2);

        // 获取所有字段的值
        list = jedis.hvals("user");
        System.out.println(list);
        Assert.assertTrue(list.size() == 3);

        // 删除字段
        r = jedis.hdel("user","name","age");
        System.out.println(r);
        Assert.assertTrue(r == 2);

        // 查找字段
        jedis.hset("user","js1","value1");
        jedis.hset("user","xxxjs2","value2");
        jedis.hset("user","abcjs3","value3");
        jedis.hset("user","1abcjs","value4");
        jedis.hset("user","2abc2js3","value5");
        jedis.hset("user","name","yangxin");
        ScanParams scanParams = new ScanParams();
        scanParams.match("*js*");    // 查找包含js的字段
        ScanResult<Map.Entry<String, String>> sr = jedis.hscan("user","0",scanParams);
        System.out.println(sr.getResult().size());
        Assert.assertTrue(sr.getResult().size() == 5);
        for (Iterator<Map.Entry<String, String>> iterator = sr.getResult().iterator(); iterator.hasNext();) {
            Map.Entry<String, String> entry = iterator.next();
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }

    }

    /**
     * 消息发布
     * 文档：127.0.0.1:6384> help @pubsub
     */
    @Test
    public void testPublish() {
        // 发布一个事件
        long r = jedis.publish("event_msg","message_" + System.currentTimeMillis());
        System.out.println(r);
    }

    /**
     * 消息订阅
     * 文档：127.0.0.1:6384> help @pubsub
     */
    @Test
    public void testSubscribe() {
        // 订阅一个事件
        JedisPubSub sub = new JedisPubSub() {

            private int count;

            /**
             * 收到订阅消息回调
             * @param channel 订阅的消息通道
             * @param message 消息内容
             */
            @Override
            public void onMessage(String channel, String message) {
                count++;
                if (count > 3) {
                    unsubscribe(channel);
                    count = 0;
                    return;
                }
                System.out.println("onMessage--->channel: " + channel + ", message: " + message);
            }

            // 消息订阅成功回调
            @Override
            public void onSubscribe(String channel, int subscribedChannels) {
                System.out.println("onSubscribe---->channel:" + channel + ", subscribedChannels: " + subscribedChannels);
            }

            /**
             * 消息取消息订阅回调
             */
            @Override
            public void onUnsubscribe(String channel, int subscribedChannels) {
                System.out.println("onUnsubscribe--->channel: " + channel + ", subscribedChannels: " + subscribedChannels);
            }

            /**
             * 收到模式匹配主题消息回调（可以用*通配符匹配）
             * @param pattern 模式匹配主题
             * @param channel 发送消息的具体主题
             * @param message 消息内容
             */
            @Override
            public void onPMessage(String pattern, String channel, String message) {
                count++;
                if (count > 3) {
                    punsubscribe();
                    count = 0;
                    return;
                }
                System.out.println("onPMessage---->pattern: " + pattern+ ", channel:" + channel + ", message: " + message);
            }

            /**
             * 模式主题订阅成功回调
             * @param pattern 模式匹配主题
             * @param subscribedChannels
             */
            @Override
            public void onPSubscribe(String pattern, int subscribedChannels) {
                System.out.println("onPSubscribe---->pattern:" + pattern + ", subscribedChannels: " + subscribedChannels);
            }

            /**
             * 模式主题取消订阅回调
             * @param pattern 模式匹配主题
             * @param subscribedChannels
             */
            @Override
            public void onPUnsubscribe(String pattern, int subscribedChannels) {
                System.out.println("onUnsubscribe--->pattern: " + pattern + ", subscribedChannels: " + subscribedChannels);
            }
        };
        // 订阅event_msg,event_msg2,event_msg3 三个消息
        //jedis.subscribe(sub,"event_msg","event_msg2","event_msg3");

        // 订阅模式匹配消息
        jedis.psubscribe(sub,"event.msg.*", "event.doc.*");
    }

    /**
     * 事务
     */
    @Test
    public void testTransaction() {
        jedis.flushDB();
        Transaction transaction = jedis.multi();    // 开始执行事务
        transaction.hset("user","name","yangxin");
        transaction.hset("user","age","28");
        transaction.hset("user","sex","man");
        transaction.hset("user","height","1.75");
        transaction.hset("user","weight","65");
        List<Object> objects = transaction.exec();  // 执行事务
        System.out.println(objects.toString());
    }

    /**
     * 批处理
     * 10万个key是单个key存储速度的10倍以上
     */
    @Test
    public void testPipleLine() {
        Pipeline pipeline = jedis.pipelined();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            pipeline.set("key" + i, "value" + i);
            pipeline.hset("user", "key" + i, "vlaue" + i);
            if (i % 1000 == 0) {
                pipeline.sync();
            }
        }
        pipeline.sync();
        long end = System.currentTimeMillis() - start;
        System.out.println(end);
    }

    /**
     * key
     */
    @Test
    public void testKey() {
        jedis.flushDB();
        jedis.hset("user","username","yangxin");
        jedis.set("name","yangxin");
        // 删除key，返回删除key的数量
        long ret = jedis.del("user","name","language");
        System.out.println(ret);
        Assert.assertTrue(ret == 2);    // language这个key不存在

        jedis.set("name","yangxin");
        jedis.set("age","28");
        jedis.set("sex","man");
        jedis.set("height","175");
        jedis.set("weight", "65kg");
        jedis.sadd("hobbys","吃","喝","玩","乐");
        // 随机返回一个key
        String key = jedis.randomKey();
        System.out.println(key);

        // 序列化指定key的值
        byte[] value = jedis.dump("name");
        System.out.println(new String(value));

        // 查询指定的key是否存在
        boolean b = jedis.exists("name");
        Assert.assertTrue(b);

        // 设置指定key的有效期，单位：秒。返回1设置成功，0设置失败（超时或key不存在）
        ret = jedis.expire("name",60 * 10);
        System.out.println(ret);
        Assert.assertTrue(ret == 1);

        // 指定key有效期（1970.1.1 0:0:0以来的秒数）
        Calendar expire = Calendar.getInstance();
        expire.add(Calendar.SECOND, 5);
        ret = jedis.expireAt("age",expire.getTimeInMillis()/1000);
        Assert.assertTrue(ret == 1);

        /**
         * 移动一个key到指定主机指定的数据库
         * 注意：如果目标主机数据库中的key已经存在，则报错
         * arg1: 目标主机IP
         * arg2: 目标主机端口
         * arg3：当前实例的key
         * arg4: 目标主机的数据库序号
         * arg5: 移动超时时长
         */
        String s = jedis.migrate("192.168.0.201",6385,"age",0,5000);
        Assert.assertEquals(s,"OK");

        /**
         * 在当前redis实例移动指定的key到指定序号数据库中
         * 移动成功返回1，否则返回0
         */
        long r = jedis.move("weight",1);
        //Assert.assertTrue(r == 1);
        System.out.println(r);

        //------------------debug 排错------------------
        r = jedis.objectRefcount("height");     // 查看一个对象被引用的次数
        System.out.println(r);
        s = jedis.objectEncoding("height");     // 查看key在redis内部存储的数据类型
        System.out.println(s);
        r = jedis.objectIdletime("height");         // 查看自存储后的空闲时间
        System.out.println(r);

        // 获取一个key的剩余生命周期，以秒为单位，-1：表示没有有效期限制
        r = jedis.ttl("age");
        System.out.println(r);

        // 获取一个key的剩余生命周期，以毫秒为单位, -1：表示没有有效期限制
        r = jedis.pttl("age");
        System.out.println(r);

        // key重命名，如果两个key相同重命名失败，如果新key已经存在，则覆盖
        s = jedis.rename("age","newAge");
        System.out.println(s);

        // 如果新key已经存在，重命名失败
        r = jedis.renamenx("newAge","age");
        System.out.println(r);

        // 搜索key
        ScanParams sp = new ScanParams();
        sp.match("age");
        ScanResult<String> result = jedis.scan(0,sp);
        System.out.println(result.getResult());
        Assert.assertTrue(result.getResult().size() == 1);

        // 对set、list进行排序（只能是数值）
        jedis.sadd("num","1","0","3","10","10.2","8","5","2.2","2");
        System.out.println(jedis.smembers("num"));
        System.out.println(jedis.sort("num"));  // 默认升序
        SortingParams params = new SortingParams();
        params.desc();  // 降序
        System.out.println(jedis.sort("num",params));

        // 将排序结果保存到一个新的列表中（list）
        params.asc();
        System.out.println(jedis.sort("num",params,"sorted_num"));

    }

    @After
    public void after() {
        jedis.close();
    }
}