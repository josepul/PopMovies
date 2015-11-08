package es.josepul.popmovies.util;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Jose on 12/07/2015.
 */
public class DateUtils {

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
    private static final String LOG_TAG = DateUtils.class.getName();

    /**
     * Get year from string date in format yyyy-MM-dd
     * @param date string date
     * @return Year of string date
     */
    public static String getYearFromDate(String date){

        String result = "";

        try {
            Date convertedDate = simpleDateFormat.parse(date);
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTime(convertedDate);
            result = String.valueOf(gregorianCalendar.get(Calendar.YEAR));
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error getting year: " + date);
            result = "0000";
        }

        return result;
    }

    /**
     * Get date in milliseconds from string date in format yyyy-MM-dd
     * @param date string date
     * @return Date in milliseconds
     */
    public static long getMillisecondsFromStringDate(String date){
        long result = 0;

        try {
            Date convertedDate = simpleDateFormat.parse(date);
            result = convertedDate.getTime();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error getting milliseconds: " + date);
        }
        return result;
    }

    /**
     * Get string date from date in milliseconds
     * @param milliseconds Date in milliseconds
     * @return String date
     */
    public static String getStringDateFromMilliseconds(long milliseconds){
        String result = "";

        try {
            Date millisecondsDate = new Date(milliseconds);
            result = simpleDateFormat.format(millisecondsDate);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error getting date from milliseconds: " + milliseconds);
        }
        return result;
    }

}
