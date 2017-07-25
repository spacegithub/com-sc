package com.sc.utils.utils.commons;

import com.alibaba.fastjson.serializer.NameFilter;

import java.util.Map;

/**
 * <一句话功能简述>
 * <功能详细描述>
 * <p>
 * fastjson支持定制序列化，提供定制序列化的方式包括：
 * // 根据PropertyName判断是否序列化
 * public interface PropertyPreFilter extends SerializeFilter {
 * boolean apply(JSONSerializer serializer, Object object, String name);
 * }
 *
 * // 根据PropertyName和PropertyValue来判断是否序列化
 * public interface PropertyFilter extends SerializeFilter {
 * boolean apply(Object object, String propertyName, Object propertyValue);
 * }
 *
 * // 修改Key，如果需要修改Key,process返回值则可
 * public interface NameFilter extends SerializeFilter {
 * String process(Object object, String propertyName, Object propertyValue);
 * }
 *
 * // 修改Value
 * public interface ValueFilter extends SerializeFilter {
 * Object process(Object object, String propertyName, Object propertyValue);
 * }
 *
 * // 序列化时在最前添加内容
 * public abstract class BeforeFilter implements SerializeFilter {
 * protected final void writeKeyValue(String key, Object value) { ... }
 * // 需要实现的抽象方法，在实现中调用writeKeyValue添加内容
 * public abstract void writeBefore(Object object);
 * }
 *
 *
 * // 序列化时在最前添加内容
 * public abstract class AfterFilter implements SerializeFilter {
 * protected final void writeKeyValue(String key, Object value) { ... }
 * // 需要实现的抽象方法，在实现中调用writeKeyValue添加内容
 * public abstract void writeAfter(Object object);
 * }
 *
 * </p>
 *
 * @auth:qiss
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
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
