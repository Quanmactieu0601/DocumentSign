package vn.easyca.signserver.webapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class OTPService {

    private static final Logger log = LoggerFactory.getLogger(OTPService.class);
    private final SenderService senderService;
    private static final String CHARACTERS = "0123456789";
    private static final int OTP_LENGTH = 6;

    public OTPService(SenderService senderService) {
        this.senderService = senderService;
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
    public void CreateNewOTP(){

    }
}
