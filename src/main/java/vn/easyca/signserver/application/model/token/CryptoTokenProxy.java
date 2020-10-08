package vn.easyca.signserver.application.model.token;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.easyca.signserver.application.exception.TokenException;
import vn.easyca.signserver.application.services.CertificateGenerateService;
import vn.easyca.signserver.pki.cryptotoken.CryptoToken;
import vn.easyca.signserver.application.domain.Certificate;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

public class CryptoTokenProxy {

    private final Certificate certificate;

    private final CryptoToken cryptoToken;

    private final Logger log = LoggerFactory.getLogger(CertificateGenerateService.class);

    public CryptoTokenProxy(CryptoToken cryptoToken, Certificate certificate) {
        this.certificate = certificate;
        this.cryptoToken = cryptoToken;
    }

    public PrivateKey getPrivateKey() throws TokenException {
        try {
            return cryptoToken.getPrivateKey(certificate.getAlias());
        } catch (Exception e) {
            throw new TokenException();
        }
    }

    public boolean isExpired(Date date) throws TokenException {
        try {
            X509Certificate cert = null;
            cert = certificate.getX509Certificate();
            Date notAfter = cert.getNotAfter();
            Date notBefore = cert.getNotBefore();
            if (date == null) date = new Date();
            return notAfter.before(date) || notBefore.after(date);
        } catch (CertificateException e) {
            throw new TokenException();
        }
    }

    public String getBase64Certificate() {
        return certificate.getRawData();
    }

    public X509Certificate getX509Certificate() throws TokenException {
        try {
            return certificate.getX509Certificate();
        } catch (CertificateException e) {
            log.error(e.getLocalizedMessage());
            throw new TokenException();
        }
    }

    public PublicKey getPublicKey() throws TokenException {
        try {
            return cryptoToken.getPublicKey(certificate.getAlias());
        } catch (Exception ex) {
            log.error(ex.getLocalizedMessage());
            throw new TokenException();
        }
    }

    public Certificate getCertificate() {
        return certificate;
    }
}
