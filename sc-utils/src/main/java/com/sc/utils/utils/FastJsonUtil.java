package com.sc.utils.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.sc.utils.collection.Collections3;

import java.util.HashMap;
import java.util.Map;


public class FastJsonUtil {

    public static Object textToJson(String text) {
        Object objectJson = JSON.parse(text);
        return objectJson;
    }


    public static Map stringToCollect(String s) {
        Map m = (Map) JSONObject.parseObject(s);
        return m;
    }


    public static String collectToString(Map m) {
        String s = JSONObject.toJSONString(m);
        return s;
    }


    public static Map<String, Object> jsonToMap(String json) {
        JSONObject jsonObject = (JSONObject) JSON.parse(json);
        Map<String, Object> map = new HashMap<String, Object>(jsonObject);
        return map;
    }


    public static JSONObject jsonToObject(String json) {
        JSONObject jsonObject = (JSONObject) JSON.parse(json);
        return jsonObject;
    }

    /**
     * 将JSONArray转换成Map[{"aa":xx,"cc":xx,"bb":xx},{"aa":xx,"cc":xx,"bb":xx}]
     * 注意一定是ListJson对象转换
     * @param json
     * @param jsonPath
     * @param keyPropertyName
     * @param valuePropertyName
     * @return
     */
    public static Map jsonPathToMap(final String json,final String jsonPath, final String keyPropertyName, final String valuePropertyName) {
        JSONArray jsonArray = (JSONArray) JSONPath.read(json, jsonPath);
        return Collections3.extractToMap(jsonArray, keyPropertyName, valuePropertyName);
    }

}
