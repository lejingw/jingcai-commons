package com.jingcai.apps.common.lang.date;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by lejing on 15/4/16.
 */
@Slf4j
public class DateUtil {
	public static final String PATTERN_4 = "yyyy";
	public static final String PATTERN_6 = "yyyyMM";
	public static final String PATTERN_8 = "yyyyMMdd";
	public static final String PATTERN_10 = "yyyy-MM-dd";
	public static final String PATTERN_14 = "yyyyMMddHHmmss";
	public static final String PATTERN_20 = "yyyy-MM-dd HH:mm:ss";

	public static String getNow4() {
		return format4(new Date());
	}

	public static String getNow6() {
		return format6(new Date());
	}

	public static String getNow8() {
		return format8(new Date());
	}

	public static String getNow10() {
		return format10(new Date());
	}

	public static String getNow14() {
		return format14(new Date());
	}

	public static String getNow20() {
		return format20(new Date());
	}

	public static String parse20ToDate14(String date20str) {
		return format20(parse20(date20str));
	}

	public static Date parse4(String date4Str) {
		if (null == date4Str) return null;
		return parse(date4Str, PATTERN_4);
	}

	public static Date parse6(String date6Str) {
		if (null == date6Str) return null;
		return parse(date6Str, PATTERN_6);
	}

	public static Date parse8(String date8Str) {
		if (null == date8Str) return null;
		return parse(date8Str, PATTERN_8);
	}

	public static Date parse10(String date10Str) {
		if (null == date10Str) return null;
		return parse(date10Str, PATTERN_10);
	}

	public static Date parse14(String date14Str) {
		if (null == date14Str) return null;
		return parse(date14Str, PATTERN_14);
	}

	public static Date parse20(String date20Str) {
		if (null == date20Str) return null;
		return parse(date20Str, PATTERN_20);
	}

	public static Date parse(String dateStr, String pattern) {
		if (null == dateStr) return null;
		try {
			Date date = DateUtils.parseDate(dateStr, pattern);
			return date;
		} catch (ParseException e) {
			return null;
		}
	}

	public static String format4(Date date) {
		return format(date, PATTERN_4);
	}

	public static String format6(Date date) {
		return format(date, PATTERN_6);
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

	public static String format(Date date, String pattern) {
		if (null == date) return null;
		return DateFormatUtils.format(date, pattern);
	}

	public static boolean isWeekend(Date date) {
		if (null == date) return false;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
				|| cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
	}

	public static Date addMonths(Date date, int amount) {
		if (null == date) return null;
		return DateUtils.addMonths(date, amount);
	}

	public static Date addDays(Date date, int amount) {
		if (null == date) return null;
		return DateUtils.addDays(date, amount);
	}

	public static Date addHours(Date date, int amount) {
		if (null == date) return null;
		return DateUtils.addHours(date, amount);
	}

	public static Date addSeconds(Date date, int amount) {
		if (null == date) return null;
		return DateUtils.addSeconds(date, amount);
	}

	public static Date addMilliseconds(Date date, int amount) {
		if (null == date) return null;
		return DateUtils.addMilliseconds(date, amount);
	}

	public static Date addMinutes(Date date, int amount) {
		if (null == date) return null;
		return DateUtils.addMinutes(date, amount);
	}

	/**
	 * 获取统计月第一天
	 */
	public static Date firstDayOfMonth(Date statdate) {
		Calendar ca = Calendar.getInstance();
		ca.setTime(statdate);
		ca.set(Calendar.DAY_OF_MONTH, 1);
		return ca.getTime();
	}

	/**
	 * 获取统计月最后一天
	 */
	public static Date lastDayOfMonth(Date statdate) {
		Calendar ca = Calendar.getInstance();
		ca.setTime(statdate);
		ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
//		ca.set(Calendar.DAY_OF_MONTH, 1);
//		ca.roll(Calendar.DAY_OF_MONTH, -1);
		return ca.getTime();
	}

	public static Date firstDayOfWeek(Date statdate) {
		Calendar ca = Calendar.getInstance();
		ca.setTime(statdate);
		ca.set(Calendar.DAY_OF_WEEK, 1);
		return ca.getTime();
	}

	public static Date lastDayOfWeek(Date statdate) {
		Calendar ca = Calendar.getInstance();
		ca.setTime(statdate);
		ca.set(Calendar.DAY_OF_WEEK, ca.getActualMaximum(Calendar.DAY_OF_WEEK));
		return ca.getTime();
	}
}
