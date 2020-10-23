package vn.easyca.signserver.webapp.config;

import vn.easyca.signserver.ra.lib.RAConfig;

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


    public interface RAConfig {
        String URL = "http://172.16.11.84:8787/api/";
        String UserName = "admin";
        String Password = "admin";
    }

    public interface HSMConfig {
        String NAME = "EasyCAToken";
        String LIB = "C:\\Windows\\System32\\easyca_csp11_v1.dll";
        String PIN = "12345678";
        String SLOT = null;
        String ATTRIBUTES = null;

//        public static final String NAME = "nCipher";
//        public static final String LIB = "/opt/nfast/toolkits/pkcs11/libcknfast.so";
//        public static final String PIN = "05111989";
//        public static final String SLOT = "761406615";
//        public static final String ATTRIBUTES = "compatibility";
    }

    private Constants() {
    }
}
