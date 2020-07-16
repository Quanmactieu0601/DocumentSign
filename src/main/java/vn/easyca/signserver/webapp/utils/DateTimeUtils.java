package vn.easyca.signserver.webapp.utils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateTimeUtils {

    public final static String DEFAULT_FORMAT = "yyyy-MM-dd";

    public static Date parse(String strDate) throws ParseException {

        SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_FORMAT);
        return formatter.parse(strDate);
    }

    public static String serialize(Date date){
        SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_FORMAT);
        return formatter.format(date);
    }
}
