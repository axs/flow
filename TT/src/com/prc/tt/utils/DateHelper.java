             

package com.prc.tt.utils;


import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.text.*;


public class DateHelper {

    public static String getYesterdaysDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.DATE, -1);
        String date = format.format(cal.getTime());

        return date;
    }


    public static String getTodaysDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String date = format.format(new java.util.Date());

        return date;
    }


    public static String getFridaysDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = new GregorianCalendar();

        while ( cal.get(Calendar.DAY_OF_WEEK) != 6 ) {
            cal.add(Calendar.DATE, -1);
        }
        String date = format.format(cal.getTime());

        return date;
    }

    public static String getPreviousBusDate() {
        Calendar cal = new GregorianCalendar();
        String date  = null;
        if ( cal.get(Calendar.DAY_OF_WEEK) == 1 || cal.get(Calendar.DAY_OF_WEEK) == 2 ) {
            date = getFridaysDate();
        }
        else {
            date=getYesterdaysDate();
        }

        return date;
    }

}
