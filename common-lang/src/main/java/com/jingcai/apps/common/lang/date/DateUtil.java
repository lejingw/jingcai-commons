package com.jingcai.apps.common.lang.date;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lejing on 15/4/16.
 */
public class DateUtil extends org.apache.commons.lang3.time.DateUtils{
    private static final SimpleDateFormat formatter10 = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat formatter20 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat date14 = new SimpleDateFormat("yyyyMMddHHmmss");
    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    public static String getNow10() {
        return formatter10.format(new Date());
    }

    public static String getNow20() {
        return formatter20.format(new Date());
    }

    public static String getNow14() {
        return date14.format(new Date());
    }

    public static String parse20ToDate14(String date20str){
        return date14.format(parseDate20(date20str));
    }

    public static Date parseDate20(String date20Str){
        try {
            if(date20Str.length()>19){
                date20Str = date20Str.substring(0, 19);
            }
            return formatter20.parse(date20Str);
        } catch (ParseException e) {
            return null;
        }
    }
    public static Date parseDate10(String date10Str){
        try {
            if(date10Str.length()>10){
                date10Str = date10Str.substring(0, 10);
            }
            return formatter20.parse(date10Str);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 判断两个时间点的间隔是否在一个时间区间内
     * @param begindate
     * @param endDate
     * @param desInterval
     * @param type 0：正向区间 1：正负区间
     * @return
     */
    public static boolean isInInterval(Date begindate,Date endDate,long desInterval,String type){
        if(begindate==null||endDate==null)
            return false;
        long inteval = endDate.getTime() - begindate.getTime();
        if(type.equals("1"))
            inteval = Math.abs(inteval);
        if(inteval<0)
            return false;

        if(desInterval>inteval)
            return true;
        else
            return false;

    }

    /**
     * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
     */
    public static String formatDate(Date date, Object... pattern) {
        String formatDate = null;
        if (pattern != null && pattern.length > 0) {
            formatDate = DateFormatUtils.format(date, pattern[0].toString());
        } else {
            formatDate = DateFormatUtils.format(date, "yyyy-MM-dd");
        }
        return formatDate;
    }

    public static Date parseDate(Object str) {
        if (str == null){
            return null;
        }
        try {
            return parseDate(str.toString(), parsePatterns);
        } catch (ParseException e) {
            return null;
        }
    }

}
