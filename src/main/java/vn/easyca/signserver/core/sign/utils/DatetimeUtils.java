package vn.easyca.signserver.core.sign.utils;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by chen on 7/21/17.
 */
public class DatetimeUtils {
    public static String convertDateToString(java.util.Date d, String format) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        return convertDateToString(c, format);
    }

    public static String convertDateToString(Calendar d, String format) {
        String dd = Integer.toString(d.get(5));
        String mm = Integer.toString(d.get(2) + 1);
        String yyyy = Integer.toString(d.get(1));
        String hh = Integer.toString(d.get(11));
        String mi = Integer.toString(d.get(12));
        String ss = Integer.toString(d.get(13));
        String ms = Integer.toString(d.get(14));

        if (dd.length() == 1) {
            dd = "0" + dd;
        }
        if (mm.length() == 1) {
            mm = "0" + mm;
        }
        if (hh.length() == 1) {
            hh = "0" + hh;
        }
        if (mi.length() == 1) {
            mi = "0" + mi;
        }
        if (ss.length() == 1) {
            ss = "0" + ss;
        }
        if (ms.length() == 1) {
            ms = "0" + ms;
        }
        if ("DD".equalsIgnoreCase(format)) {
            return dd;
        }
        if ("MM".equalsIgnoreCase(format)) {
            return mm;
        }
        if ("YYYY".equalsIgnoreCase(format)) {
            return yyyy;
        }
        if ("MM/YYYY".equals(format)) {
            return mm + "/" + yyyy;
        }
        if ("DD/MM/YYYY".equals(format)) {
            return dd + "/" + mm + "/" + yyyy;
        }
        if ("DD/MM/YYYY HH:MI:SS".equals(format)) {
            return dd + "/" + mm + "/" + yyyy + " " + hh + ":" + mi + ":" + ss;
        }
        if ("DDMMYYYYHH24MISS".equals(format)) {
            return dd + mm + yyyy + hh + mi + ss;
        }
        if ("DDMMYYYYHH24MISSMS".equals(format)) {
            return dd + mm + yyyy + hh + mi + ss + ms;
        }

        return null;
    }

    public static Calendar convertStringToDate(String strDate, String format) {
        Calendar cal = null;
        if ("DD/MM/YYYY".equals(format)) {
            String[] dElement = strDate.split("/");
            cal = Calendar.getInstance();
            cal.set(5, new Integer(dElement[0]).intValue());
            cal.set(2, new Integer(dElement[1]).intValue() - 1);
            cal.set(1, new Integer(dElement[2]).intValue());
        } else if ("DD/MM/YYYY HH:MI:SS".equals(format)) {
            String dateValue = strDate.substring(0, strDate.indexOf(" "));
            String timeValue = strDate.substring(strDate.indexOf(" ") + 1);
            String[] dElement = dateValue.split("/");
            String[] tElement = timeValue.split(":");
            cal = Calendar.getInstance();
            cal.set(5, new Integer(dElement[0]).intValue());
            cal.set(2, new Integer(dElement[1]).intValue() - 1);
            cal.set(1, new Integer(dElement[2]).intValue());
            cal.set(11, new Integer(tElement[0]).intValue());
            cal.set(12, new Integer(tElement[1]).intValue());
            cal.set(13, new Integer(tElement[2]).intValue());
        }

        return cal;
    }

    public static Calendar convertStringToDate(String strDate, String format, String timezoneID) {
        Calendar cal = convertStringToDate(strDate, format);
        cal.setTimeZone(TimeZone.getTimeZone(timezoneID));
        return cal;
    }
}
