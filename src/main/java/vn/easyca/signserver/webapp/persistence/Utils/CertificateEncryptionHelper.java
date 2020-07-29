package vn.easyca.signserver.webapp.persistence.Utils;

import vn.easyca.signserver.webapp.persistence.entity.CertificateEntity;
import vn.easyca.signserver.core.domain.TokenInfo;
import vn.easyca.signserver.webapp.utils.AESCBCEncryptor;

public class CertificateEncryptionHelper {


    public CertificateEntity decryptCert(CertificateEntity certificate) {
        String plaintTokenInfo = null;
        try {
            plaintTokenInfo = AESCBCEncryptor.decrypt(certificate.getTokenInfo());
        } catch (Exception e) {
            e.printStackTrace();
        }
        certificate.setTokenInfo(plaintTokenInfo);
        return certificate;
    }
    public CertificateEntity encryptCert(CertificateEntity certificate) {
        String encryptedTokenInfo = null;
        try {
            encryptedTokenInfo = AESCBCEncryptor.encrypt(certificate.getTokenInfo());
        } catch (Exception e) {
            e.printStackTrace();
        }
        certificate.setTokenInfo(encryptedTokenInfo);
        return certificate;
    }
}
