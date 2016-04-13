package com.air.project.utils.date;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/** 
 * @author yanghao 
 * @date 2014年9月17日 下午4:56:30
 */
public class DateUtil {  
	  
    private static final ThreadLocal<SimpleDateFormat> threadLocal = new ThreadLocal<SimpleDateFormat>();  
      
    private static final Object object = new Object();  
      
    /** 
     * 获取SimpleDateFormat 
     * @param pattern 日期格式 
     * @return SimpleDateFormat对象 
     * @throws RuntimeException 异常：非法日期格式 
     */  
    private static SimpleDateFormat getDateFormat(String pattern) throws RuntimeException {  
        SimpleDateFormat dateFormat = threadLocal.get();  
        if (dateFormat == null) {  
            synchronized (object) {  
                if (dateFormat == null) {  
                    dateFormat = new SimpleDateFormat(pattern);  
                    dateFormat.setLenient(false);  
                    threadLocal.set(dateFormat);  
                }  
            }  
        }  
        dateFormat.applyPattern(pattern);  
        return dateFormat;  
    }  
  
    /** 
     * @category 获取日期中的某数值。如获取月份 
     * @param date 日期 
     * @param calendarType 日期格式 .Calendar.month....
     * @return 数值 
     */  
    public static int getInteger(Date date, int calendarType) {  
        int num = 0;  
        Calendar calendar = Calendar.getInstance();  
        if (date != null) {  
            calendar.setTime(date);  
            num = calendar.get(calendarType);  
            if(Calendar.MONTH==calendarType){
            	num++;
            }
        }  
        return num;  
    }  
  
    /** 
     * @category 增加日期中某类型的某数值。如增加日期 
     * @param date 日期 
     * @param calendarType 类型 
     * @param amount 数值 
     * @return 计算后日期 
     */  
    public static Date addInteger(Date date, int calendarType, int amount) {  
        Date myDate = null;  
        if (date != null) {  
            Calendar calendar = Calendar.getInstance();  
            calendar.setTime(date);  
            calendar.add(calendarType, amount);  
            myDate = calendar.getTime();  
        }  
        return myDate;  
    }  
    /**
     * @category 两个日期差 天、月、年
     * @param date
     * @param largeDate
     * @param calendarType   Calendar.DAY_OF_YEAR ，Calendar.MONTH，Calendar.YEAR
     * @return
     */
    public static long diff(Date date,Date largeDate, int calendarType) {  
    	 int n = 0;
         Calendar c1 = Calendar.getInstance();
         Calendar c2 = Calendar.getInstance();
         c1.setTime(date);
         c2.setTime(largeDate);

         // 循环对比，直到相等，n 就是所要的结果
         while (!c1.after(c2)) {
             n++;
             if (calendarType == Calendar.MONTH) {
                 // 比较月份，月份+1
                 c1.add(Calendar.MONTH, 1);
             } else {
                 // 比较天数，日期+1
                 c1.add(Calendar.DATE, 1);
             }
         }

         n = n - 1;

         if (calendarType == Calendar.YEAR) {
             n = n / 365;
         }

         return n;
    } 
    /** 
     * @category 将日期字符串转化为日期。失败返回null。 
     * @param date 日期字符串 
     * @param pattern 日期格式 
     * @return 日期 
     */  
    public static Date stringToDate(String date, String pattern) {  
        Date myDate = null;  
        if (date != null) {  
            try {  
                myDate = getDateFormat(pattern).parse(date);  
            } catch (Exception e) { 
            	e.printStackTrace();
            }  
        }  
        return myDate;  
    }  
  
  
    /** 
     * @category 将日期转化为日期字符串。失败返回null。 
     * @param date 日期 
     * @param pattern 日期格式 
     * @return 日期字符串 
     */  
    public static String dateToString(Date date, String pattern) {  
        String dateString = null;  
        if (date != null) {  
            try {  
                dateString = getDateFormat(pattern).format(date);  
            } catch (Exception e) {  
            }  
        }  
        return dateString;  
    }  
    /** @category 获取当天的开始时间, 如:2014-02-24 00:00:00 */
    public static Date getBeginOfDate() {
        return getBeginOfDate(null);
    }

