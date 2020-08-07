package vn.easyca.signserver.business.services.signing.signer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.easyca.signserver.pki.cryptotoken.Config;
import vn.easyca.signserver.pki.cryptotoken.CryptoToken;
import vn.easyca.signserver.pki.cryptotoken.P11CryptoToken;
import vn.easyca.signserver.pki.cryptotoken.P12CryptoToken;
import vn.easyca.signserver.business.domain.Certificate;
import vn.easyca.signserver.webapp.config.Constants;
import vn.easyca.signserver.webapp.jpa.entity.CertificateEntity;
import vn.easyca.signserver.business.domain.TokenInfo;
import vn.easyca.signserver.business.services.CertificateService;
import vn.easyca.signserver.business.error.sign.InitTokenProxyException;

import java.io.ByteArrayInputStream;
import java.util.Base64;

@Service
public class CryptoTokenProxyFactory {

    @Autowired
    private CertificateService certificateService;

    public CryptoTokenProxy resolveCryptoTokenProxy(String serial, String pin) throws Exception {
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
                return resolveP11Token(certificate);
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

    private CryptoToken resolveP11Token(Certificate certificate) {
        P11CryptoToken p11CryptoToken = new P11CryptoToken();
        TokenInfo tokenInfo = certificate.getTokenInfo();
        Config config = new Config();
        config = config.initPkcs11(tokenInfo.getName(), tokenInfo.getLibrary(), tokenInfo.getPassword());
        config = config.withSlot(tokenInfo.getSlot().toString());
        config = config.withAttributes(tokenInfo.getP11Attrs());
        try {
            p11CryptoToken.init(config);
            return p11CryptoToken;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return p11CryptoToken;
    }


}
