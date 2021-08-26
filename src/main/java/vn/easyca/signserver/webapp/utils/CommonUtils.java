package vn.easyca.signserver.webapp.utils;

import vn.easyca.signserver.core.exception.ApplicationException;

import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class CommonUtils {
    public static String genRandomAlias() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String genRandomHsmCertPin() {
        int min = 100000;
        int max = 999999;
        Random random = new Random();
        int randomNumber =  random.nextInt((max - min) +  1) + min;
        return String.valueOf(randomNumber);
    }

    public static boolean isExpired(X509Certificate x509Certificate, Date signDate) throws ApplicationException {
        X509Certificate cert = null;
        cert = x509Certificate;
        Date notAfter = cert.getNotAfter();
        Date notBefore = cert.getNotBefore();
        if (signDate == null) signDate = new Date();
        return notAfter.before(signDate) || notBefore.after(signDate);
    }
}
