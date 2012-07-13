             

package com.prc.tt.utils;



import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;


public class DateUtils {
    private static final DateTimeFormatter YEAR = new DateTimeFormatterBuilder().appendYear(4,
                                                                                            4).toFormatter();
    private static final DateTimeFormatter MONTH = new DateTimeFormatterBuilder().appendMonthOfYear(2).toFormatter();
    private static final DateTimeFormatter DAY = new DateTimeFormatterBuilder().appendDayOfMonth(2).toFormatter();
    private static final DateTimeFormatter T = new DateTimeFormatterBuilder().appendLiteral('T').toFormatter();
    private static final DateTimeFormatter HOUR = new DateTimeFormatterBuilder().appendHourOfDay(2).toFormatter();
    private static final DateTimeFormatter MINUTE = new DateTimeFormatterBuilder().appendMinuteOfHour(2).toFormatter();
    private static final DateTimeFormatter SECOND = new DateTimeFormatterBuilder().appendSecondOfMinute(2).toFormatter();
    private static final DateTimeFormatter MILLI = new DateTimeFormatterBuilder().appendMillisOfSecond(3).toFormatter();
    private static final DateTimeFormatter TZ = new DateTimeFormatterBuilder().appendTimeZoneOffset("Z", //$NON-NLS-1$
                                                                                                    true,
                                                                                                    2,
                                                                                                    4).toFormatter();

    public static final DateTimeFormatter MILLIS_CHI = new DateTimeFormatterBuilder().append(YEAR).append(MONTH).append(DAY).append(T).append(HOUR).append(MINUTE).append(SECOND).append(MILLI).toFormatter().withZone(DateTimeZone.forID("America/Chicago"));
    public static final DateTimeFormatter MILLIS_WITH_TZ = new DateTimeFormatterBuilder().append(YEAR).append(MONTH).append(DAY).append(T).append(HOUR).append(MINUTE).append(SECOND).append(MILLI).append(TZ).toFormatter().withZone(DateTimeZone.UTC);
    public static final DateTimeFormatter MILLIS = new DateTimeFormatterBuilder().append(YEAR).append(MONTH).append(DAY).append(T).append(HOUR).append(MINUTE).append(SECOND).append(MILLI).toFormatter().withZone(DateTimeZone.UTC);
    public static final DateTimeFormatter SECONDS_WITH_TZ = new DateTimeFormatterBuilder().append(YEAR).append(MONTH).append(DAY).append(T).append(HOUR).append(MINUTE).append(SECOND).append(TZ).toFormatter().withZone(DateTimeZone.UTC);
    public static final DateTimeFormatter SECONDS = new DateTimeFormatterBuilder().append(YEAR).append(MONTH).append(DAY).append(T).append(HOUR).append(MINUTE).append(SECOND).toFormatter().withZone(DateTimeZone.UTC);
    public static final DateTimeFormatter MINUTES_WITH_TZ = new DateTimeFormatterBuilder().append(YEAR).append(MONTH).append(DAY).append(T).append(HOUR).append(MINUTE).append(TZ).toFormatter().withZone(DateTimeZone.UTC);
    public static final DateTimeFormatter MINUTES =  new DateTimeFormatterBuilder().append(YEAR).append(MONTH).append(DAY).append(T).append(HOUR).append(MINUTE).toFormatter().withZone(DateTimeZone.UTC);
    public static final DateTimeFormatter DAYS_WITH_TZ = new DateTimeFormatterBuilder().append(YEAR).append(MONTH).append(DAY).append(TZ).toFormatter().withZone(DateTimeZone.UTC);
    public static final DateTimeFormatter DAYS = new DateTimeFormatterBuilder().append(YEAR).append(MONTH).append(DAY).toFormatter().withZone(DateTimeZone.UTC);


    private static final DateTimeFormatter[] DATE_FORMATS = new DateTimeFormatter[] {
        MILLIS_WITH_TZ,
        MILLIS,
        SECONDS_WITH_TZ,
        SECONDS,
        MINUTES_WITH_TZ,
        MINUTES,
        DAYS_WITH_TZ,
        DAYS};

    private static final DateTimeFormatter DEFAULT_FORMAT = MILLIS_WITH_TZ;

    public static String dateToString(Date inDate) {
        return dateToString(inDate,
                            DEFAULT_FORMAT);
    }


    public static String nowPlusOffsetToString(int offset) {
        DateTime dt = new DateTime();
        DateTime plusDuration = dt.plus(new Duration(offset));

        return plusDuration.toString(MILLIS_CHI);
    }


    public static String dateToString(Date inDate,DateTimeFormatter inFormat) {
        return inFormat.print(new DateTime(inDate));
    }




    public static Date stringToDate(String inDateString) throws Exception
    {
        for( int formatCounter=0;formatCounter<DATE_FORMATS.length;formatCounter++ ) {
            try {
                return new Date(DATE_FORMATS[formatCounter].parseDateTime(inDateString).getMillis());
            }
            catch( IllegalArgumentException e ) {
                // this format didn't work, try a less specific one
            }
        }
        throw new Exception("Eror");


    }
}

