package com.sc.utils.utils;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class JodoTimeUtil {

    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    
    public static String getDate() {
        return getDate("yyyy-MM-dd");
    }

    
    public static String getDate(String pattern) {
        return DateFormatUtils.format(new Date(), pattern);
    }

    
    public static String formatDate(Date date, Object... pattern) {
        String formatDate = null;
        if (pattern != null && pattern.length > 0) {
            formatDate = DateFormatUtils.format(date, pattern[0].toString());
        } else {
            formatDate = DateFormatUtils.format(date, "yyyy-MM-dd");
        }
        return formatDate;
    }

    
    public static String formatDateTime(Date date) {
        return formatDate(date, "yyyy-MM-dd HH:mm:ss");
    }

    
    public static String getTime() {
        return formatDate(new Date(), "HH:mm:ss");
    }

    
    public static String getDateTime() {
        return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
    }

    
    public static String getYear() {
        return formatDate(new Date(), "yyyy");
    }

    
    public static String getMonth() {
        return formatDate(new Date(), "MM");
    }

    
    public static String getDay() {
        return formatDate(new Date(), "dd");
    }

    
    public static String getWeek() {
        return formatDate(new Date(), "E");
    }

    
    public static Date parseDate(Object str) {
        if (str == null){
            return null;
        }
        try {
            return new SimpleDateFormat(parsePatterns[0]).parse(str.toString());
        } catch (ParseException e) {
            return null;
        }
    }

    
    public static long pastDays(Date date) {
        long t = System.currentTimeMillis()-date.getTime();
        return t/(24*60*60*1000);
    }

    
    public static long pastHour(Date date) {
        long t = System.currentTimeMillis()-date.getTime();
        return t/(60*60*1000);
    }

    
    public static long pastMinutes(Date date) {
        long t = System.currentTimeMillis()-date.getTime();
        return t/(60*1000);
    }

    
    public static String formatDateTime(long timeMillis){
        long day = timeMillis/(24*60*60*1000);
        long hour = (timeMillis/(60*60*1000)-day*24);
        long min = ((timeMillis/(60*1000))-day*24*60-hour*60);
        long s = (timeMillis/1000-day*24*60*60-hour*60*60-min*60);
        long sss = (timeMillis-day*24*60*60*1000-hour*60*60*1000-min*60*1000-s*1000);
        return (day>0?day+",":"")+hour+":"+min+":"+s+"."+sss;
    }

    
    public static int daysBetween(Date early, Date laste) {
        DateTime dateTimeEarly = new DateTime(early);
        DateTime dateTimeLaste = new DateTime(laste);
        return Days.daysBetween(dateTimeEarly, dateTimeLaste).getDays();
    }

}