    /** @category 获取当天的结束时间, 如:2014-02-24 23:59:59 */
    public static Date getEndOfDate() {
        return getEndOfDate(null);
    }

    /**@category 获取指定日期的开始时间, 如:2014-02-24 00:00:00 */
    public static Date getBeginOfDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        return setDayToBegin(calendar).getTime();
    }

    /**@category 获取指定日期的结束时间, 如:2014-02-24 23:59:59 */
    public static Date getEndOfDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        return setDayToEnd(calendar).getTime();
    }

    /**
     * @category 获取当年天数
     */
    public static int getDaysInYear() {
        GregorianCalendar calendar = new GregorianCalendar();
        return calendar.isLeapYear(calendar.get(Calendar.YEAR)) ? 366 : 365;
    }

    /**
     * @category 当前日期
     */
    public static String getSysDateOfDate() {
        return dateToString(new Date(), "yyyy-MM-dd HH:mm:ssSSS");
    }


    /**
     * 时间范围
     */
    public static class DateRange {
        /** 开始时间 */
        public Date begin;
        /** 结束时间 */
        public Date end;

        public DateRange() {
        }

        public DateRange(Date begin, Date end) {
            this.begin = begin;
            this.end = end;
        }
    }

    /**
     * @category 获取指定日期的当天时间范围如(2014-06-09 00:00:00 2014-06-09 23:59:59)
     *
     * @param date 时间,为空表示当前时间
     * @return 时间范围
     */
    public static DateRange getDayRange(Date date) {
        DateRange range = new DateRange();
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }

        range.begin = setDayToBegin(calendar).getTime();
        range.end = setDayToEnd(calendar).getTime();

        return range;
    }

    /**
     * @category 获取指定日期本周的时间范围如
     *
     * @param date 时间,为空表示当前时间
     * @return 时间范围
     */
    public static DateRange getWeekReange(Date date) {
        DateRange range = new DateRange();
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        range.begin = setDayToBegin(calendar).getTime();

        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 6);
        range.end = setDayToEnd(calendar).getTime();

        return range;
    }

    /**
     * @category 获取指定日期本月的时间范围如
     *
     * @param date 时间,为空表示当前时间
     * @return 时间范围
     */
    public static DateRange getMonthReange(Date date) {
        DateRange range = new DateRange();
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        range.begin = setDayToBegin(calendar).getTime();

        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 1);
        range.end = setDayToEnd(calendar).getTime();

        return range;
    }

    /**
     * @category 获取指定日期本季度的时间范围如
     *
     * @param date 时间,为空表示当前时间
     * @return 时间范围
     */
    public static DateRange getSeasonReange(Date date) {
        DateRange range = new DateRange();
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }

        int month = calendar.get(Calendar.MONTH);
        calendar.set(Calendar.MONTH, month - (month % 3));
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        range.begin = setDayToBegin(calendar).getTime();

        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 3);
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 1);
        range.end = setDayToEnd(calendar).getTime();

        return range;
    }

    /**
     * @category 获取指定日期本半年的时间范围如
     *
     * @param date 时间,为空表示当前时间
     * @return 时间范围
     */
    public static DateRange getHalfYearReange(Date date) {
        DateRange range = new DateRange();
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }

        calendar.set(calendar.get(Calendar.YEAR), Calendar.JANUARY, 1, 0, 0, 0);
        range.begin = calendar.getTime();

        calendar.set(Calendar.MONTH, 6);
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 1);
        range.end = setDayToEnd(calendar).getTime();

        return range;
    }

    /**
     * @category 获取指定日期本半年的时间范围如
     *
     * @param date 时间,为空表示当前时间
     * @return 时间范围
     */
    public static DateRange getYearReange(Date date) {
        DateRange range = new DateRange();
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }

        calendar.set(calendar.get(Calendar.YEAR), Calendar.JANUARY, 1, 0, 0, 0);
        range.begin = calendar.getTime();

        calendar.set(Calendar.MONTH, 12);
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 1);
        range.end = setDayToEnd(calendar).getTime();

        return range;
    }

    private static Calendar setDayToEnd(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar;
    }

    private static Calendar setDayToBegin(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }
    
    /** 
     * @category 增加日期的年份。失败返回null。 
     * @param date 日期 
     * @param yearAmount 增加数量。可为负数 
     * @return 增加年份后的日期 
     */  
    public static Date addYear(Date date, int yearAmount) {  
        return addInteger(date, Calendar.YEAR, yearAmount);  
    }  
  
    /** 
     * @category 增加日期的月份。失败返回null。 
     * @param date 日期 
     * @param monthAmount 增加数量。可为负数 
     * @return 增加月份后的日期 
     */  
    public static Date addMonth(Date date, int monthAmount) {  
        return addInteger(date, Calendar.MONTH, monthAmount);  
    }  
  
  
    /** 
     * @category 增加日期的天数。失败返回null。 
     * @param date 日期 
     * @param dayAmount 增加数量。可为负数 
     * @return 增加天数后的日期 
     */  
    public static Date addDay(Date date, int dayAmount) {  
        return addInteger(date, Calendar.DATE, dayAmount);  
    }  
  
  
    /** 
     * @category 增加日期的小时。失败返回null。 
     * @param date 日期 
     * @param hourAmount 增加数量。可为负数 
     * @return 增加小时后的日期 
     */  
    public static Date addHour(Date date, int hourAmount) {  
        return addInteger(date, Calendar.HOUR_OF_DAY, hourAmount);  
    }  
  
    /** 
     * @category 增加日期的分钟。失败返回null。 
     * @param date 日期 
     * @param dayAmount 增加数量。可为负数 
     * @return 增加分钟后的日期 
     */  
    public static Date addMinute(Date date, int minuteAmount) {  
        return addInteger(date, Calendar.MINUTE, minuteAmount);  
    }  
  
  
    /** 
     * @category 增加日期的秒钟。失败返回null。 
     * @param date 日期 
     * @param dayAmount 增加数量。可为负数 
     * @return 增加秒钟后的日期 
     */  
    public static Date addSecond(Date date, int secondAmount) {  
        return addInteger(date, Calendar.SECOND, secondAmount);  
    }  
  
    /** 
     * @category 获取日期的年份。失败返回0。 
     * @param date 日期 
     * @return 年份 
     */  
    public static int getYear(Date date) {  
        return getInteger(date, Calendar.YEAR);  
    }  
  
    /** 
     * @category 获取日期的月份。失败返回0。 
     * @param date 日期 
     * @return 月份 
     */  
    public static int getMonth(Date date) {  
        return getInteger(date, Calendar.MONTH);  
    }  
  
  
    /** 
     * @category 获取日期的天数。失败返回0。 
     * @param date 日期 
     * @return 天 
     */  
    public static int getDay(Date date) {  
        return getInteger(date, Calendar.DATE);  
    }  
  
  
    /** 
     * @category 获取日期的小时。失败返回0。 
     * @param date 日期 
     * @return 小时 
     */  
    public static int getHour(Date date) {  
        return getInteger(date, Calendar.HOUR_OF_DAY);  
    }  
  
  
    /** 
     * @category 获取日期的分钟。失败返回0。 
     * @param date 日期 
     * @return 分钟 
     */  
    public static int getMinute(Date date) {  
        return getInteger(date, Calendar.MINUTE);  
    }  
  
  
    /** 
     * @category 获取日期的秒钟。失败返回0。 
     * @param date 日期 
     * @return 秒钟 
     */  
    public static int getSecond(Date date) {  
        return getInteger(date, Calendar.SECOND);  
    }  
} 