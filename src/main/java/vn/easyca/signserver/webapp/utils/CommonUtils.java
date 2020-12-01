package vn.easyca.signserver.webapp.utils;

import java.util.UUID;

public class CommonUtils {
    public static String genRandomAlias() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
