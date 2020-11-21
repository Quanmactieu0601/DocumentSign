package vn.easyca.signserver.core.model;

import vn.easyca.signserver.pki.cryptotoken.*;
import vn.easyca.signserver.core.domain.Certificate;
import vn.easyca.signserver.pki.sign.cache.AbstractCachedObject;
import vn.easyca.signserver.pki.sign.cache.GuavaCache;
import vn.easyca.signserver.infrastructure.database.jpa.entity.CertificateEntity;
import vn.easyca.signserver.core.domain.TokenInfo;
import vn.easyca.signserver.pki.cryptotoken.error.*;

import java.io.ByteArrayInputStream;
import java.util.Base64;

// TODO: Init factory as service
public class CryptoTokenProxyFactory {

    public CryptoTokenProxy resolveCryptoTokenProxy(Certificate certificate, String pin) throws CryptoTokenProxyException {
        if (certificate == null)
            throw new CryptoTokenProxyException("Certificate is not null");
        GuavaCache cache = GuavaCache.getInstance();
        if (!cache.contain(certificate.getSerial())) {
            if (pin == null || pin.isEmpty())
                pin = certificate.getTokenInfo().getPassword();
            CryptoToken token = resolveToken(certificate.getTokenInfo(), certificate.getTokenType(), pin);
            cache.set(certificate.getSerial(), new CacheElement(token, certificate.getTokenType()));
        }
        return new CryptoTokenProxy(((CacheElement) cache.get(certificate.getSerial())).getCryptoToken(), certificate);
    }

    private CryptoToken resolveToken(TokenInfo tokenInfo, String type, String pin) throws CryptoTokenProxyException {
        switch (type) {
            case CertificateEntity.PKCS_11:
                return resolveP11Token(tokenInfo);
            case CertificateEntity.PKCS_12:
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
