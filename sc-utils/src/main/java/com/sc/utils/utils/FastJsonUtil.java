package com.sc.utils.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * FastJson工具包
 *
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class FastJsonUtil {
    /**
     * 将string转化为序列化的json字符串
     * @param
     * @return
     */
    public static Object textToJson(String text) {
        Object objectJson  = JSON.parse(text);
        return objectJson;
    }

    /**
     * json字符串转化为map
     * @param s
     * @return
     */
    public static Map stringToCollect(String s) {
        Map m = (Map) JSONObject.parseObject(s);
        return m;
    }

    /**
     * 将map转化为string
     * @param m
     * @return
     */
    public static String collectToString(Map m) {
        String s = JSONObject.toJSONString(m);
        return s;
    }

    /**
     * 返回解析json后对应的Map
     * @param json
     * @return
     */
    public static Map<String, Object> jsonToMap(String json){
        JSONObject jsonObject= (JSONObject) JSON.parse(json);
        Map<String, Object> map=new HashMap<String,Object>(jsonObject);
        return map;
    }


    /**
     * 返回解析json后对应的Object
     * @param json
     * @return
     */
    public static JSONObject jsonToObject(String json){
        JSONObject  jsonObject = (JSONObject) JSON.parse(json);
        return jsonObject;
    }
}
