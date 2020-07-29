package vn.easyca.signserver.core.signer;

import org.springframework.beans.factory.annotation.Autowired;
import vn.easyca.signserver.sign.core.cryptotoken.Config;
import vn.easyca.signserver.sign.core.cryptotoken.CryptoToken;
import vn.easyca.signserver.sign.core.cryptotoken.P11CryptoToken;
import vn.easyca.signserver.sign.core.cryptotoken.P12CryptoToken;
import vn.easyca.signserver.core.domain.Certificate;
import vn.easyca.signserver.webapp.domain.CertificateEntity;
import vn.easyca.signserver.core.domain.TokenInfo;
import vn.easyca.signserver.core.services.CertificateService;
import vn.easyca.signserver.core.error.sign.InitTokenProxyException;

import java.io.ByteArrayInputStream;
import java.util.Base64;

public class CryptoTokenProxyFactory {

    private static final String TOKEN_NAME = "token";
    private static final String TOKEN_LIB = "C:\\Windows\\System32\\easyca_csp11_v1.dll";
    private static final String TOKEN_PASS = "12345678";

    @Autowired
    private CertificateService certificateService;

    public CryptoTokenProxy resolveCryptoTokenProxy(String serial,String pin) throws Exception {
        Certificate certificate = certificateService.getBySerial(serial);
        if (certificate == null)
            throw new Exception("Certificate that has serial" + serial + "not found");
        CryptoToken cryptoToken = resolveToken(certificate, pin);
        return new CryptoTokenProxy(cryptoToken, certificate);
    }

    private CryptoToken resolveToken(Certificate certificate, String pin) throws Exception {
        String tokenType = certificate.getTokenType();
        switch (tokenType) {
            case CertificateEntity.PKCS_11:
                return resolveP11Token();
            case CertificateEntity.PKCS_12:
                return resolveP12Token(certificate, pin);
            default:
                throw new InitTokenProxyException("Not found token type" + tokenType);
        }
    }

    private CryptoToken resolveP12Token(Certificate certificate, String pin) {

        TokenInfo tokenInfo = certificate.getTokenInfo();
        byte[] fileContent = Base64.getDecoder().decode(tokenInfo.getData());
        P12CryptoToken p12CryptoToken = new P12CryptoToken();
        Config config = new Config();
        config.initPkcs12(new ByteArrayInputStream(fileContent), pin);
        try {
            p12CryptoToken.init(config);
            return p12CryptoToken;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return p12CryptoToken;
    }

    private CryptoToken resolveP11Token() {
        P11CryptoToken p11CryptoToken = new P11CryptoToken();
        Config config = new Config();
        config = config.initPkcs11(TOKEN_NAME,TOKEN_LIB,TOKEN_PASS);
        try {
            p11CryptoToken.init(config);
            return p11CryptoToken;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return p11CryptoToken;
    }


}
