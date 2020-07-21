package vn.easyca.signserver.webapp.service.certificate;

import vn.easyca.signserver.webapp.commond.encryption.Encryption;
import vn.easyca.signserver.webapp.domain.Certificate;
import vn.easyca.signserver.webapp.domain.TokenInfo;

import java.util.Optional;

public class EncryptionHelper {

    private final Encryption encryption;
    public EncryptionHelper(Encryption encryption) {
        this.encryption = encryption;
    }

    protected Optional<Certificate> decryptCert(Optional<Certificate> optionalCertificate)  {
        if (optionalCertificate.isPresent()) {
            Certificate certificate = optionalCertificate.get();
            certificate = decryptCert(certificate);
            return Optional.of(certificate);
        }
        return optionalCertificate;
    }


    protected Certificate decryptCert(Certificate certificate) {
        if (encryption != null &&
            certificate.getCertificateTokenInfo() != null &&
            certificate.getCertificateTokenInfo().getData() != null) {
            TokenInfo tokenInfo= decryptionTokenInfo(certificate.getCertificateTokenInfo());
            certificate.setCertificateTokenInfo(tokenInfo);
            return certificate;
        }
        return certificate;
    }


    protected TokenInfo decryptionTokenInfo(TokenInfo tokenInfo) {
        String data = tokenInfo.getData();
        if (encryption != null) {
            try {
                data = encryption.decrypt(data);
            } catch (Encryption.EncryptionException e) {
                e.printStackTrace();
            }
        }
        tokenInfo.setData(data);
        tokenInfo.setEncrypted(false);
        return tokenInfo;
    }

    protected Optional<Certificate> encryptCert(Optional<Certificate> optionalCertificate)  {
        if (optionalCertificate.isPresent()) {
            Certificate certificate = optionalCertificate.get();
            certificate = encryptCert(certificate);
            return Optional.of(certificate);
        }
        return optionalCertificate;
    }


    protected Certificate encryptCert(Certificate certificate) {
        if (encryption != null &&
            certificate.getCertificateTokenInfo() != null &&
            certificate.getCertificateTokenInfo().getData() != null) {
            TokenInfo tokenInfo= encryptionTokenInfo(certificate.getCertificateTokenInfo());
            certificate.setCertificateTokenInfo(tokenInfo);
            return certificate;
        }
        return certificate;
    }


    protected TokenInfo encryptionTokenInfo(TokenInfo tokenInfo)  {
        String data = tokenInfo.getData();
        if (encryption != null) {
            try {
                data = encryption.decrypt(data);
            } catch (Encryption.EncryptionException e) {
                e.printStackTrace();
            }
        }
        tokenInfo.setData(data);
        tokenInfo.setEncrypted(true);
        return tokenInfo;
    }
}
