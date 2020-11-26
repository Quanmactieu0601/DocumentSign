package vn.easyca.signserver.core.model;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.pki.cryptotoken.*;
import vn.easyca.signserver.core.domain.CertificateDTO;
import vn.easyca.signserver.pki.cryptotoken.HsmConfig;
import vn.easyca.signserver.pki.cryptotoken.CryptoToken;
import vn.easyca.signserver.pki.cryptotoken.P12CryptoToken;
import vn.easyca.signserver.pki.sign.cache.AbstractCachedObject;
import vn.easyca.signserver.pki.sign.cache.GuavaCache;
import vn.easyca.signserver.webapp.config.HsmTypeConstant;
import vn.easyca.signserver.webapp.domain.Certificate;
import vn.easyca.signserver.core.domain.TokenInfo;
import vn.easyca.signserver.pki.cryptotoken.error.*;
import vn.easyca.signserver.webapp.utils.SymmetricEncryptors;

@Component
public class CryptoTokenProxyFactory {
    private final SymmetricEncryptors symmetricService;
    private final P12CryptoToken p12CryptoToken;
    private final P11ProtectServerCryptoToken p11ProtectServerCryptoToken;
    private final P11CryptoToken p11CryptoToken;
    private final HsmConfig hsmConfig;

    public CryptoTokenProxyFactory(SymmetricEncryptors symmetricService, P12CryptoToken p12CryptoToken,
                                   P11ProtectServerCryptoToken p11ProtectServerCryptoToken, P11CryptoToken p11CryptoToken,
                                   HsmConfig hsmConfig) {
        this.symmetricService = symmetricService;
        this.p12CryptoToken = p12CryptoToken;
        this.p11CryptoToken = p11CryptoToken;
        this.p11ProtectServerCryptoToken = p11ProtectServerCryptoToken;
        this.hsmConfig = hsmConfig;
    }

    public CryptoTokenProxy resolveCryptoTokenProxy(CertificateDTO certificateDTO, String pin) throws CryptoTokenProxyException, ApplicationException {
        if (certificateDTO == null)
            throw new CryptoTokenProxyException("Certificate is not null");
        String serial = certificateDTO.getSerial();
        String encryptedPinFromDB = certificateDTO.getEncryptedPin();
        String tokenType = certificateDTO.getTokenType();
        String rawPin = null;
        // Get pin from db instead from client if db pin has value
        if (StringUtils.isNotEmpty(encryptedPinFromDB)) {
            rawPin = symmetricService.decrypt(encryptedPinFromDB);
            if (Certificate.PKCS_11.equals(tokenType) && !rawPin.equals(pin)) {
                throw new CryptoTokenProxyException("Certificate pin not correct!");
            }
        } else if (!StringUtils.isNotEmpty(pin)) {
            throw new CryptoTokenProxyException("Certificate pin not correct!");
        } else
            rawPin = pin;

        GuavaCache cache = GuavaCache.getInstance();
        String cachedKey = serial + rawPin;
        if (!cache.contain(cachedKey)) {
            CryptoToken token = resolveToken(certificateDTO.getTokenInfo(), tokenType, rawPin);
            cache.set(cachedKey, new CacheElement(token, tokenType));
        }
        return new CryptoTokenProxy(((CacheElement) cache.get(cachedKey)).getCryptoToken(), certificateDTO);
    }

    private CryptoToken resolveToken(TokenInfo tokenInfo, String type, String pin) throws CryptoTokenProxyException {
        switch (type) {
            case Certificate.PKCS_11:
                return resolveP11Token(tokenInfo);
            case Certificate.PKCS_12:
                return resolveP12Token(tokenInfo, pin);
            default:
                throw new CryptoTokenProxyException("Not found token type" + type);
        }
    }

    public CryptoToken resolveP12Token(TokenInfo tokenInfo, String pin) throws CryptoTokenProxyException {
        P12CryptoToken p12CryptoToken = new P12CryptoToken();
        try {
            p12CryptoToken.initPkcs12(tokenInfo.getData(), pin);
        } catch (InitCryptoTokenException e) {
            throw new CryptoTokenProxyException("init token has error", e);
        }
        return p12CryptoToken;
    }

    public CryptoToken resolveP11Token(TokenInfo tokenInfo) throws CryptoTokenProxyException {
        // TODO: tokenInfo != null => resolve from DB (if needed)
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
            throw new CryptoTokenProxyException("Can not resolve CryptoToken.Please check information Serial and pin", exception);
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
