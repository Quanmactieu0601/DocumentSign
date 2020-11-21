package vn.easyca.signserver.webapp.utils;

import vn.easyca.signserver.core.domain.CertificateDTO;

public class CertificateEncryptionHelper {

    public CertificateDTO decryptCert(CertificateDTO certificateDTO) {
        try {
            if (certificateDTO.getTokenInfo() != null && certificateDTO.getTokenInfo().getData() != null) {
                String plaintData = AESCBCEncryptor.decrypt(certificateDTO.getTokenInfo().getData());
                certificateDTO.getTokenInfo().setData(plaintData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return certificateDTO;
    }

    public CertificateDTO encryptCert(CertificateDTO certificateDTO) {
        String plaintData = certificateDTO.getTokenInfo() != null ? certificateDTO.getTokenInfo().getData() : null;
        if (plaintData != null){
            String encryptionData = null;
            try {
                encryptionData = AESCBCEncryptor.encrypt(plaintData);
                certificateDTO.getTokenInfo().setData(encryptionData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return certificateDTO;
    }
}
