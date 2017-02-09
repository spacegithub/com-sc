package com.sc.utils;

import com.google.common.base.MoreObjects;

import com.alibaba.fastjson.JSON;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 工具类
 *
 * @auth:qiss
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class CommonsUtils {

    /**
     * 组织对象的toString方法
     */
    public static <T> String StringValue(Class<T> czt, Object object) {
        if (object instanceof List) {
            return czt.getSimpleName() + "{" + Arrays.toString(((List) object).toArray()) + "}";
        }
        if (object instanceof Set) {
            return czt.getSimpleName() + "{" + Arrays.toString(((Set) object).toArray()) + "}";
        }
        if (object instanceof Map) {
            StringBuilder sb = new StringBuilder("Map{");
            for (Map.Entry<Object, Object> entry : ((Map<Object, Object>) object).entrySet()) {
                sb.append("[" +  entry.getKey()+ "," + StringValue(entry.getValue().getClass(), entry.getValue()) + "]");
            }
            return sb.append("}").toString();
        }
        Field fields[] = czt.getDeclaredFields();
        Field.setAccessible(fields, true);
        MoreObjects.ToStringHelper stringHelper = MoreObjects.toStringHelper(czt);
        try {
            for (Field field : fields) {
                stringHelper.add(field.getName(), field.get(object));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return stringHelper.toString();
    }

    /**
     * 不为空且长度比如为传入的size
     *
     * @param size 集合长度
     */
    public static boolean isNotEmpty(Collection collection, long size) {
        return collection != null && !collection.isEmpty() && collection.size() == size;
    }

    /**
     * 根据传入的参数获取一个map
     *
     * @param strings map的key名称
     * @param objects map的value
     */
    public static Map<String, Object> getMap(String[] strings, Object... objects) {
        Map<String, Object> map = new HashMap();
        if (strings.length > objects.length) {
            return map;
        }
        for (int i = 0; i < strings.length; i++) {
            map.put(strings[i], objects[i]);
        }
        return map;
    }

    /**
     * 传入多个值获取一个list
     *
     * @param ts 传入的对象
     */
    public static <T> List<T> transList(T... ts) {
        List<T> ls = new ArrayList<>();
        for (T t : ts) {
            ls.add(t);
        }
        return ls;
    }

    /**
     * 判断所有的Object
     * 如果是字符串则空字符串也算
     */
    public static boolean isEmpty(Object obj) {
        return obj == null ? true : (obj.getClass().isArray() ? Array.getLength(obj) == 0 : (obj instanceof CharSequence ? ((CharSequence) obj).length() == 0 : (obj instanceof Collection ? ((Collection) obj).isEmpty() : (obj instanceof Map ? ((Map) obj).isEmpty() : false))));
    }

    /**
     * 转换分到元
     */
    public static String changeF2Y(Long amount) throws Exception {
        return BigDecimal.valueOf(amount).divide(new BigDecimal(100)).toString();
    }

    /**
     * 转为json
     */
    public static String toJson(Object obj) {
        return JSON.toJSONString(obj);
    }

    /**
     * 从json中取对应的key值
     *
     * @param clazz 对应的转换为的类型
     */
    public static <T> T findInJson(String json, String key, Class<T> clazz) {

        return (T) JSON.parseObject(json).getObject(key, clazz);
    }

    /**
     * 如果地一个为空则取第二个值,第二个值必须为非空否则抛出NullPointerException异常
     */
    public static <T> T firstNonNull(T first, T second) {
        if (second == null) {
            throw new NullPointerException();
        }
        return first != null ? first : second;
    }

}
