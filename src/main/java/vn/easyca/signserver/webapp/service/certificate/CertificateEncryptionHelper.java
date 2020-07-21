package vn.easyca.signserver.webapp.service.certificate;

import vn.easyca.signserver.webapp.domain.Certificate;
import vn.easyca.signserver.webapp.domain.TokenInfo;
import vn.easyca.signserver.webapp.utils.AESCBCEncryptor;

import java.util.Optional;

public class CertificateEncryptionHelper {
    public Optional<Certificate> decryptCert(Optional<Certificate> optionalCertificate) {
        if (optionalCertificate.isPresent()) {
            Certificate certificate = optionalCertificate.get();
            certificate = decryptCert(certificate);
            return Optional.of(certificate);
        }
        return optionalCertificate;
    }

    private Certificate decryptCert(Certificate certificate) {
        if (certificate.getCertificateTokenInfo() != null && certificate.getCertificateTokenInfo().getData() != null) {
            TokenInfo tokenInfo = decryptionTokenInfo(certificate.getCertificateTokenInfo());
            certificate.setCertificateTokenInfo(tokenInfo);
            return certificate;
        }
        return certificate;
    }

    private TokenInfo decryptionTokenInfo(TokenInfo tokenInfo) {
        String data = tokenInfo.getData();
        try {
            data = AESCBCEncryptor.decrypt(data);
            tokenInfo.setData(data);
            return tokenInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Certificate encryptCert(Certificate certificate) {
        if (certificate.getCertificateTokenInfo() != null && certificate.getCertificateTokenInfo().getData() != null) {
            TokenInfo tokenInfo = encryptionTokenInfo(certificate.getCertificateTokenInfo());
            certificate.setCertificateTokenInfo(tokenInfo);
            return certificate;
        }
        return certificate;
    }

    private TokenInfo encryptionTokenInfo(TokenInfo tokenInfo) {
        String data = tokenInfo.getData();
        try {
            data = AESCBCEncryptor.encrypt(data);
            tokenInfo.setData(data);
            return tokenInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
