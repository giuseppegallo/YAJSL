/* 
 * YAJSL - Yet Another Java Swing Library
 *
 * Copyright (c) 2013 Giuseppe Gallo
 *
 * LICENSED UNDER:
 *
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2013 Giuseppe Gallo
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package YAJSL.Utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Collection of utility methods for dates.
 * 
 * @author Giuseppe Gallo
 */
public class DateUtils {
    
    /** The delta to be considered in order to accept January as 1 */
    private final static int JANUARY_DELTA = Calendar.JANUARY - 1;
    
    /**
     * Returns the current Date.
     * 
     * @return  the current date.
     */
    public static Date now() {
        return now(false);
    }
    
    /**
     * Returns the current Date.
     * 
     * @param trunc  if true, the date is truncated to midnight
     * @return  the current date.
     */
    public static Date now(boolean trunc) {
        Calendar c = Calendar.getInstance();
        if (trunc) {
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
        }
        return c.getTime();
    }
    
    /**
     * Truncates a date to midnight and returns it.
     * 
     * @param date  the date to be truncated to midnight; if null uses the current date
     * @return  the date truncated to midnight
     */
    public static Date truncToMidnight(Date date) {
        if (date == null) return date;
        
        // Truncate the date to midnight
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        
        return c.getTime();
    }
    
    /**
     * Adds a number of years to the date passed in input
     * 
     * @param date  the starting date; if null uses the current date
     * @param years  the number of years to be added
     * @return  the date obtained by adding the years to the starting date
     */
    public static Date addYears(Date date, int years) {
        Calendar c = Calendar.getInstance();        
        if (date != null) c.setTime(date);
        
        c.add(Calendar.YEAR, years);
        
        return c.getTime();
    }

    /**
     * Adds a number of months to the date passed in input
     * 
     * @param date  the starting date; if null uses the current date
     * @param months  the number of months to be added
     * @return  the date obtained by adding the months to the starting date
     */
    public static Date addMonths(Date date, int months) {
        Calendar c = Calendar.getInstance();        
        if (date != null) c.setTime(date);
        
        c.add(Calendar.MONTH, months);
        
        return c.getTime();
    }

    /**
     * Adds a number of days to the date passed in input
     * 
     * @param date  the starting date; if null uses the current date
     * @param days  the number of days to be added
     * @return  the date obtained by adding the days to the starting date
     */
    public static Date addDays(Date date, int days) {
        Calendar c = Calendar.getInstance();        
        if (date != null) c.setTime(date);
        
        c.add(Calendar.DAY_OF_MONTH, days);
        
        return c.getTime();
    }

    /**
     * Returns the next day of month greater than the date passed in input.
     * 
     * @param date  the starting date
     * @param dom  the day of month to be returned
     * @return  the next day of month greater than the date passed in input
     */
    public static Date getNextDOM(Date date, int dom) {
        Calendar c = Calendar.getInstance();
        
        if (date != null) c.setTime(date);
        
        int day = c.get(Calendar.DAY_OF_MONTH);
        
        if (dom < day) c.add(Calendar.MONTH, 1);
        c.set(Calendar.DAY_OF_MONTH, dom);
        
        return truncToMidnight(c.getTime());
    }
    
    /**
     * Returns the difference between two dates (in days).<p>
     * The dates must be truncated to midnight.
     * 
     * @param start  the lower date
     * @param end  the higher date
     * @return  the difference between two dates (in days)
     */
    public static long diffDates(Date start, Date end) {
        return (start == null || end == null) ? 0 : Math.round((end.getTime() - start.getTime())/86400000D);
    }

    /**
     * Returns the date (truncated to midnight) corresponding to the given year, month and day.
     * 
     * @param year  the year
     * @param month  the month (1 = January)
     * @param day  the day
     * @return  the date (truncated to midnight) corresponding to the given year, month and day
     */
    public static Date getDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month + JANUARY_DELTA);
        cal.set(Calendar.DAY_OF_MONTH, day);
        
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        return cal.getTime();
    }
    
    /**
     * Returns the day of month of the date passed in input.
     * 
     * @param date  the date
     * @return  the day of month of the date passed in input
     */
    public static int getDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Returns true if the date passed in input is the last day of a month.
     * @param date  the date
     * @return  true if the date passed in input is the last day of a month
     */
    public static boolean isMonthEnd(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return (day == 1);
    }
    
    /**
     * Returns the minimum date between the 2 passed in input.
     * 
     * @param date1  the first date
     * @param date2  the second date
     * @return  the minimum date between the 2 passed in input
     */
    public static Date min(Date date1, Date date2) {
        return (date1 == null || date2 == null) ? null : (date1.before(date2)) ? date1 : date2;
    }
}
