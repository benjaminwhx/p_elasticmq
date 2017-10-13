package com.github.elasticmq.sqs.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {
    public static final String yyyyMMdd = "yyyyMMdd";
    public static final String yyyy_MM_dd = "yyyy-MM-dd HH:mm:ss";
    public static final String COMMON_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String yyyyMMddHHmmss = "yyyyMMddHHmmss";

    public static long betweenMills(Date fromDate, Date toDate) {
        return toDate.getTime() - fromDate.getTime();
    }

    public static Date parseDate(String format, String dateStr) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.parse(dateStr);
    }

    public static String formatDate(String format, Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }
    public static String formatDateNow() {
        SimpleDateFormat sdf = new SimpleDateFormat(yyyyMMddHHmmss);
        return sdf.format(new Date());
    }

    public static String formatDateLimitInHours(Date date,int hours){
        Calendar Cal = Calendar.getInstance();
        Cal.setTime(new Date());
        Cal.add(Calendar.HOUR_OF_DAY,hours*-1);
        return formatDate(yyyy_MM_dd,Cal.getTime());
    }

}
