package vn.easyca.signserver.webapp.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateTimeUtils {

    public final static String DEFAULT_FORMAT = "yyyy-MM-dd";

    public static Date parse(String strDate) throws ParseException {

        SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_FORMAT);
        return formatter.parse(strDate);
    }

    public static Date tryParse(String strDate, Date defaultVal) {

        SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_FORMAT);
        try {
            return formatter.parse(strDate);
        } catch (ParseException e) {
            return defaultVal;
        }
    }


    public static String serialize(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_FORMAT);
        return formatter.format(date);
    }

    public static String getCurrentTimeStamp() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }


    public static Instant convertToInstant(String date)  {
        SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_FORMAT);
        Date dates = null;
        try {
            dates = formatter.parse(date);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dates.toInstant();
    }
}
