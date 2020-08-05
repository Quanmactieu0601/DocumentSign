package vn.easyca.signserver.business.services.signing.signer;

import vn.easyca.signserver.pki.cryptotoken.CryptoToken;
import vn.easyca.signserver.business.domain.Certificate;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * instead for token domain.
 * help connecting to core. manage token's info.
 */

public class CryptoTokenProxy {

    private final Certificate certificate;

    private final CryptoToken cryptoToken;

    public CryptoTokenProxy(CryptoToken cryptoToken, Certificate certificate) throws Exception {
        this.certificate = certificate;
        this.cryptoToken = cryptoToken;
    }

    public PrivateKey getPrivateKey() throws Exception {
        return cryptoToken.getPrivateKey(certificate.getAlias());
    }

    public boolean isExpired(Date date) throws Exception {
        X509Certificate cert = certificate.getX509Certificate();
        Date notAfter = cert.getNotAfter();
        Date notBefore = cert.getNotBefore();
        if (date == null) date = new Date();
        return notAfter.before(date) || notBefore.after(date);
    }

    public String getBase64Certificate() {
        return certificate.getRawData();
    }

    public X509Certificate getX509Certificate() throws Exception {
        return certificate.getX509Certificate();
    }

    public PublicKey getPublicKey() throws Exception {
        return cryptoToken.getPublicKey(certificate.getAlias());
    }

    public Certificate getCertificate() {
        return certificate;
    }
}
