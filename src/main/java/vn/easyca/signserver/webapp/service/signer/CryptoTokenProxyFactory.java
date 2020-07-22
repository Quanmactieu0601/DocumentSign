package vn.easyca.signserver.webapp.service.signer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import vn.easyca.signserver.core.cryptotoken.Config;
import vn.easyca.signserver.core.cryptotoken.CryptoToken;
import vn.easyca.signserver.core.cryptotoken.P11CryptoToken;
import vn.easyca.signserver.core.cryptotoken.P12CryptoToken;
import vn.easyca.signserver.webapp.domain.Certificate;
import vn.easyca.signserver.webapp.domain.TokenInfo;
import vn.easyca.signserver.webapp.service.certificate.CertificateService;
import vn.easyca.signserver.webapp.service.error.sign.InitTokenProxyException;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.Optional;


public class CryptoTokenProxyFactory {

    private CertificateService certificateService;

    public CryptoTokenProxyFactory(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    public CryptoTokenProxy resolveCryptoTokenProxy(String serial, String pin) throws Exception {
        Optional<Certificate> optionalCertificate = certificateService.findBySerial(serial);
        if (!optionalCertificate.isPresent())
            throw new Exception("Certificate that has serial" + serial + "not found");
        Certificate certificate = optionalCertificate.get();
        CryptoToken cryptoToken = resolveToken(certificate, pin);
        return new CryptoTokenProxy(cryptoToken, certificate);
    }

    private CryptoToken resolveToken(Certificate certificate, String pin) throws Exception {
        String tokenType = certificate.getTokenType();
        switch (tokenType) {
            case Certificate.PKCS_11:
                return resolveP11Token(certificate, pin);
            case Certificate.PKCS_12:
                return resolveP12Token(certificate, pin);
            default:
                throw new InitTokenProxyException("Not found token type" + tokenType);
        }
    }

    private CryptoToken resolveP12Token(Certificate certificate, String pin) {

        TokenInfo tokenInfo = certificate.getCertificateTokenInfo();
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

    private CryptoToken resolveP11Token(Certificate certificate, String pin) {

        TokenInfo tokenInfo = certificate.getCertificateTokenInfo();
        P11CryptoToken p11CryptoToken = new P11CryptoToken();
        Config config = new Config();
        config.initPkcs11(tokenInfo.getName(), tokenInfo.getLibrary(), pin);
        try {
            p11CryptoToken.init(config);
            return p11CryptoToken;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return p11CryptoToken;
    }


}
