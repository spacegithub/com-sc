

package com.sc.utils;

/**
 * 对象判断常用方法
 *
 * @author Edmund
 */
public class ObjectUtils {
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
