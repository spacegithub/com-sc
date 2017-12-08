package com.sc.utils.beanmap;

import com.google.common.base.CaseFormat;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class BeanUtil extends BeanUtils {

    /**
     * 将对象转为Map
     */
    public static Map<String, Object> object2Map(Object object) {
        Map<String, Object> data = new TreeMap<String, Object>(); //对key进行排序
        try {
            BeanInfo info = Introspector.getBeanInfo(object.getClass(), Introspector.IGNORE_ALL_BEANINFO);
            PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
            for (PropertyDescriptor pd : descriptors) {
                // 将 驼峰式写法转成下划线写法
                String name = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, pd.getName());
                Object value = pd.getReadMethod().invoke(object);
                if ("class".equals(name) || value == null)
                    continue;
                data.put(name, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 将map转为对象
     * @param clazz
     * @param data
     * @param <T>
     * @return
     */
    public static <T> T map2Object(Class<T> clazz, Map<String, Object> data) {
        try {
            T object = clazz.newInstance();
            BeanInfo info = Introspector.getBeanInfo(object.getClass(), Introspector.IGNORE_ALL_BEANINFO);
            PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
            for (PropertyDescriptor pd : descriptors) {
                String name = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, pd.getName());

                Object value = data.get(name);
                if (value == null){
                    value = data.get(name+"_0");
                }
                if (value == null) {
                    continue;
                }
                pd.getWriteMethod().invoke(object, value);

            }
            return object;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将源对象中的值覆盖到目标对象中，仅覆盖源对象中不为NULL值的属性
     *
     * @param dest
     *            目标对象，标准的JavaBean
     * @param orig
     *            源对象，可为Map、标准的JavaBean
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public static void applyIf(Object dest, Object orig) throws Exception {
        try {
            if (orig instanceof Map) {
                Iterator names = ((Map) orig).keySet().iterator();
                while (names.hasNext()) {
                    String name = (String) names.next();
                    if (PropertyUtils.isWriteable(dest, name)) {
                        Object value = ((Map) orig).get(name);
                        if (value != null) {
                            PropertyUtils.setSimpleProperty(dest, name, value);
                        }
                    }
                }
            } else {
                java.lang.reflect.Field[] fields = orig.getClass().getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    String name = fields[i].getName();
                    if (PropertyUtils.isReadable(orig, name) && PropertyUtils.isWriteable(dest, name)) {
                        Object value = PropertyUtils.getSimpleProperty(orig, name);
                        if (value != null) {
                            PropertyUtils.setSimpleProperty(dest, name, value);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new Exception("将源对象中的值覆盖到目标对象中，仅覆盖源对象中不为NULL值的属性", e);
        }
    }

    /**
     * 检查对象中是否存在空值属性
     *
     * @param orig
     *            源对象，标准的JavaBean
     * @param dest
     *            排除检查的属性:Map
     *
     *
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public static boolean checkObjProperty(Object orig, Map dest) throws Exception {
        try {
            java.lang.reflect.Field[] fields = orig.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                String name = fields[i].getName();
                if (!dest.containsKey(name)) {
                    if (PropertyUtils.isReadable(orig, name)) {
                        Object value = PropertyUtils.getSimpleProperty(orig, name);
                        if (value == null) {
                            return true;
                        }
                    }
                }
            }
            return false;
        } catch (Exception e) {
            throw new Exception("检查对象中是否存在空属性值,可排除相关字段", e);
        }
    }

}
