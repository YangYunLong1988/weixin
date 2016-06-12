package com.snowstore.diana.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Calendars {
	private static Logger LOGGER = LoggerFactory.getLogger(HttpUtil.class);
	
	public static final String YYYY_MM_DD = "yyyy-MM-dd";

	/**
	 * 读取日期格式
	 * 
	 * @param pattern
	 * @return
	 */
	public static DateFormat getDateFormat(String pattern) {
		DateFormat format = new SimpleDateFormat(pattern);
		return format;
	}

	/**
	 * 格式化日期
	 * 
	 * @param pattern
	 * @param date
	 * @return
	 */
	public static String format(String pattern, Date date) {
		return getDateFormat(pattern).format(date);
	}

	/**
	 * 格式化日期
	 * 
	 * @param pattern
	 * @return
	 */
	public static String format(String pattern) {
		return format(pattern, new Date());
	}

	public static Date StringToDate(String dateStr, String formatStr) {
		DateFormat dd = new SimpleDateFormat(formatStr);
		Date date = null;
		try {
			date = dd.parse(dateStr);
		} catch (ParseException e) {
			LOGGER.error("ParseException", e);
		}

		return date;
	}

	public static Date StringToDate(String dateStr) {
		DateFormat dd = new SimpleDateFormat(Calendars.YYYY_MM_DD);
		Date date = null;
		try {
			date = dd.parse(dateStr);
		} catch (ParseException e) {
			LOGGER.error("ParseException", e);
		}

		return date;
	}

	/**
	 * 格式化日期
	 * 
	 * @author wulinjie
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String format(Date date, String pattern) {
		if (null != date) {
			return org.apache.commons.lang3.time.DateFormatUtils.format(date, pattern);
		}
		return null;
	}

	/**
	 * 转换日期格式
	 * 
	 * @author wulinjie
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static Date parse(Date date, String pattern) {
		if (null != date) {
			try {
				return org.apache.commons.lang3.time.DateUtils.parseDate(format(date, "yyyy-MM-dd"), pattern);
			} catch (ParseException e) {
				LOGGER.error("ParseException", e);
			}
		}
		return null;
	}

	/**
	 * 获取当前时间
	 * 
	 * @author wulinjie
	 */
	public static Date getCurrentDate() {
		// 当前时间
		Date currentTime = new Date();
		DateFormat formatter = getDateFormat("yyyy-MM-dd HH:mm:ss");
		String strDate = formatter.format(currentTime);
		ParsePosition pos = new ParsePosition(0);
		return formatter.parse(strDate, pos);
	}

	/**
	 * 计算当前日期增加指定天数后的日期
	 * 
	 * @author wulinjie
	 * @param date
	 * @param days
	 * @return
	 */
	public static Date getDaysDate(Date date, Integer days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, days);
		return calendar.getTime();
	}

}