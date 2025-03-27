package com.example.cs4084_group_01.util;

import java.util.Calendar;
import java.util.Date;

/**
 * Utility class for handling date operations
 */
public class DateUtils {
    
    /**
     * Checks if a given date is today
     * 
     * @param date The date to check
     * @return true if the date is today, false otherwise
     */
    public static boolean isToday(Date date) {
        if (date == null) {
            return false;
        }
        
        Calendar today = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        
        return today.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
               today.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR);
    }
    
    /**
     * Gets the start of the day for a given date
     * 
     * @param date The date to get the start of day for
     * @return A new Date representing the start of the given day
     */
    public static Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
    
    /**
     * Gets the end of the day for a given date
     * 
     * @param date The date to get the end of day for
     * @return A new Date representing the end of the given day
     */
    public static Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }
    
    /**
     * Checks if two dates are on the same day
     * 
     * @param date1 The first date
     * @param date2 The second date
     * @return true if both dates are on the same day, false otherwise
     */
    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
               cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }
    
    private DateUtils() {
        // Private constructor to prevent instantiation
    }
} 