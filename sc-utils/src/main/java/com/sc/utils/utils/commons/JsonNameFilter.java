package com.sc.utils.utils.commons;

import com.alibaba.fastjson.serializer.NameFilter;

import java.util.Map;


public class JsonNameFilter implements NameFilter {

    private Map<String, String> map;

    public void setKeys(Map<String, String> map) {
        this.map = map;
    }

    @Override
    public String process(Object object, String name, Object value) {
        if (map.containsKey(name)) {
            return map.get(name);
        }
        return name;
    }
}
