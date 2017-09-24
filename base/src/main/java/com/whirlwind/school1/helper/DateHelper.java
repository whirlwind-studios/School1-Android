package com.whirlwind.school1.helper;

import android.content.Context;

import com.whirlwind.school1.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateHelper {

    public static final int START = 0, MIDDLE = 1, END = 2;
    private static final long secsPerDay = 60 * 60 * 24;
    private static final DateFormat format = DateFormat.getDateInstance(DateFormat.FULL);
    private static final Calendar calendar = Calendar.getInstance();

    public static Calendar getCalendar(long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date * 1000);
        return calendar;
    }

    public static long getDate(int year, int month, int dayOfMonth, int hour, int minute, int second) {
        synchronized (calendar) {
            calendar.set(year, month, dayOfMonth, hour, minute, second);
            return calendar.getTimeInMillis() / 1000;
        }
    }

    public static long getDate(int year, int month, int dayOfMonth) {
        return getDate(year, month, dayOfMonth, 12, 0, 0);
    }

    public static long getDate(Calendar calendar) {
        return calendar.getTimeInMillis() / 1000;
    }

    public static long getDate(Calendar calendar, int hour, int minute, int second) {
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        return getDate(calendar);
    }

    public static long getDate(Calendar calendar, int type) {
        if (type == START)
            return getDate(calendar, 0, 0, 0);
        else if (type == MIDDLE)
            return getDate(calendar, 12, 0, 0);
        else if (type == END)
            return getDate(calendar, 23, 59, 59);
        else throw new IllegalArgumentException("Parameter type must be START, MIDDLE or END");
    }

    public static String getString(Calendar calendar) {
        return format.format(calendar.getTimeInMillis());
    }

    public static String getString(long date) {
        return format.format(date * 1000);
    }

    public static String getString(String pattern, Calendar calendar) {
        return new SimpleDateFormat(pattern, Locale.getDefault()).format(calendar.getTimeInMillis());
    }

    public static String getString(String pattern, long date) {
        return new SimpleDateFormat(pattern, Locale.getDefault()).format(date * 1000);
    }

    public static long getRelativeDays(long date) {
        long time2 = System.currentTimeMillis() / 1000;
        return (date - (date % secsPerDay)) / secsPerDay - (time2 - (time2 % secsPerDay)) / secsPerDay;
    }

    public static String getStringRelative(Context context, long date) {
        long relativeDays = getRelativeDays(date);

        String string = "";
        if (relativeDays == 0)
            string = context.getString(R.string.message_date_today);
        else if (relativeDays == 1)
            string = context.getString(R.string.message_date_tomorrow);

        Calendar dateCalendar = Calendar.getInstance(),
                todayCalendar = Calendar.getInstance();
        dateCalendar.setTimeInMillis(date * 1000);

        DateFormat dateFormat = format;

        if (dateCalendar.get(Calendar.YEAR) == todayCalendar.get(Calendar.YEAR))
            dateFormat = new SimpleDateFormat(((SimpleDateFormat) format).toLocalizedPattern()
                    .replaceAll(
                            "([^\\p{Alpha}']|('[\\p{Alpha}]+'))*y+([^\\p{Alpha}']|('[\\p{Alpha}]+'))*",
                            ""));

        return string + dateFormat.format(date * 1000);
    }

    public static String getStringRelative(Context context, Calendar calendar) {
        return getStringRelative(context, calendar.getTimeInMillis() / 1000);
    }
}