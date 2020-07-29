package vn.easyca.signserver.core.signer;

import vn.easyca.signserver.sign.core.cryptotoken.CryptoToken;
import vn.easyca.signserver.core.domain.Certificate;
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

    private final PrivateKey privateKey;

    private final PublicKey publicKey;

    private final CryptoToken cryptoToken;

    public CryptoTokenProxy(CryptoToken cryptoToken, Certificate certificate) throws Exception {
        this.certificate = certificate;
        this.cryptoToken = cryptoToken;
        this.privateKey = cryptoToken.getPrivateKey(certificate.getAlias());
        this.publicKey = cryptoToken.getPublicKey(certificate.getAlias());
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
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

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public Certificate getCertificate() {
        return certificate;
    }
}
