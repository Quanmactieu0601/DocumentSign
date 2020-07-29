package vn.easyca.signserver.webapp.persistence;

import vn.easyca.signserver.webapp.domain.CertificateEntity;
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

    private TokenInfo decryptionTokenInfo(TokenInfo tokenInfo) {
        String data = tokenInfo.getData();
        try {
            tokenInfo.setData(data);
            return tokenInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public CertificateEntity encryptCert(CertificateEntity certificate) {
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
