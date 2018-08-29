
package com.sc.ruleengine.core;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unchecked")
public class RuleContextDefault implements RuleContext, Serializable {

    private Map<String, Object> itemMap = new ConcurrentHashMap<String, Object>();

    @Override
    public <T> T put(final String name, final T object) {
        return (T) itemMap.put(name, object);
    }

    @Override
    public <T> T remove(final String name) {
        return (T) itemMap.remove(name);
    }

    @Override
    public <T> T get(final String name) {
        return (T) itemMap.get(name);
    }

    @Override
    public void putAll(Map<String, Object> map) {
        this.itemMap.putAll(map);
    }

    @Override
    public <T> T get(final String name, final T defaultValue) {
        T result = (T) itemMap.get(name);
        if (result == null) {
            result = defaultValue;
        }
        return result;
    }

    @Override
    public boolean exist(String name) {
        return itemMap.containsKey(name);
    }

    @Override
    public RuleContext contain(String name) {
        if (itemMap.containsKey(name)) {
            return this;
        }
        return null;
    }

    @Override
    public void clear() {
        itemMap.clear();
    }

    @Override
    public Map<String, Object> getItemMap() {
        return itemMap;
    }

    public void setItemMap(Map<String, Object> itemMap) {
        this.itemMap = itemMap;
    }

    @Override
    public int size() {
        return itemMap.size();
    }

    @Override
    public boolean renameKey(String key, String newKey) {
        if (itemMap.containsKey(key)) {
            itemMap.put(newKey, itemMap.get(key));
            itemMap.remove(key);
            return true;
        }
        return false;
    }
}
