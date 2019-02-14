# com-sc

[![License](http://img.shields.io/:license-apache-blue.svg)](https://github.com/Senssic/com-sc/blob/master/LICENSE)
[![JDK 1.8](https://img.shields.io/badge/JDK-1.8-blue.svg)](#com-sc)

后端开发项目中经常使用到的开发工具箱,可以作为日常开发的基础工具箱.包括基础实体,代码模板生成,excle操作工具,邮箱发送工具,mybatis代码生成器,redis操作封装,简单规则引擎,简单socket封装.基于hessian和restful的简单测试封装,模拟线程池,和常用工具类等

## 模块说明

**sc-base-api:** 基本实体模块,包括统一请求响应分页相关以及业务请求头的规范

**sc-code-generator:** 基于velocity的代码模板生成代码,可以扩展生成各种模板的代码

**sc-excle:** 对于excle操作的一些封装

```java
//1.读取excle
ExcelService.readExcel4TargetDispatch(new FileInputStream("c:\\aa.txt"));
//2.读取excle,并校验excle
ExcelService.readExcel4TargetDispatch(new FileInputStream("c:\\aa.txt"), new ExcleValidate() {
    @Override
    public Boolean validate(Sheet sheet) {
        return true;
    }
});
//3.写入excle
List<Object> title = new ArrayList<>();
title.add("标题ssssssssssssssssssssssssssssssssssssssssssssssssss1");
title.add("标题2");
title.add("标题2");
title.add("标题2");
title.add("标题2");
title.add("标题2");
title.add("标题2");
title.add("标题2");
title.add("标题ssssssssssssssssssssssssssssssssssssssssssssssssss2");
List<Object> body = new ArrayList<>();
body.add("身体1");
body.add(2);
body.add(null);
body.add(null);
body.add(null);
body.add(null);

body.add(3);
List<List<Object>> listList = new ArrayList<>();
listList.add(title);
listList.add(body);
FileOutputStream fileOutputStream = new FileOutputStream("F:\\a.xlsx");
ExcelService.writerExcel4TargetDispatch(fileOutputStream, listList);
```

**sc-hessian:**  基于hessian的RPC调用封装

**sc-mail:**  对email的使用封装

```java
MailSenderInfo mailInfo = new MailSenderInfo();
mailInfo.setMailServerHost("smtp.163.com");
mailInfo.setMailServerPort("25");
mailInfo.setValidate(true);
mailInfo.setUserName("XXX@163.com");
mailInfo.setPassword("xxx");
mailInfo.setFromAddress("XXX@163.com");
mailInfo.setToAddress("XXX@qq.com");
mailInfo.setSubject("物流预警");
mailInfo.setContent("亲,这些都是超时的信息,请你查收");
mailInfo.setFileName("预警清单.wps");
mailInfo.setByt(new String("aaff").getBytes());

ComplexMailSender sms = new ComplexMailSender();
sms.sendAttachmentMail(mailInfo);
```

**sc-message-queue:** 对于rabbit和spring集成的简单封装

**sc-mybatis-generator:** 对mybatis生成插件进行处理,使生成的实体和mapper更符合使用要求,可以扩展定制.

**sc-pay-channel:** 支付宝支付简单封装测试,以后可能会集成多个支付渠道

**sc-redis:** 对于redis的简单封装

```java
//1.redis的基本操作封装
RedisTemplate redisTemplate=new RedisTemplate(new JedisPool("localhost", 6379));
redisTemplate.get("aa");
redisTemplate.hGetAll("bb");

//2.基于redisson的锁
Redisson redisson = RedissonManager.getInstance().getRedissonClient();
RLock lock = redisson.getLock("1000");
lock.lock();
System.out.println("redisson = " + redisson);
lock.unlock();
redisson.shutdown();
```

**sc-rule-engine:** 简单的规则引擎封装

*规则文件:*

```xml
<rule-set name="feerule">
    <rule id="step2" multipleTimes="false" exclusive="true" type="FEE_RULE">
        <rule-eval>
            <condition><![CDATA[salary>3500 && salary<=5000]]></condition>
            <action><![CDATA[(salary-3500)*0.03]]></action>
        </rule-eval>
    </rule>
    <rule id="step3" multipleTimes="false" exclusive="true" type="FEE_RULE">
        <rule-eval>
            <condition><![CDATA[salary>5000 && salary<=8000]]></condition>
            <action><![CDATA[(salary-3500)*0.1-105]]></action>
        </rule-eval>
    </rule>
    <rule id="step4" multipleTimes="false" exclusive="true" type="FEE_RULE">
        <rule-eval>
            <condition><![CDATA[salary>8000 && salary<=12500]]></condition>
            <action><![CDATA[(salary-3500)*0.2-555]]></action>
        </rule-eval>
    </rule>
    <rule id="step5" multipleTimes="false" exclusive="true" type="FEE_RULE">
        <rule-eval>
            <condition><![CDATA[salary>12500 && salary<=38500]]></condition>
            <action><![CDATA[(salary-3500)*0.25-1005]]></action>
        </rule-eval>
    </rule>
    <rule id="step6" multipleTimes="false" exclusive="true" type="FEE_RULE">
        <rule-eval>
            <condition><![CDATA[salary>38500 && salary<=58500]]></condition>
            <action><![CDATA[(salary-3500)*0.3-2755]]></action>
        </rule-eval>
    </rule>
    <rule id="step7" multipleTimes="false" exclusive="true" type="FEE_RULE">
        <rule-eval>
            <condition><![CDATA[salary>58500 && salary<=83500]]></condition>
            <action><![CDATA[(salary-3500)*0.35-5505]]></action>
        </rule-eval>
    </rule>
    <rule id="step8" multipleTimes="false" exclusive="true" type="FEE_RULE">
        <rule-eval>
            <condition><![CDATA[salary>83500]]></condition>
            <action><![CDATA[(salary-3500)*0.45-13505]]></action>
        </rule-eval>
    </rule>
</rule-set>
```

*规则示例:*

```java
String feerulexml = Class.class.getClass().getResource("/").getPath() + "feerule.xml";
XStream xstream = new XStream(new DomDriver());
xstream.autodetectAnnotations(true);
xstream.processAnnotations(new Class[]{RuleSet.class, Rule.class});
RuleEngine ruleEngine = new RuleEngineDefault();

ruleEngine.addRules((RuleSet) xstream.fromXML(FileUtils.readFileToString(new File(feerulexml), "utf-8")));
ruleEngine.addRuleExecutors(Arrays.<RuleExecutor>asList(new FeeRuleExecutor()));
RuleContext ruleContext = new RuleContextDefault();
ruleContext.put("salary", 5100);
ruleEngine.execute(ruleContext, "feerule");
System.out.println("-->" + ruleContext.get("totalSalary"));
```

**sc-socket:** 对于网络编程的封装,从TIO项目copy过来的

**sc-test:** 轻量级的dubbu,hessian,rabbitmq和restful的测试工具类集合

*dubbo客户端*

```java
TestService testService = invokeDubbo("1.0.1", "207.246.117.90:2181", TestService.class);
System.out.println("-->" + testService.helloWorld("stest"));
```

*hessian客户端*

```java
URLExec urlExec = URLExec.instance("8087", "/xxx");
ComHessianUtils.postHessian(urlExec, requestObj, IService.class);
```

*restful客户端*

```java
URLExec urlExec = URLExec.instance("8082", "/xxx/xxx/");

ComRestUtils.postUrl(urlExec, "{}");
```

*rabbitmq客户端*

```java
RabbitSendUtils rabbitSendUtils = RabbitSendUtils.init("10.1.41.137", "56721", "admin", "xxx", "test.vhost.A");
Map<String, Object> map = new JSONObject();
map.put("bizLine", "1");
map.put("orderId", "170194");
map.put("random", UUID.randomUUID().toString().replace("-", ""));
rabbitSendUtils.send("test.exchange.topic", "topic.order", map);
```

**sc-thread-pool:** 模拟线程池处理

**sc-utils:** 常用工具类

- com.sc.utils

  - beanutils *bean的一些工具类,包括验证,转换,过滤,检查,复制,创建等*

  - classcan   类扫描器,支持扫描和匹配目录下所有接口和类

  - collection 对于集合的一些操作包括转换,差并集,过滤排序,分页映射等基本操作

  - enctypt      ASC加密解密,base64操作,url编解码,MD5加密验证,RSA加解密

  - exception  对于异常的一些封装,包括获取根异常,异常原因,异常信息

  - mapper     对于bean的转换和xml以及json和bean的互转进行封装

  - qrcode     生成二维码和一维码

  - reflect       反射的工具类封装,以及获取对于Class对象的封装,获取类路径,加载类,判断类类型

  - rest            对于restful的请求进行封装

  - spring      对于运行时的spring容器的一些常用操作,获取属性值,获取代理类等

  - utils

    - commons  最基本常用的工具类,包括转json,判空,读取json, 费波纳茨,提取属性金额转换等等
    - file                对于文件目录的一些处理封装,包括配置文件获取,文件压缩,文件属性文件目录拷贝创建等
    - regex           封装了常用的对于正则的匹配和处理
    - webutil       封装对于请求体和请求url的一些处理,包括验证码,获取路径返回http code转发,重定向等
    - xml              对于XML一些处理,包括解析,转换,修改,映射,提取转换

    其他金额的处理,JSON的处理,时间的处理,SSH命令的封装等.

  - web 结合spring容器自定义注解,封装对入参属性的提取和响应的json转换.



# 呜谢

有部分代码参考网上的实现,便于统一更新了包名

- [tio](https://github.com/tio/tio)
- [easyexcel](https://github.com/alibaba/easyexcel)