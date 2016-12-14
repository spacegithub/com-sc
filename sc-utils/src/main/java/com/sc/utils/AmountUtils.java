

package com.sc.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 数值工具类
 * <功能详细描述>
 *
 * @see: [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class AmountUtils {

    /**
     * 数组中文数组定义
     */
    private static final char[] numberZHCharacter = {'零', '一', '二', '三', '四', '五', '六', '七', '八', '九', '十'};

    /**
     * 将阿拉伯数字转换为对应的中文数字，支持两位数字的转换
     *
     * @param number 阿拉伯数字
     * @return 中文数字字符串
     */
    public static String toZHCharacter(int number) {
        if (number < 10) {
            return String.valueOf(numberZHCharacter[number]);
        }
        StringBuffer sb = new StringBuffer();
        int mode = 10;
        int highBit = number / mode;
        int lowBit = number % mode;
        if (highBit > 1) {
            sb.append(numberZHCharacter[highBit]);
        }
        if (highBit >= 1) {
            sb.append(numberZHCharacter[mode]);
        }
        if (lowBit > 0) {
            sb.append(numberZHCharacter[lowBit]);
        }
        return sb.toString();
    }

    /**
     * 将小数转换为货币格式的数值
     *
     * @param number 输入的小数
     * @return 保留两位小数的货币格式的数值
     */
    public static String pennyToDollar(double number) {
        BigDecimal newNumber = new BigDecimal(number);
        BigDecimal result = newNumber.divide(new BigDecimal(100));
        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format(result);
    }

    /**
     * @see #pennyToDollar(double)
     */
    public static String pennyToDollar(long number) {
        BigDecimal newNumber = new BigDecimal(number);
        BigDecimal result = newNumber.divide(new BigDecimal(100));
        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format(result);
    }

    /**
     * 将小数转换为RMB货币格式的数值<br>
     * 如果是整元则输出整元，如果是整角则输出整角，否则输出整分
     *
     * @param number 输入的整数
     * @return 格式化后的值
     */
    public static String pennyToRMB(long number) {
        if (number % 100 == 0) {
            return String.valueOf(number / 100);
        } else if (number % 10 == 0) {
            BigDecimal newNumber = new BigDecimal(number);
            BigDecimal result = newNumber.divide(new BigDecimal(100));
            DecimalFormat df = new DecimalFormat("#0.0");
            return df.format(result);
        } else {
            BigDecimal newNumber = new BigDecimal(number);
            BigDecimal result = newNumber.divide(new BigDecimal(100));
            DecimalFormat df = new DecimalFormat("#0.00");
            return df.format(result);
        }
    }

    public static String pennyToRMBFloat(long number) {
        String pennyRMB = pennyToRMB(number);
        return pennyRMB.contains(".") ? pennyRMB : pennyRMB + ".00";
    }

    /**
     * 将米转换为保留一位小数的千米
     *
     * @param number 米
     * @return 保留一位小数的千米
     */
    public static String meterToKilometer(long number) {
        BigDecimal newNumber = new BigDecimal(number);
        BigDecimal result = newNumber.divide(new BigDecimal(1000));
        DecimalFormat df = new DecimalFormat("#.0");
        return df.format(result);
    }

}
