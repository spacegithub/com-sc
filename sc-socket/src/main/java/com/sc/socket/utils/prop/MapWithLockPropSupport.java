package com.sc.socket.utils.prop;

import com.sc.socket.utils.lock.MapWithLock;

import java.util.HashMap;

/**
 *
 * 2017年8月18日 下午5:36:02
 */
public class MapWithLockPropSupport implements IPropSupport {

	private final MapWithLock<String, Object> props = new MapWithLock<>(new HashMap<String, Object>(8));

	/**
	 *
	 *
	 */
	public MapWithLockPropSupport() {
	}

	/**
	 *
	 *
	 */
	@Override
	public void clearAttribute() {
		//initProps();
		props.clear();
	}

	/**
	 *
	 * @param key
	 * @return
	 *
	 */
	@Override
	public Object getAttribute(String key) {
		//initProps();
		return props.getObj().get(key);
	}

//	private void initProps() {
//		if (props == null) {
//			synchronized (this) {
//				if (props == null) {
//					props = new MapWithLock<>(new HashMap<String, Object>(10));
//				}
//			}
//		}
//	}

	/**
	 * @param key
	 *
	 */
	@Override
	public void removeAttribute(String key) {
		//initProps();
		props.remove(key);
	}

	/**
	 *
	 * @param key
	 * @param value
	 *
	 */
	@Override
	public void setAttribute(String key, Object value) {
		//initProps();
		props.put(key, value);
	}
}
