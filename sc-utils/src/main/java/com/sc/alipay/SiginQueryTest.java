package com.sc.alipay;

import java.util.HashMap;
import java.util.Map;

/**
 * <一句话功能简述>
 * <功能详细描述>
 *
 * @auth:qiss
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class SiginQueryTest {

    public static void main(String[] args)throws Exception {
        String partner = "2088911501117604";
        // 商户的私钥
        String key = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMWmkzyWFAIy3u1r3Z/oQhOMTTnW5bQmFdP7VMBfRVUUyQimFO6sPwsEomrqhzRvowWUEGjDSAwfdZIX+eZnNBa/4uaNxH3olopnD9cUDg28HN5DEKY+4aZHDDF3/KHdjL32eKZrqM+5uvpK7cCCNsnZZkEOnVOdXXanpwI49LU9AgMBAAECgYAIQedQ1qxLUzjBDoqZzahFDM2FJxc9qYGr84oc514MKewkMlwZhJS1ryHh+Z5gcHGNIBx3ZAE0kHPnAJ1uJM2mOtXFCH+8eYY1T2YwnKaeONLhuL0ecXRy0qtnx5kEt7VOZ4N7rSkULZPvnsAlAjQfWcbay6vV2RZ6L+U7xj+lAQJBAO7XCuURKp6gr61Q9fL04BoRxqsOxUTQ9Tmz/OtY8Wf3OGvI9U70JMZI0jG9UFiJLEH/nO4jhepDQjozjVUji10CQQDT2fEjfHGm1ZzdsimbF8mkLyG6Jjud+pmSpxOLQXxn7lXH2FiaHFOPw/MdI17tLBFaO8PeCFc+YqQHx0MyaRNhAkEA6vDH+8qmJQIlrZSNS2AMLji8N7pA3M+72fYuXgfQ+Bcc/hHhFLhekFpDwb/bECxnR8i8wxhYe7eLpxrc9RoQ1QJAGPSLW0rFeImhONxL2mEq95Q1vN/UFf/3JzdXLghAx11j7W6mwUCk90J12PDZtcaDJMX1hKom3Mpx2Bi46a9HAQJAPlpvvQBobq4vjO0z2jn+u5/nEa0WYE76lu/19DOnZ554LwO1V5cJxHOV5fVtx5WSspsMgVVSzfoxqi1HiMHfKg==";
        String alipayGateway = "https://mapi.alipay.com/gateway.do?";
        String serviceName = "single_trade_query";
        AliPayCore aliPayCore = new AliPayCore(new AliPayConf(partner, key, alipayGateway, serviceName));
        Map<String,String> map=new HashMap<>();
        map.put("out_trade_no","CJIA145205p32346");
        //map.put("","");
        System.out.println("-->" +  aliPayCore.buildRequest(map));
    }
}
