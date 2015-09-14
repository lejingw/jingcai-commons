package com.jingcai.apps.common.lang.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lejing on 15/4/16.
 */
public class DateUtil {
    private static final SimpleDateFormat formatter10 = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat formatter20 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat date14 = new SimpleDateFormat("yyyyMMddHHmmss");

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

}
