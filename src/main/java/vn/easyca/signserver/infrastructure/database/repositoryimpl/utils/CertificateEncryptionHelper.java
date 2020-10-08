package vn.easyca.signserver.infrastructure.database.repositoryimpl.utils;

import vn.easyca.signserver.application.domain.Certificate;
import vn.easyca.signserver.webapp.utils.AESCBCEncryptor;

public class CertificateEncryptionHelper {

    public Certificate decryptCert(Certificate certificate) {
        try {
            if (certificate.getTokenInfo() != null && certificate.getTokenInfo().getData() != null) {
                String plaintData = AESCBCEncryptor.decrypt(certificate.getTokenInfo().getData());
                certificate.getTokenInfo().setData(plaintData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return certificate;
    }

    public Certificate encryptCert(Certificate certificate) {
        String plaintData = certificate.getTokenInfo() != null ? certificate.getTokenInfo().getData() : null;
        if (plaintData != null){
            String encryptionData = null;
            try {
                encryptionData = AESCBCEncryptor.encrypt(plaintData);
                certificate.getTokenInfo().setData(encryptionData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return certificate;
    }
}
