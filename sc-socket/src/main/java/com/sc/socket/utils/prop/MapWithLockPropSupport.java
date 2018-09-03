package com.sc.socket.utils.prop;

import com.sc.socket.utils.lock.MapWithLock;

import java.util.HashMap;

/**
 * 自带读写锁,且支持属性操作
 * 2017年8月18日 下午5:36:02
 */
public class MapWithLockPropSupport implements IPropSupport {

    /**
     * 线程安全的map,操作对象自带读写锁
     */
    private final MapWithLock<String, Object> props = new MapWithLock<>(new HashMap<String, Object>(8));


    public MapWithLockPropSupport() {
    }

    @Override
    public void clearAttribute() {
        props.clear();
    }

    @Override
    public Object getAttribute(String key) {
        return props.getObj().get(key);
    }


    @Override
    public void removeAttribute(String key) {
        props.remove(key);
    }


    @Override
    public void setAttribute(String key, Object value) {
        props.put(key, value);
    }
}
