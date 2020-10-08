package vn.easyca.signserver.webapp.config;

import vn.easyca.signserver.ra.RAConfig;
import vn.easyca.signserver.ra.RAServiceFade;

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

//    public static final RAServiceFade.Config RA_CONFIG = new RAServiceFade.Config("http://14.225.17.177:8787/api/", "admin", "Sds@202o");

    public static final RAConfig RA_CONFIG = new RAConfig("http://172.16.11.84:8787/api/", "admin", "admin");


    public static class HSMConfig {

//        private final String TOKEN_NAME = "EasyCAToken";
//        private final String TOKEN_LIB = "C:\\Windows\\System32\\easyca_csp11_v1.dll";
//        private final String TOKEN_PIN = "12345678";

        public static final String NAME = "EasyCAToken";
        public static final String LIB = "C:\\Windows\\System32\\easyca_csp11_v1.dll";
        public static final String PIN = "12345678";
        public static final String SLOT = null;
        public static final String ATTRIBUTES = null;

//        public static final String NAME = "nCipher";
//        public static final String LIB = "/opt/nfast/toolkits/pkcs11/libcknfast.so";
//        public static final String PIN = "05111989";
//        public static final String SLOT = "761406615";
//        public static final String ATTRIBUTES = "compatibility";
    }

    private Constants() {
    }
}
