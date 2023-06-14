package vn.easyca.signserver.webapp.service;

import com.google.common.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.easyca.signserver.webapp.utils.CacheProvider;

import java.util.Random;

public class OTPService {

    private static final Logger log = LoggerFactory.getLogger(OTPService.class);
    private final SenderService senderService;

    private final Cache<String, Object> cache;
    private static final String CHARACTERS = "0123456789";
    private static final int OTP_LENGTH = 6;

    public OTPService(SenderService senderService) {
        this.senderService = senderService;
        cache = CacheProvider.getCache();
    }

    public static String generateOTP() {
        StringBuilder otp = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < OTP_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            otp.append(CHARACTERS.charAt(index));
        }

        return otp.toString();
    }

    public String generateKeyCache(){
        return "";
    }
    public void CreateNewOTP(){
        //todo: create key

        //todo: create otp

        //todo: send OTP
    }

    public boolean ValidOTP(){
        boolean isValidOTP = false;
        //todo: create key

        //todo: get cache value by key

        //todo: Equal cache value with OTP

        //todo: return
        return isValidOTP;
    }
}
