package com.sc.socket.utils.prop;

/**
 * 属性支持接口,对属性的一些操作
 *
 */
public interface IPropSupport {
	/**
	 * 清除所有属性
	 */
	public void clearAttribute();

	/**
	 * 获取属性
	 * @param key
	 * @return
	 *
	 */
	public Object getAttribute(String key);

	/**
	 * 删除属性
	 * @param key
	 *
	 */
	public void removeAttribute(String key);

	/**
	 * 设置属性
	 * @param key
	 * @param value
	 *
	 */
	public void setAttribute(String key, Object value);
}
