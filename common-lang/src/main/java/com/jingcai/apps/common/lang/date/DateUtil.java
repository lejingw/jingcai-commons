package com.jingcai.apps.common.lang.date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by lejing on 15/4/16.
 */
public class DateUtil {
	public static final String PATTERN_8 = "yyyyMMdd";
	public static final String PATTERN_10 = "yyyy-MM-dd";
	public static final String PATTERN_14 = "yyyyMMddHHmmss";
	public static final String PATTERN_20 = "yyyy-MM-dd HH:mm:ss";
	private static final SimpleDateFormat formatter8 = new SimpleDateFormat(PATTERN_8);
	private static final SimpleDateFormat formatter10 = new SimpleDateFormat(PATTERN_10);
	private static final SimpleDateFormat formatter14 = new SimpleDateFormat(PATTERN_14);
	private static final SimpleDateFormat formatter20 = new SimpleDateFormat(PATTERN_20);

	public static String getNow8() {
		return formatter8.format(new Date());
	}

	public static String getNow10() {
		return formatter10.format(new Date());
	}

	public static String getNow14() {
		return formatter14.format(new Date());
	}

	public static String getNow20() {
		return formatter20.format(new Date());
	}

	public static String parse20ToDate14(String date20str) {
		return formatter14.format(parseDate20(date20str));
	}

	public static Date parseDate8(String date8Str) {
		if(null == date8Str)	return null;
		try {
			if (date8Str.length() > 8) {
				date8Str = date8Str.substring(0, 8);
			}
			return formatter8.parse(date8Str);
		} catch (ParseException e) {
			return null;
		}
	}

	public static Date parseDate10(String date10Str) {
		if(null == date10Str) 	return null;
		try {
			if (date10Str.length() > 10) {
				date10Str = date10Str.substring(0, 10);
			}
			return formatter10.parse(date10Str);
		} catch (ParseException e) {
			return null;
		}
	}

	public static Date parseDate14(String date14Str) {
		if(null == date14Str) 	return null;
		try {
			if (date14Str.length() > 14) {
				date14Str = date14Str.substring(0, 14);
			}
			return formatter14.parse(date14Str);
		} catch (ParseException e) {
			return null;
		}
	}

	public static Date parseDate20(String date20Str) {
		if(null == date20Str) 	return null;
		try {
			if (date20Str.length() > 19) {
				date20Str = date20Str.substring(0, 19);
			}
			return formatter20.parse(date20Str);
		} catch (ParseException e) {
			return null;
		}
	}

	public static Date parseDate(String dateStr, String pattern) {
		if(null == dateStr) 	return null;
		try {
			Date date = DateUtils.parseDate(dateStr, pattern);
			return date;
		} catch (ParseException e) {
			return null;
		}
	}

	public static String format8(Date date) {
		return format(date, PATTERN_8);
	}

	public static String format10(Date date) {
		return format(date, PATTERN_10);
	}

	public static String format14(Date date) {
		return format(date, PATTERN_14);
	}

	public static String format20(Date date) {
		return format(date, PATTERN_20);
	}

	public static String formatDate(Date date, String pattern) {
		return format(date, pattern);
	}
	public static String format(Date date, String pattern) {
		if(null == date)	return null;
		return DateFormatUtils.format(date, pattern);
	}

	public static boolean isWeekend(Date date) {
		if(null == date)	return false;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
				|| cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
	}

	public static Date addMonths(Date date, int amount){
		if(null == date)	return null;
		return DateUtils.addMonths(date, amount);
	}
	public static Date addDays(Date date, int amount){
		if(null == date)	return null;
		return DateUtils.addDays(date, amount);
	}
	public static Date addHours(Date date, int amount){
		if(null == date)	return null;
		return DateUtils.addHours(date, amount);
	}
	public static Date addSeconds(Date date, int amount){
		if(null == date)	return null;
		return DateUtils.addSeconds(date, amount);
	}
	public static Date addMilliseconds(Date date, int amount){
		if(null == date)	return null;
		return DateUtils.addMilliseconds(date, amount);
	}
	public static Date addMinutes(Date date, int amount){
		if(null == date)	return null;
		return DateUtils.addMinutes(date, amount);
	}
}
