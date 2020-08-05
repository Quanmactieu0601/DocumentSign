package vn.easyca.signserver.webapp.config;

/**
 * Application constants.
 */
public final class Constants {

    // Regex for acceptable logins
    public static final String LOGIN_REGEX = "^(?>[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)|(?>[_.@A-Za-z0-9-]+)$";

    public static final String SYSTEM_ACCOUNT = "system";
    public static final String DEFAULT_LANGUAGE = "en";
    public static final String ANONYMOUS_USER = "anonymoususer";
    public static final String KEY_ENCRYPTION = "abcdpoiuytrefghijklmop";

    private Constants() {
    }

    public static class RACONFIG{
        public static final String URL = "http://172.16.11.84:8787/api/";
        public static final String USER = "admin";
        public static final String PASS = "admin";
    }
    public static class HSMCONFIG{
        public static final String NAME = "EasyCAToken";
        public static final String LIB = "C:\\Windows\\System32\\easyca_csp11_v1.dll";
        public static final String PIN = "12345678";

    }
}
