package vn.easyca.signserver.core.services;

import org.springframework.stereotype.Service;
import vn.easyca.signserver.core.dto.CertificateGenerateDTO;

import java.util.List;

@Service
public class ThirdPartyRequestService {
    private final SigningService signingService;
    private final CertificateGenerateService certificateGenerateService;

    public ThirdPartyRequestService(SigningService signingService, CertificateGenerateService certificateGenerateService) {
        this.signingService = signingService;
        this.certificateGenerateService = certificateGenerateService;
    }

    public void registerCertificate(List<CertificateGenerateDTO> certificateGenerateDTO) {


    }

    public void sign() {

    }

    public void registerCertAndSign() {

    }


}
