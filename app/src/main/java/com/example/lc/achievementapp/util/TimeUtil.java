package com.example.lc.achievementapp.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

    /**
     * 返还当前日期的时间（以一日为单位）
     * @return
     */
    public static long getDateTime(){
        String dateTime = parseTime(System.currentTimeMillis());
        long time = stringToLong(dateTime, "yyyy-MM-dd");
        return time;
    }

    /**
     * 获取以时间命名的无符号字符串
     * @return
     */
    public static String getLocalTimeForFileName(){
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        return format.format(date);
    }

    /**
     * 解析时间
     * @param time
     * @return
     */
    public static String parseTime(long time){
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    public static String parseTime(long time, String timeFormat){
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat(timeFormat);
        return format.format(date);
    }

    public static long stringToLong(String strTime, String formatType) {
        try {
            Date date = stringToDate(strTime, formatType); // String类型转成date类型
            if (date == null) {
                return 0;
            } else {
                long currentTime = dateToLong(date); // date类型转成long类型
                return currentTime;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    public static Date stringToDate(String strTime, String formatType)
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        date = formatter.parse(strTime);
        return date;
    }

    public static long dateToLong(Date date) {
        return date.getTime();
    }

    /**
     * 计算两个日期之间的相差天数
     * @param day1
     * @param day2
     * @return
     */
    public static int differToTwoDays(long day1, long day2){
        int day = (int) ((day2 - day1) / 1000 / 60 / 60 / 24);
        return day;
    }

}
