package vn.easyca.signserver.sign.core.sign.utils;

import java.util.UUID;

/**
 * Created by chen on 7/26/17.
 */
public class UniqueID {
    public static String generate() {
        return UUID.randomUUID().toString();
    }
}
