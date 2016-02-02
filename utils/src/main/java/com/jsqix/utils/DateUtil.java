package com.jsqix.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtil {
	public static final String DATA_FORMAT_ZH = "yyyy年MM月dd日";
	public static final String DATE_FORMAT_DEFAUlt = "yyyy-MM-dd";
	public static final String TIME_FORMAT_DEFAUlt = "HH:mm:ss";
	public static final String DATETIME_FORMAT_DEFAUlt = DATE_FORMAT_DEFAUlt
			+ " " + TIME_FORMAT_DEFAUlt;
	public static final String DATE_FORMAT_DISPLAY = "dd/MM/yyyy";
	public static final String DATE_FORMAT_DISPLAY2 = "yyyy/MM/dd";
	public static final String TIME_FORMAT_DISPLAY = "HH:mm";
	public static final String DATETIME_FORMAT_DISPLAY = DATE_FORMAT_DISPLAY
			+ " " + TIME_FORMAT_DISPLAY;

	public static final String DATE_FORMAT_MYSQL = "yyyy-MM-dd";
	public static final String TIME_FORMAT_MYSQL = "HH:mm:ss";
	public static final String DATETIME_FORMAT_MYSQL = DATE_FORMAT_MYSQL + " "
			+ TIME_FORMAT_MYSQL;

	public static String dateToString(Date date, String pattern) {
		if (date == null)
			return null;
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		return dateFormat.format(date);
	}

	public static String getCurrentDatetimeString() {
		return dateToString(new Date(), DATETIME_FORMAT_DEFAUlt);
	}

	public static String getCurrentDateString() {
		return dateToString(new Date(), DATE_FORMAT_DEFAUlt);
	}

	public static String getCurrentDateString(String type) {
		return dateToString(new Date(), type);
	}

	public static String getCurrentTimeString() {
		return dateToString(new Date(), TIME_FORMAT_DEFAUlt);
	}

	public static String getCurrentDateTimeAddByDay(int day) {
		Calendar rightNow = Calendar.getInstance();
		rightNow.setTime(new Date());
		rightNow.add(Calendar.DAY_OF_YEAR, day);// 日期加60天
		return dateToString(rightNow.getTime(), DATETIME_FORMAT_DEFAUlt);
	}

	public static String getCurrentDateAddByDay(int day) {
		Calendar rightNow = Calendar.getInstance();
		rightNow.setTime(new Date());
		rightNow.add(Calendar.DAY_OF_YEAR, day);// 日期加60天
		return dateToString(rightNow.getTime(), DATE_FORMAT_DEFAUlt);
	}

	public static String getCurrentDateTimeAddByDay(Date date, int day) {
		Calendar rightNow = Calendar.getInstance();
		rightNow.setTime(date);
		rightNow.add(Calendar.DAY_OF_YEAR, day);// 日期加60天
		return dateToString(rightNow.getTime(), DATETIME_FORMAT_DEFAUlt);
	}

	public static String getCurrentDateAddByDay(Date date, int day) {
		Calendar rightNow = Calendar.getInstance();
		rightNow.setTime(date);
		rightNow.add(Calendar.DAY_OF_YEAR, day);// 日期加60天
		return dateToString(rightNow.getTime(), DATE_FORMAT_DEFAUlt);
	}

	/* 时间戳转换成字符窜 */
	public static String getLongDateToString(long time, String pattern) {
		return dateToString(new Date(time), pattern);
	}

	/* 将字符串转为时间 */
	public static Date getStringToDate(String time, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		Date date = new Date();
		try {
			date = sdf.parse(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}

	/* 将字符串转为时间戳 */
	public static long getStringToLongDate(String time, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		Date date = new Date();
		try {
			date = sdf.parse(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date.getTime();
	}

	public static Date stringToDate(String date, String pattern) {
		if (date == null)
			return null;

		if (pattern == TIME_FORMAT_MYSQL) {
			pattern = DATETIME_FORMAT_MYSQL;
			date = "2012-01-01 " + date;
		}

		if (pattern == TIME_FORMAT_DISPLAY) {
			pattern = DATETIME_FORMAT_DISPLAY;
			date = "01/01/2012 " + date;
		}

		SimpleDateFormat dateFormat = getDateFormat(pattern);

		try {
			Date parsedDate = dateFormat.parse(date);
			return parsedDate;
		} catch (ParseException e) {
		}

		return null;
	}

	private static SimpleDateFormat getDateFormat(String pattern) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern,
				Locale.getDefault());
		simpleDateFormat.setTimeZone(TimeZone.getDefault()); // Use UTC timezone
																// throughout
		return simpleDateFormat;
	}

	public static Date sqlDatetimeStringToDate(String sqlDatetimeString) {
		return stringToDate(sqlDatetimeString, DATETIME_FORMAT_MYSQL);
	}

	public static int getCurrentDateTimeStatus() {
		String time = getCurrentTimeString();
		int hour = StringUtils.toInt(time.split(":")[0]);
		if (hour > 5 && hour < 12) {
			return TIME.AM;
		} else if (hour > 12 && hour < 17) {
			return TIME.NOON;
		} else {
			return TIME.PM;
		}
	}

	public static interface TIME {
		public static final int AM = 1;// 上午
		public static final int NOON = 2;// 下午
		public static final int PM = 3;// 晚上

	}
}
