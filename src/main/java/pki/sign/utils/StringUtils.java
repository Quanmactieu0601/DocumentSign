package pki.sign.utils;

/**
 * Created by chen on 8/28/17.
 */
public class StringUtils {

    public static boolean isNullOrEmpty(String input) {
        if (input == null) return true;
        return input.isEmpty();
    }
}
