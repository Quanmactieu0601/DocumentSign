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

    public interface HASH_ALGORITHM {
        String SHA1 = "SHA1";
        String SHA256 = "SHA256";
        String SHA512 = "SHA512";
        String DEFAULT_HASH_ALGORITHM = SHA1;
    }

    public static final List<String> HASH_ALGORITHMS = Arrays.asList(HASH_ALGORITHM.SHA1, HASH_ALGORITHM.SHA256, HASH_ALGORITHM.SHA512);

    public static final String TEMP_FOLDER_DIRECTORY = "opt/easysign/tmp";
}
