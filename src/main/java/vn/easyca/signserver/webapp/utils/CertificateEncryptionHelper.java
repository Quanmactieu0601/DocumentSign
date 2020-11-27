package vn.easyca.signserver.webapp.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.easyca.signserver.core.domain.CertificateDTO;
import vn.easyca.signserver.core.exception.ApplicationException;

@Component
public class CertificateEncryptionHelper {

    final SymmetricEncryptors symmetricEncryptors;

    public CertificateEncryptionHelper(SymmetricEncryptors symmetricEncryptors) {
        this.symmetricEncryptors = symmetricEncryptors;
    }

    public CertificateDTO decryptCert(CertificateDTO certificateDTO) {
        try {
            if (certificateDTO.getTokenInfo() != null && certificateDTO.getTokenInfo().getData() != null) {
                String plaintData = symmetricEncryptors.decrypt(certificateDTO.getTokenInfo().getData());
                certificateDTO.getTokenInfo().setData(plaintData);
            }
        } catch (ApplicationException e) {
            e.printStackTrace();
        }
        return certificateDTO;
    }

    public CertificateDTO encryptCert(CertificateDTO certificateDTO) {
        String plaintData = certificateDTO.getTokenInfo() != null ? certificateDTO.getTokenInfo().getData() : null;
        if (plaintData != null){
            String encryptionData = null;
            try {
                encryptionData = symmetricEncryptors.encrypt(plaintData);
                certificateDTO.getTokenInfo().setData(encryptionData);
            } catch (ApplicationException e) {
                e.printStackTrace();
            }
        }
        return certificateDTO;
    }
}
