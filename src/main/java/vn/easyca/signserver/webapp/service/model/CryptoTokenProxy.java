package vn.easyca.signserver.webapp.service.model;

import lombok.Getter;
import vn.easyca.signserver.core.cryptotoken.CryptoToken;
import vn.easyca.signserver.webapp.domain.Certificate;

import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import java.io.ByteArrayInputStream;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Date;

public class Signature {


    @Getter
    private final Certificate certificateDomain;

    private X509Certificate x509Certificate;

    private PrivateKey privateKey;

    private String pin;

    private CryptoToken cryptoToken;

    public Signature(Certificate certificateDomain, String pin) throws Exception {


        this.certificateDomain = certificateDomain;
        this.pin = pin;
        cryptoToken = CryptoTokenFactory.create(certificateDomain,pin);
    }


    public java.security.cert.Certificate[] getX509Certificates() throws KeyStoreException {
        java.security.cert.Certificate[] certificates = new java.security.cert.Certificate[1];
        certificates[0] = cryptoToken.getCertificate(certificateDomain.getAlias());
        return certificates;
    }

    public PrivateKey getPrivateKey() throws Exception {

        if (privateKey == null) {
            privateKey = cryptoToken.getPrivateKey(certificateDomain.getAlias());
        }
        return privateKey;
    }

    public String getHashAlgorithm() {
        return "SHA1";
    }

    // business logic
    public CertificateInfo getCertificateInfo(){

        return null;
    }

    public boolean hasEfficiency(Date usedDate){

        usedDate = usedDate == null ? new Date() : usedDate;
        CertificateInfo certificateInfo = getCertificateInfo();
        return usedDate.getTime() >= certificateInfo.getValidFrom().getTime() &&
               usedDate.getTime() <= certificateInfo.getValidTo().getTime();
    }

    public String getBase64Certificate() throws KeyStoreException, CertificateEncodingException {

        return Base64.getEncoder().encodeToString(getX509Certificates()[0].getEncoded());
    }

    public PublicKey getPublicKey() throws Exception {
        return cryptoToken.getPublicKey(certificateDomain.getAlias());
    }
}
