package vn.easyca.signserver.core.model;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.core.services.SymmetricService;
import vn.easyca.signserver.pki.cryptotoken.*;
import vn.easyca.signserver.core.domain.CertificateDTO;
import vn.easyca.signserver.pki.cryptotoken.Config;
import vn.easyca.signserver.pki.cryptotoken.CryptoToken;
import vn.easyca.signserver.pki.cryptotoken.P12CryptoToken;
import vn.easyca.signserver.pki.sign.cache.AbstractCachedObject;
import vn.easyca.signserver.pki.sign.cache.GuavaCache;
import vn.easyca.signserver.webapp.domain.Certificate;
import vn.easyca.signserver.core.domain.TokenInfo;
import vn.easyca.signserver.pki.cryptotoken.error.*;

import java.io.ByteArrayInputStream;
import java.util.Base64;

@Component
public class CryptoTokenProxyFactory {
    private final SymmetricService symmetricService;

    public CryptoTokenProxyFactory(SymmetricService symmetricService) {
        this.symmetricService = symmetricService;
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
        if (!cache.contain(serial)) {
            CryptoToken token = resolveToken(certificateDTO.getTokenInfo(), tokenType, rawPin);
            cache.set(serial, new CacheElement(token, tokenType));
        }
        return new CryptoTokenProxy(((CacheElement) cache.get(serial)).getCryptoToken(), certificateDTO);
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

    private CryptoToken resolveP12Token(TokenInfo tokenInfo, String pin) throws CryptoTokenProxyException {
        byte[] fileContent = Base64.getDecoder().decode(tokenInfo.getData());
        P12CryptoToken p12CryptoToken = new P12CryptoToken();
        Config config = new Config();
        config.initPkcs12(new ByteArrayInputStream(fileContent), pin);
        try {
            p12CryptoToken.init(config);
        } catch (InitCryptoTokenException e) {
            throw new CryptoTokenProxyException("init token has error", e);
        }
        return p12CryptoToken;
    }

    private CryptoToken resolveP11Token(TokenInfo tokenInfo) throws CryptoTokenProxyException {
        // TODO: temporary hardcode to resolve p11 crypto token
        P11ProtectServerCryptoToken p11CryptoToken = new P11ProtectServerCryptoToken();
        Config config = new Config();
        config = config.initPkcs11(tokenInfo.getName(), tokenInfo.getLibrary(), tokenInfo.getPassword());
        config = config.withSlot(tokenInfo.getSlot().toString());
        config = config.withAttributes(tokenInfo.getP11Attrs());
        try {
            p11CryptoToken.init(config);
        } catch (Exception exception) {
            throw new CryptoTokenProxyException("Can not resolve CryptoToken.Please check information Serial and pin", exception);
        }
        return p11CryptoToken;
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
