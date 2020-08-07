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

    public static class RaConfig {
        public static final String URL = "http://14.225.17.177:8787/api/";
        public static final String USER = "admin";
        public static final String PASS = "Sds@202o";
    }

    public static class HSMConfig {
        public static final String NAME = "nCipher";
        public static final String LIB = "/opt/nfast/toolkits/pkcs11/libcknfast.so";
        public static final String PIN = "05111989";
        public static final String SLOT = "761406615";
        public static final String ATTRIBUTES = "compatibility";
    }
}
