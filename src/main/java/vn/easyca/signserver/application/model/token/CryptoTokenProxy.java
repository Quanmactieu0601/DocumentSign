package vn.easyca.signserver.application.model.token;

import vn.easyca.signserver.pki.cryptotoken.CryptoToken;
import vn.easyca.signserver.application.domain.Certificate;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

import vn.easyca.signserver.pki.cryptotoken.error.*;

public class CryptoTokenProxy {

    private final Certificate certificate;

    private final CryptoToken cryptoToken;

    public CryptoTokenProxy(CryptoToken cryptoToken, Certificate certificate) {
        this.certificate = certificate;
        this.cryptoToken = cryptoToken;
    }

    public PrivateKey getPrivateKey() throws CryptoTokenException {
        return cryptoToken.getPrivateKey(certificate.getAlias());
    }

    public boolean isExpired(Date date) throws CertificateException {
        X509Certificate cert = null;
        cert = certificate.getX509Certificate();
        Date notAfter = cert.getNotAfter();
        Date notBefore = cert.getNotBefore();
        if (date == null) date = new Date();
        return notAfter.before(date) || notBefore.after(date);
    }

    public String getBase64Certificate() {
        return certificate.getRawData();
    }

    public X509Certificate getX509Certificate() throws CertificateException {
        return certificate.getX509Certificate();
    }

    public PublicKey getPublicKey() throws CryptoTokenException {
        return cryptoToken.getPublicKey(certificate.getAlias());
    }

    public Certificate getCertificate() {
        return certificate;
    }
}
