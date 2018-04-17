

package com.sc.utils.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;


public class AmountUtils {

    
    private static final char[] numberZHCharacter = {'零', '一', '二', '三', '四', '五', '六', '七', '八', '九', '十'};

    
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

    
    public static String pennyToDollar(double number) {
        BigDecimal newNumber = new BigDecimal(number);
        BigDecimal result = newNumber.divide(new BigDecimal(100));
        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format(result);
    }

    
    public static String pennyToDollar(long number) {
        BigDecimal newNumber = new BigDecimal(number);
        BigDecimal result = newNumber.divide(new BigDecimal(100));
        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format(result);
    }

    
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

    
    public static String meterToKilometer(long number) {
        BigDecimal newNumber = new BigDecimal(number);
        BigDecimal result = newNumber.divide(new BigDecimal(1000));
        DecimalFormat df = new DecimalFormat("#.0");
        return df.format(result);
    }

}
