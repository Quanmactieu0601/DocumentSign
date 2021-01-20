package vn.easyca.signserver.webapp.security;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.springframework.stereotype.Component;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.webapp.domain.OtpHistory;
import vn.easyca.signserver.webapp.domain.UserEntity;
import vn.easyca.signserver.webapp.service.OtpHistoryService;
import vn.easyca.signserver.webapp.service.SystemConfigCachingService;
import vn.easyca.signserver.webapp.service.UserApplicationService;
import vn.easyca.signserver.webapp.utils.SymmetricEncryptors;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Component
public class AuthenticatorTOTPService {
    public static String APP_NAME = "Easy-Sign";
    public static String QR_PREFIX =
        "https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=";

    private final SymmetricEncryptors symmetricEncryptors;
    private final SystemConfigCachingService systemConfigCachingService;
    private final UserApplicationService userApplicationService;
    private final OtpHistoryService otpHistoryService;

    public AuthenticatorTOTPService(SymmetricEncryptors symmetricEncryptors, SystemConfigCachingService systemConfigCachingService, UserApplicationService userApplicationService, OtpHistoryService otpHistoryService) {
        this.symmetricEncryptors = symmetricEncryptors;
        this.systemConfigCachingService = systemConfigCachingService;
        this.userApplicationService = userApplicationService;
        this.otpHistoryService = otpHistoryService;
    }

    private GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder getBuilder() {
        GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder builder = new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder();
        int tolerance = 3;
        builder.setWindowSize(tolerance);
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
        int lifeTime = systemConfigCachingService.getConfig().getOtpLifeTime();
        String secretKey = symmetricEncryptors.decrypt(encryptedSecretKey);
        GoogleAuthenticator gAuth = new GoogleAuthenticator(getBuilder().build());
        boolean isAuthorized = gAuth.authorize(secretKey, Integer.valueOf(otp));
        if (lifeTime != 0) { // if use config
            Optional<UserEntity> userEntity = userApplicationService.getUserWithAuthorities();
            Optional<OtpHistory> otpHistory = otpHistoryService.findTop1By(userEntity.get().getId(), encryptedSecretKey, otp, LocalDateTime.now());
            if (!isAuthorized && !otpHistory.isPresent()) {
                return false;
            } else {
                if (!otpHistory.isPresent()) {
                    OtpHistory _otpHistory = new OtpHistory();
//                otpHistory.setComId();
                    _otpHistory.setUserId(userEntity.get().getId());
                    _otpHistory.setSecretKey(encryptedSecretKey);
                    _otpHistory.setOtp(otp);
                    _otpHistory.setActionTime(LocalDateTime.now());
                    _otpHistory.setExpireTime(LocalDateTime.now().plus(lifeTime, ChronoUnit.SECONDS));
                    otpHistoryService.save(_otpHistory);
                }
                return true;
            }
        }
        return isAuthorized;
    }

    public boolean isAuthorizedRawKey(String secretKey, String otp) {
        GoogleAuthenticator gAuth = new GoogleAuthenticator(getBuilder().build());
        return gAuth.authorize(secretKey, Integer.valueOf(otp));
    }
}
