package vn.easyca.signserver.application.model.token;
import org.springframework.stereotype.Service;
import vn.easyca.signserver.pki.cryptotoken.Config;
import vn.easyca.signserver.pki.cryptotoken.CryptoToken;
import vn.easyca.signserver.pki.cryptotoken.P11CryptoToken;
import vn.easyca.signserver.pki.cryptotoken.P12CryptoToken;
import vn.easyca.signserver.application.domain.Certificate;
import vn.easyca.signserver.pki.sign.cache.AbstractCachedObject;
import vn.easyca.signserver.pki.sign.cache.GuavaCache;
import vn.easyca.signserver.infrastructure.database.jpa.entity.CertificateEntity;
import vn.easyca.signserver.application.domain.TokenInfo;
import vn.easyca.signserver.pki.cryptotoken.error.*;

import java.io.ByteArrayInputStream;
import java.util.Base64;

@Service
public class CryptoTokenProxyFactory {

    public CryptoTokenProxy resolveCryptoTokenProxy(Certificate certificate) throws CryptoTokenProxyException {
        if (certificate == null)
            throw new CryptoTokenProxyException("Certificate is not null");
        GuavaCache cache = GuavaCache.getInstance();
        CryptoToken token = null;
        if (!cache.contain(certificate.getSerial())) {
            token = resolveToken(certificate.getTokenInfo(), certificate.getTokenType(), certificate.getTokenInfo().getPassword());
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
        P11CryptoToken p11CryptoToken = new P11CryptoToken();
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
