package vn.easyca.signserver.webapp.security;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.springframework.stereotype.Component;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.webapp.utils.SymmetricEncryptors;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Component
public class AuthenticatorTOTPService {
    public static String APP_NAME = "Easy-Signing";
    public static String QR_PREFIX =
        "https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=";

    private final SymmetricEncryptors symmetricEncryptors;

    public AuthenticatorTOTPService(SymmetricEncryptors symmetricEncryptors) {
        this.symmetricEncryptors = symmetricEncryptors;
    }

    private GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder getBuilder() {
        GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder builder = new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder();
//        builder.setTimeStepSizeInMillis(0);
        builder.setWindowSize(3);
        return builder;
    }
    public String generateTOTPKey() {
        GoogleAuthenticator gAuth = new GoogleAuthenticator(getBuilder().build());
        final GoogleAuthenticatorKey key = gAuth.createCredentials();
        String secretKey = key.getKey();
        return secretKey;
    }

    public String generateEncryptedTOTPKey() throws ApplicationException {
        String secretKey = generateTOTPKey();
        return symmetricEncryptors.encrypt(secretKey);
    }

    public String getQRCodeFromSecretKey(String serial, String secretKey) throws ApplicationException {
        try {
            String subSerial = String.format("****%s", serial.substring(20));
            return QR_PREFIX + URLEncoder.encode(String.format(
                "otpauth://totp/%s:%s?secret=%s&issuer=%s",
                APP_NAME, subSerial, secretKey, APP_NAME),
                "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new ApplicationException(String.format("Error when generate QR Code URL - serial %s", serial), e);
        }
    }

    public String getQRCodeFromEncryptedSecretKey(String serial, String encryptedSecretKey) throws ApplicationException {
        String secretKey = symmetricEncryptors.decrypt(encryptedSecretKey);
        return getQRCodeFromSecretKey(serial, secretKey);
    }

    public boolean isAuthorized(String encryptedSecretKey, String otp) throws ApplicationException {
        String secretKey = symmetricEncryptors.decrypt(encryptedSecretKey);
        GoogleAuthenticator gAuth = new GoogleAuthenticator(getBuilder().build());
        return gAuth.authorize(secretKey, Integer.valueOf(otp));
    }

    public boolean isAuthorizedRawKey(String secretKey, String otp) throws ApplicationException {
        GoogleAuthenticator gAuth = new GoogleAuthenticator(getBuilder().build());
        return gAuth.authorize(secretKey, Integer.valueOf(otp));
    }
}
