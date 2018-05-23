package com.example.demo.util;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil {

    public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DAY_PATTERN = "yyyy-MM-dd";

    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static Date toDate(LocalDateTime dateTime) {
        if(dateTime == null) return null;
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static String format(LocalDateTime dateTime, String format){
        if(dateTime == null) return null;
        return dateTime.format(DateTimeFormatter.ofPattern(format));
    }

    public static LocalDateTime parse(String dateTimeStr, String format){
        if(dateTimeStr == null) return null;
        DateTimeFormatter sf = DateTimeFormatter.ofPattern(format);
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, sf);
        return dateTime;
    }

    public static String format(LocalDateTime dateTime){
        return format(dateTime, DEFAULT_DATE_TIME_FORMAT);
    }

    public static LocalDateTime parse(String dateTimeStr){
        return parse(dateTimeStr, DEFAULT_DATE_TIME_FORMAT);
    }


    /**
     * 时间格式转换为字符串格式
     *
     * @param date   时间
     * @param format 格式 如("yyyy-MM-dd hh:mm:ss")
     * @return String
     */
    public static String DateToString(Date date, String format) {
        if (date == null) {
            //默认值
            date = new Date();
        }
        if (format == null || format.equals("")) {
            //默认值
            format = "yyyy-MM-dd HH:mm:ss";
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);

        return simpleDateFormat.format(date);

    }

    /**
     * 字符串格式转换为时间格式
     *
     * @param dateStr 字符串
     * @param format  格式 如("yyyy-MM-dd HH:mm:ss")
     * @return Date
     */
    public static Date StringToDate(String dateStr, String format) {
        if (format == null || format.equals("")) {
            format = "yyyy-MM-dd HH:mm:ss";
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = null;

        try {
            date = simpleDateFormat.parse(dateStr);
            if (date.getTime() > System.currentTimeMillis()) {
                date = new Date();
            }
        } catch (ParseException e1) {
            return null;
        }
        return date;
    }

    /**
     * 获取当前日期
     *
     * @param pattern 格式如:yyyy-MM-dd
     * @return
     */
    public static String getCurrentDate(String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        return sdf.format(new Date());
    }

    /**
     * get cuurent Date return java.util.Date type
     */
    public static Date getNowUtilDate() {
        return new Date();
    }

    /**
     * get current Date return java.sql.Date type
     *
     * @return
     */
    public static java.sql.Date getNowSqlDate() {
        return new java.sql.Date(System.currentTimeMillis());
    }

    /**
     * get the current timestamp return java.sql.Timestamp
     *
     * @return
     */
    public static Timestamp getNowTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * time1>time2 返回正数
     *
     * @param time1
     * @param time2
     * @return
     */
    public static long getOffsetBetweenTimes(String time1, String time2) {
        return StringToDate(time1, DEFAULT_FORMAT).getTime() - StringToDate(time2, DEFAULT_FORMAT).getTime();
    }

    /**
     * 对指定日期滚动指定天数,负数时,则往前滚,正数时,则往后滚
     *
     * @param date Date
     * @param days int
     * @return String
     */
    public static String rollDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);
        return DateUtil.DateToString(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 对指定日期滚动指定分钟,负数时,则往前滚,正数时,则往后滚
     *
     * @param date Date
     * @param minutes int
     * @return String
     */
    public static String rollMinutes(Date date, int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minutes);
        return DateUtil.DateToString(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 对指定日期滚动指定分钟,负数时,则往前滚,正数时,则往后滚
     *
     * @param date Date
     * @param seconds int
     * @return String
     */
    public static String rollSeconds(Date date, int seconds, String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, seconds);
        return DateUtil.DateToString(calendar.getTime(), format);
    }

    /**
     * 返回  2013-01-16T06:24:26.829Z 时间
     *
     * @param date
     * @return
     * @throws Exception
     */
    public static XMLGregorianCalendar getXmlDatetime(Date date) throws Exception {
        if (null == date) {
            date = new Date();
        }
        GregorianCalendar nowGregorianCalendar = new GregorianCalendar();
        XMLGregorianCalendar xmlDatetime = DatatypeFactory.newInstance().newXMLGregorianCalendar(nowGregorianCalendar);
        // XMLGregorianCalendar ->GregorianCalendar
        nowGregorianCalendar = xmlDatetime.toGregorianCalendar();
        nowGregorianCalendar.setTime(date);
        return xmlDatetime;
    }

    public static boolean checkDateBettwenBoth(Date checkDate, Date date1, Date date2) {
        boolean temp = false;
        if (checkDate == null || date1 == null || date2 == null) {
            temp = false;
            return temp;
        }

        if (checkDate.equals(date1) || checkDate.equals(date2)) {
            temp = true;
        }

        if (checkDate.after(date1) && checkDate.before(date2)) {
            temp = true;
        }

        return temp;
    }

    public static String getFormatDatetime()
            throws Exception {
        GregorianCalendar gCalendar = new GregorianCalendar();
        SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_FORMAT);
        String strDateTime;
        try {
            strDateTime = formatter.format(gCalendar.getTime());
        } catch (Exception ex) {
            System.out.println("Error Message:".concat(String.valueOf(String.valueOf(ex.toString()))));
            String s = null;
            return s;
        }
        return strDateTime;
    }

    public static Date StringToDate(String s) {
        Date date = new Date(0L);
        try {
            Calendar calendar = Calendar.getInstance();
            int year = Integer.parseInt(s.substring(0, s.indexOf("-")));
            int month = Integer.parseInt(s.substring(s.indexOf("-") + 1, s.lastIndexOf("-")));
            int day = Integer.parseInt(s.substring(s.lastIndexOf("-") + 1, s.length()));
            calendar.set(year, month - 1, day);
            date.setTime(calendar.getTime().getTime());
        } catch (Exception e) {
            System.out.println(String.valueOf((new StringBuffer(String.valueOf(e))).append(",").append(s)));
        }
        return date;
    }

    public static String DateToString(Date dt) {
        SimpleDateFormat format = new SimpleDateFormat("yyMMdd");
        return format.format(dt);
    }
}
