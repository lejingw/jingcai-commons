package com.jingcai.apps.common.lang.string;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by lejing on 15/4/23.
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils{
    private static final String CHARSET_NAME = "UTF-8";

    /**
     * 转换为字节数组
     * @param str
     * @return
     */
    public static byte[] getBytes(String str){
        if (str != null){
            try {
                return str.getBytes(CHARSET_NAME);
            } catch (UnsupportedEncodingException e) {
                return null;
            }
        }else{
            return null;
        }
    }

    /**
     * 转换为字节数组
     * @param str
     * @return
     */
    public static String toString(byte[] bytes){
        try {
            return new String(bytes, CHARSET_NAME);
        } catch (UnsupportedEncodingException e) {
            return EMPTY;
        }
    }


    /**
     * 转换为Float类型
     */
    public static Float toFloat(Object val){
        return toDouble(val).floatValue();
    }

    /**
     * 转换为Long类型
     */
    public static Long toLong(Object val){
        return toDouble(val).longValue();
    }

    /**
     * 转换为Integer类型
     */
    public static Integer toInteger(Object val){
        return toLong(val).intValue();
    }

    /**
     * 转换为Double类型
     */
    public static Double toDouble(Object val){
        if (val == null){
            return 0D;
        }
        try {
            return Double.valueOf(trim(val.toString()));
        } catch (Exception e) {
            return 0D;
        }
    }

    public static String parseMoney(String str){
        double dstr = toDouble(str);
        BigDecimal number = BigDecimal.valueOf(dstr);
        DecimalFormat decimalFormat=new DecimalFormat(".00");
        return decimalFormat.format(number);
    }

    public static String parseDistance(String str){
        double dstr = toDouble(str);
        BigDecimal number = BigDecimal.valueOf(dstr);
        DecimalFormat decimalFormat=new DecimalFormat(".0");
        return decimalFormat.format(number);
    }
}
