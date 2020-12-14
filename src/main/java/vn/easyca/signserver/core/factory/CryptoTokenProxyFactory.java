package vn.easyca.signserver.core.factory;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.pki.cryptotoken.*;
import vn.easyca.signserver.core.domain.CertificateDTO;
import vn.easyca.signserver.webapp.config.HsmConfig;
import vn.easyca.signserver.pki.cryptotoken.CryptoToken;
import vn.easyca.signserver.pki.cryptotoken.P12CryptoToken;
import vn.easyca.signserver.pki.sign.cache.AbstractCachedObject;
import vn.easyca.signserver.pki.sign.cache.GuavaCache;
import vn.easyca.signserver.webapp.config.HsmTypeConstant;
import vn.easyca.signserver.webapp.domain.Certificate;
import vn.easyca.signserver.core.domain.TokenInfo;
import vn.easyca.signserver.pki.cryptotoken.error.*;
import vn.easyca.signserver.webapp.security.AuthenticatorTOTPService;
import vn.easyca.signserver.webapp.service.SystemConfigCachingService;
import vn.easyca.signserver.webapp.utils.SymmetricEncryptors;

@Component
public class CryptoTokenProxyFactory {
    private final SymmetricEncryptors symmetricService;
    private final P12CryptoToken p12CryptoToken;
    private final P11ProtectServerCryptoToken p11ProtectServerCryptoToken;
    private final P11CryptoToken p11CryptoToken;
    private final HsmConfig hsmConfig;
    private final SystemConfigCachingService systemConfigCachingService;
    private final AuthenticatorTOTPService authenticatorTOTPService;

    public CryptoTokenProxyFactory(SymmetricEncryptors symmetricService, P12CryptoToken p12CryptoToken,
                                   P11ProtectServerCryptoToken p11ProtectServerCryptoToken, P11CryptoToken p11CryptoToken,
                                   HsmConfig hsmConfig, SystemConfigCachingService systemConfigCachingService, AuthenticatorTOTPService authenticatorTOTPService) {
        this.symmetricService = symmetricService;
        this.p12CryptoToken = p12CryptoToken;
        this.p11CryptoToken = p11CryptoToken;
        this.p11ProtectServerCryptoToken = p11ProtectServerCryptoToken;
        this.hsmConfig = hsmConfig;
        this.systemConfigCachingService = systemConfigCachingService;
        this.authenticatorTOTPService = authenticatorTOTPService;
    }

    public CryptoTokenProxy resolveCryptoTokenProxy(CertificateDTO certificateDTO, String pin)  throws ApplicationException {
        if (certificateDTO == null)
            throw new ApplicationException("Certificate is not null");
        return getCryptoTokenProxy(certificateDTO, pin);
    }

    public CryptoTokenProxy resolveCryptoTokenProxy(CertificateDTO certificateDTO, String pin, String otp) throws ApplicationException {
        if (certificateDTO == null)
            throw new ApplicationException("Certificate is not null");

        boolean useOTP = systemConfigCachingService.getConfig().getUseOTP();
        if (useOTP) {
            if (StringUtils.isEmpty(otp))
                throw new ApplicationException("OTP is empty");
            boolean isOTPValid = authenticatorTOTPService.isAuthorized(certificateDTO.getSecretKey(), otp);
            if (!isOTPValid) {
                throw new ApplicationException("OTP is not valid");
            }
        }
        return getCryptoTokenProxy(certificateDTO, pin);
    }

    private CryptoTokenProxy getCryptoTokenProxy(CertificateDTO certificateDTO, String pin) throws ApplicationException {
        boolean skipCheckPassword = systemConfigCachingService.getConfig().getSaveTokenPassword();
        String serial = certificateDTO.getSerial();
        String tokenType = certificateDTO.getTokenType();
        String encryptedPinFromDB = certificateDTO.getEncryptedPin();
        String rawPin = null;

        if (Certificate.PKCS_12.equals(tokenType)) {
            if (skipCheckPassword) {
                if (StringUtils.isEmpty(encryptedPinFromDB))
                    throw new ApplicationException("P12 DB password is empty");
                rawPin = symmetricService.decrypt(encryptedPinFromDB);
            } else {
                rawPin = pin;
            }
        } else if (Certificate.PKCS_11.equals(tokenType)) {
            if (StringUtils.isEmpty(encryptedPinFromDB))
                throw new ApplicationException("HSM DB password is empty");
            rawPin = symmetricService.decrypt(encryptedPinFromDB);
            if (!rawPin.equals(pin))
                throw new ApplicationException("Certificate pin not correct!");
        }

        GuavaCache cache = GuavaCache.getInstance();
        String cachedKey = serial + rawPin;
        if (!cache.contain(cachedKey)) {
            CryptoToken token = resolveToken(certificateDTO.getTokenInfo(), tokenType, rawPin);
            cache.set(cachedKey, new CacheElement(token, tokenType));
        }
        return new CryptoTokenProxy(((CacheElement) cache.get(cachedKey)).getCryptoToken(), certificateDTO);
    }

    private CryptoToken resolveToken(TokenInfo tokenInfo, String type, String pin) throws ApplicationException {
        switch (type) {
            case Certificate.PKCS_11:
                return resolveP11Token(tokenInfo);
            case Certificate.PKCS_12:
                return resolveP12Token(tokenInfo, pin);
            default:
                throw new ApplicationException("Not found token type" + type);
        }
    }

    public CryptoToken resolveP12Token(TokenInfo tokenInfo, String pin) throws ApplicationException {
        P12CryptoToken p12CryptoToken = new P12CryptoToken();
        try {
            p12CryptoToken.initPkcs12(tokenInfo.getData(), pin);
        } catch (InitCryptoTokenException e) {
            throw new ApplicationException("init token has error", e);
        }
        return p12CryptoToken;
    }

    public CryptoToken resolveP11Token(TokenInfo tokenInfo) throws ApplicationException {
        // TODO: tokenInfo != null => resolve from DB (if needed | used for multiple hsm devices are running)
        CryptoToken cryptoToken = null;
        switch (hsmConfig.getType()) {
            case HsmTypeConstant.HSM_PROTECT_SERVER:
                cryptoToken = p11ProtectServerCryptoToken;
                break;
            default: cryptoToken = p11CryptoToken;
        }
        try {
            cryptoToken.initPkcs11();
        } catch (Exception exception) {
            throw new ApplicationException("Can not resolve CryptoToken.Please check information Serial and pin", exception);
        }
        return cryptoToken;
    }


    private static class CacheElement extends AbstractCachedObject {
        private final CryptoToken cryptoToken;
        private final String signType;

        private CacheElement(CryptoToken cryptoToken, String signType) {
            this.cryptoToken = cryptoToken;
            this.signType = signType;
        }

        @Override
        public String getSigningType() {
            return signType;
        }

        public CryptoToken getCryptoToken() {
            return cryptoToken;
        }
    }
}
