package com.zxxwl.common.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Date {
    public static String format(String format, long unix){
        DateFormat sdf = new SimpleDateFormat(format, Locale.US);
        return sdf.format(new java.util.Date(unix * 1000L));
    }

    public static long str2time(String text){
        return str2time("yyyy-MM-dd HH:mm:ss", text);
    }

    public static long str2time(String format, String text){
        DateFormat sdf = new SimpleDateFormat(format, Locale.US);
        try {
            java.util.Date date = sdf.parse(text);
            return date.getTime();
        } catch (ParseException e) {
            return 0L;
        }
    }

    public static int week2number(String week){
        week = week.toLowerCase();
        switch (week){
            case "mon":
            case "monday":
                return 1;
            case "tue":
            case "tuesday":
                return 2;
            case "wed":
            case "wednesday":
                return 3;
            case "thu":
            case "thursday":
                return 4;
            case "fri":
            case "friday":
                return 5;
            case "sat":
            case "saturday":
                return 6;
            case "sun":
            case "sunday":
                return 7;
        }

        return 0;
    }

    public static long now(){
        return System.currentTimeMillis();
    }

    public static int time(){
        return (int)(System.currentTimeMillis() / 1000);
    }
}
