package com.silverkeytech.news_engine;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Ryan
 */
public class DateHelper {
    private static final String TAG = DateHelper.class.getSimpleName();

    public final static String DEFAULT = "MM/dd/yyyy hh:mm:ss a Z";
    public final static String ISO8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSZ";
    public final static String ISO8601_NOMS = "yyyy-MM-dd'T'HH:mm:ssZ";
    public final static String RFC822 = "EEE, dd MMM yyyy HH:mm:ss Z";
    public final static String SIMPLE = "MM/dd/yyyy hh:mm:ss a";
    public final static String NO_SPACES = "dd MMM yyyy HH:mm:ss Z";
    public final static String ISO8601_NOMS_NO_TZ = "yyyy-MM-dd'T'HH:mm:ss";

    public static String format(String format, Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static String format(Date date) {
        return format(DEFAULT, date);
    }

    public static String formatISO8601(Date date) {
        return format(ISO8601, date);
    }

    public static String formatISO8601NoMilliseconds(Date date) {
        return format(ISO8601_NOMS, date);
    }

    public static String formatRFC822(Date date) {
        return format(RFC822, date);
    }

    public static Date parse(String date) throws ParseException {
        return parse(DEFAULT, date);
    }

    public static Date parse(String format, String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.parse(date);
    }

    public static Date parseISO8601(String date) throws ParseException {
        return parse(ISO8601, date);
    }

    public static Date parseISO8601NoMilliseconds(String date) throws ParseException {
        return parse(ISO8601_NOMS, date);
    }

    public static Date parseRFC822(String date) throws ParseException {
        return parse(RFC822, date);
    }

    public static Date Now() {
        return new Date();
    }
}
