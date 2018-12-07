package com.lb.baseui.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author LiuBo
 * @date 2018-10-31
 */
public final class TimeUtils {
    /**
     * 一天多少s
     */
    public static final int DAY_SECOND = 24 * 3600;

    /**
     * 获取某月某天在 是今年的第几天
     * @param calendar 日历对象
     * @param month 0-11
     * @param day 1-28 29 30 31
     * @return -1 异常 1-365 366
     */
    public static int getDayOfYear(Calendar calendar, int month, int day) {
        try {
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            return calendar.get(Calendar.DAY_OF_YEAR);
        } catch (Exception e) {
        }
        return -1;
    }

    public static String getFormatTime(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINA);
        return sdf.format(new Date());
    }
}
