package com.milkit.app.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;



public class DateUtil {
	
	public static final long ONE_MINUTE_IN_MILLIS = 60000;		//60초


	public static String plusDay(Date srcdate, String targetdateFormat, int addday) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat(targetdateFormat);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(srcdate);
		cal.add(Calendar.DAY_OF_MONTH, addday);
		srcdate = cal.getTime();
		
		return dateFormat.format(srcdate);
	}

	public static Date plusDay(Date srcdate, int addday) throws ParseException {
		Calendar cal = Calendar.getInstance();
		cal.setTime(srcdate);
		cal.add(Calendar.DAY_OF_MONTH, addday);
		srcdate = cal.getTime();
		
		return srcdate;
	}

	public static Date plusMin(Date srcdate, int addMin) throws ParseException {
		long longTime = srcdate.getTime();
		Date result = new Date(longTime + (addMin * ONE_MINUTE_IN_MILLIS));
		
		return result;
	}
	
	public static int compareDate(Date srcDate, Date compareDate) throws ParseException {
		if (srcDate.equals(compareDate)) return 0;

		return !srcDate.after(compareDate) ? 1 : -1;
	}

	public static String getFormatedTimeString(Date date, String format) {
		String timeString = "";
		
		if(date != null && format != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			timeString =  dateFormat.format(date);
		}
		
		return timeString;
	}
}
