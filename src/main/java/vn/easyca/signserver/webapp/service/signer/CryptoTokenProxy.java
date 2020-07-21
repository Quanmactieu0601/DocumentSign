package vn.easyca.signserver.webapp.service.signer;

import vn.easyca.signserver.core.cryptotoken.CryptoToken;
import vn.easyca.signserver.webapp.domain.Certificate;

import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Date;

/**
 * instead for token domain.
 * help connecting to core. manage token's info.
 */

public class CryptoTokenProxy {


    private final Certificate certificateDomain;

    private final PrivateKey privateKey;

    private final PublicKey publicKey;

    private final CryptoToken cryptoToken;

    private X509Certificate x509Certificate;


    public CryptoTokenProxy(CryptoToken cryptoToken, Certificate certificateDomain) throws Exception {

        this.certificateDomain = certificateDomain;
        this.cryptoToken = cryptoToken;
        this.privateKey = cryptoToken.getPrivateKey(certificateDomain.getAlias());
        this.publicKey = cryptoToken.getPublicKey(certificateDomain.getAlias());
    }


    public java.security.cert.Certificate[] getX509Certificates() throws KeyStoreException {
        java.security.cert.Certificate[] certificates = new java.security.cert.Certificate[1];
        certificates[0] = cryptoToken.getCertificate(certificateDomain.getAlias());
        this.x509Certificate = (X509Certificate) certificates[0];
        return certificates;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public boolean hasEfficiency(Date usedDate) {
        usedDate = usedDate == null ? new Date() : usedDate;
        try {
            this.x509Certificate.checkValidity(usedDate);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    public String getBase64Certificate() throws KeyStoreException, CertificateEncodingException {

        return Base64.getEncoder().encodeToString(getX509Certificates()[0].getEncoded());
    }

    public PublicKey getPublicKey() {

        return publicKey;
    }

    public Certificate getCertificateDomain() {
        return certificateDomain;
    }
}
