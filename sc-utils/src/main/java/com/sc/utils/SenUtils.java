package com.sc.utils;

import com.google.common.base.MoreObjects;

import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <一句话功能简述> <功能详细描述>
 *
 * @auth:qiss
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class SenUtils {

    /**
     * 组织对象的toString方法
     */
    public static <T> String getToString(Class<T> czt, Object object) {
        Field fields[] = czt.getDeclaredFields();
        Field.setAccessible(fields, true);
        MoreObjects.ToStringHelper stringHelper = MoreObjects.toStringHelper(czt);
        try {
            for (Field field : fields) {
                stringHelper.add(field.getName(), field.get(object));
            }
        } catch (IllegalAccessException e) {
          //do nothing
        }
        return stringHelper.toString();
    }

    /**
     * 对象属性copy
     */
    public static <T> T copy(Object source, Class<T> czt) {
        T t = null;
        try {
            t = czt.newInstance();
            BeanUtils.copyProperties(source, t);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
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

}
