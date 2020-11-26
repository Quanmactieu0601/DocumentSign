package vn.easyca.signserver.webapp.config;

import vn.easyca.signserver.ra.lib.RAConfig;

import java.util.Arrays;
import java.util.List;

/**
 * Application constants.
 */
public final class Constants {

    // Regex for acceptable logins
    public static final String LOGIN_REGEX = "^(?>[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)|(?>[_.@A-Za-z0-9-]+)$";

    public static final String SYSTEM_ACCOUNT = "system";
    public static final String DEFAULT_LANGUAGE = "en";
    public static final String ANONYMOUS_USER = "anonymoususer";
    public static final List<String> HASH_ALGORITHM = Arrays.asList("SHA1", "SHA256", "SHA512");


    public interface RAConfig {
        String URL = "http://172.16.11.84:8787/api/";
        String UserName = "admin";
        String Password = "admin";
    }
}
