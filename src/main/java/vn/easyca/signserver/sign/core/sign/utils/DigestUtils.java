package vn.easyca.signserver.sign.core.sign.utils;

import java.security.MessageDigest;

/**
 * Created by chen on 7/26/17.
 */
public class DigestUtils {
    public static byte[] digest(byte[] data, String hashAlg) throws Exception {
        return MessageDigest.getInstance(hashAlg).digest(data);
    }
}
