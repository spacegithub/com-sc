package com.sc.utils.utils.xml;

import java.text.SimpleDateFormat;
import java.util.regex.Pattern;


public class XMLTools {
    public XMLTools() {
    }

    
    public static boolean isStrEmpty(String str) {
        if ((str != null) && (str.trim().length() > 0)) {
            return false;
        } else {
            return true;
        }
    }

    
    public static String ruleStr(String str) {
        if (str == null) {
            return "";
        } else {
            return str.trim();
        }
    }

    
    public static String GBK2Unicode(String str) {
        try {
            str = new String(str.getBytes("GBK"), "ISO-8859-1");
        } catch (java.io.UnsupportedEncodingException e) {}
        ;
        return str;
    }

    
    public static String Unicode2GBK(String str) {
        try {
            str = new String(str.getBytes("ISO-8859-1"), "GBK");
        } catch (java.io.UnsupportedEncodingException e) {}
        ;
        return str;
    }

    
    public static String getSysTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        return df.format(new java.util.Date());
    }

    
    public static String getSysDate() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        return df.format(new java.util.Date());
    }

    
    public static String getSysTimeFormat(String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(new java.util.Date());
    }

    
    public static boolean isDay(String d, String format){
        try{
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            sdf.setLenient(false);
            sdf.parse(d);
        }catch (Exception e){
            return false;
        }
        return true;
    }

    
    public static boolean checkAmount(String amount){
        if(amount==null){
            return false;
        }
        String checkExpressions;
        checkExpressions="^([1-9]\\d*|[0])\\.\\d{1,2}$|^[1-9]\\d*$|^0$";
        return Pattern.matches(checkExpressions, amount);
    }

    
    public static String getXMLValue(String srcXML, String element) {
        String ret = "";
        try {
            String begElement = "<" + element + ">";
            String endElement = "</" + element + ">";
            int begPos = srcXML.indexOf(begElement);
            int endPos = srcXML.indexOf(endElement);
            if (begPos != -1 && endPos != -1 && begPos <= endPos) {
                begPos += begElement.length();
                ret = srcXML.substring(begPos, endPos);
            } else {
                ret = "";
            }
        } catch (Exception e) {
            ret = "";
        }
        return ret;
    }
}
