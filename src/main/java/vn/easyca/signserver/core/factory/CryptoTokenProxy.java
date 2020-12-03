package vn.easyca.signserver.core.factory;

import vn.easyca.signserver.core.domain.CertificateDTO;
import vn.easyca.signserver.pki.cryptotoken.CryptoToken;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

import vn.easyca.signserver.pki.cryptotoken.error.*;

public class CryptoTokenProxy {

    private final CertificateDTO certificateDTO;

    private final CryptoToken cryptoToken;

    public CryptoTokenProxy(CryptoToken cryptoToken, CertificateDTO certificateDTO) {
        this.certificateDTO = certificateDTO;
        this.cryptoToken = cryptoToken;
    }

    public PrivateKey getPrivateKey() throws CryptoTokenException {
        return cryptoToken.getPrivateKey(certificateDTO.getAlias());
    }

    public boolean isExpired(Date date) throws CertificateException {
        X509Certificate cert = null;
        cert = certificateDTO.getX509Certificate();
        Date notAfter = cert.getNotAfter();
        Date notBefore = cert.getNotBefore();
        if (date == null) date = new Date();
        return notAfter.before(date) || notBefore.after(date);
    }

    public String getBase64Certificate() {
        return certificateDTO.getRawData();
    }

    public X509Certificate getX509Certificate() throws CertificateException {
        return certificateDTO.getX509Certificate();
    }

    public PublicKey getPublicKey() throws CryptoTokenException {
        return cryptoToken.getPublicKey(certificateDTO.getAlias());
    }

    public CertificateDTO getCertificateDTO() {
        return certificateDTO;
    }

    public CryptoToken getCryptoToken() {
        return cryptoToken;
    }

    public String getProviderName() throws CryptoTokenException {
        return cryptoToken.getProviderName();
    }
}
