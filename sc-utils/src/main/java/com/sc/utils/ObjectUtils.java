

package com.sc.utils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * 对象判断常用方法
 *
 * @author Edmund
 */
public class ObjectUtils {

    /**
     * 判断所有的Object
     * 如果是字符串则空字符串也算
     * @param obj
     * @return
     */
    private  static boolean isEmpty(Object obj) {
        return obj == null?true:(obj.getClass().isArray()? Array.getLength(obj) == 0:(obj instanceof CharSequence?((CharSequence)obj).length() == 0:(obj instanceof Collection ?((Collection)obj).isEmpty():(obj instanceof Map ?((Map)obj).isEmpty():false))));
    }
    /**
     * 判断String对象是否是为Null或者为""
     * @param str String对象
     * @return 判断结果
     */
    public static boolean isEmpty(String str){
        if (str == null || "".equals(str.trim())){
            return true;
        }

        return false;
    }

    /**
     * 判断Integer对象是否是为Null或者为0
     * @param i Integer对象
     * @return 判断结果
     */
    public static boolean isEmpty(Integer i){
        if(i == null || i == 0){
            return true;
        }

        return false;
    }

    /**
     * 判断Long对象是否是为Null或者为0
     * @param l Long对象
     * @return 判断结果
     */
    public static boolean isEmpty(Long l){
        if (l == null || l == 0L){
            return true;
        }

        return false;
    }

    /**
     * 判断Double对象是否是为Null或者为0
     * @param d Double对象
     * @return 判断结果
     */
    public static boolean isEmpty(Double d){
        if(d == null || d == 0D){
            return true;
        }

        return false;
    }
}
