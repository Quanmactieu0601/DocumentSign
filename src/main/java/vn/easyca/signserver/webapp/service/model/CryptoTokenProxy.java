package vn.easyca.signserver.webapp.service.model;

import vn.easyca.signserver.core.cryptotoken.CryptoToken;
import vn.easyca.signserver.webapp.domain.Certificate;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Date;

public class CryptoTokenProxy {


    private final Certificate certificateDomain;

    private PrivateKey privateKey;

    private PublicKey publicKey;

    private String pin;

    private CryptoToken cryptoToken;

    private X509Certificate certificate;

    public CryptoTokenProxy(Certificate certificateDomain, String pin) throws Exception {

        this.certificateDomain = certificateDomain;
        this.pin = pin;
        cryptoToken = CryptoTokenFactory.create(certificateDomain, pin);
        privateKey = cryptoToken.getPrivateKey(certificateDomain.getAlias());
        publicKey = cryptoToken.getPublicKey(certificateDomain.getAlias());
    }


    public java.security.cert.Certificate[] getX509Certificates() throws KeyStoreException {
        java.security.cert.Certificate[] certificates = new java.security.cert.Certificate[1];
        certificates[0] = cryptoToken.getCertificate(certificateDomain.getAlias());
        return certificates;
    }

    public PrivateKey getPrivateKey() {

        return privateKey;
    }

    public boolean hasEfficiency(Date usedDate) {


        usedDate = usedDate == null ? new Date() : usedDate;
        try {
            this.certificate.checkValidity(usedDate);
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
