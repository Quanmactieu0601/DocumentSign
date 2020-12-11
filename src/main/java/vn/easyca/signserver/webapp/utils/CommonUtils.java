package vn.easyca.signserver.webapp.utils;

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
}
