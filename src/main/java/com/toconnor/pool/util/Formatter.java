package com.toconnor.pool.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Formatter
 */
public class Formatter
{
	private static final String STANDARD = "EEE, MMM-dd h:mm a";
	private static final String DATETIME = "EEE, MMM-dd h:mm:ss a";
	private static final String SORTABLE = "yyyy-MM-dd HH:mm";

	public static String formatDate(Date date)
	{
		return formatDate(date, STANDARD);
	}

	public static String formatDateTime(Date date)
	{
		return formatDate(date, DATETIME);
	}

	public static String formatIsoDate(Date date)
	{
		return formatDate(date, SORTABLE);
	}

	private static String formatDate(Date date, String format)
	{
		if(date == null) return "";

		DateFormat df = new SimpleDateFormat(format);
		df.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		return df.format(date);
	}

	public static String formatProvider(String provider)
	{
		if("google".equals(provider))
		{
			return "Google";
		}
		else if("windowslive".equals(provider))
		{
			return "Windows Live";
		}
		else if("linkedin".equals(provider))
		{
			return "LinkedIn";
		}
		else if("facebook".equals(provider))
		{
			return "Facebook";
		}
		else if("yahoo".equals(provider))
		{
			return "Yahoo";
		}
		return provider;
	}
}
