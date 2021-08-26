package vn.easyca.signserver.core.services;

import org.springframework.stereotype.Service;
import vn.easyca.signserver.core.dto.CertificateGenerateDTO;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.core.factory.CryptoTokenProxyException;
import vn.easyca.signserver.core.interfaces.CertificateRequester;
import vn.easyca.signserver.pki.cryptotoken.error.CryptoTokenException;
import vn.easyca.signserver.pki.cryptotoken.utils.CSRGenerator;
import vn.easyca.signserver.ra.lib.dto.RegisterResultDto;

import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
public class ThirdPartyRequestService {
    private final SigningService signingService;
    private final CertificateGenerateService certificateGenerateService;

    public ThirdPartyRequestService(SigningService signingService, CertificateGenerateService certificateGenerateService) {
        this.signingService = signingService;
        this.certificateGenerateService = certificateGenerateService;
    }

    public void registerCertificate(List<CertificateGenerateDTO> certificateGenerateDTO) throws ApplicationException {
        List<RegisterResultDto> registerResultDtoList = certificateGenerateService.genCertificates(certificateGenerateDTO);
        for (int i = 0; i <= registerResultDtoList.size(); i++) {
            String serial = registerResultDtoList.get(0).getCertSerial();
        }
    }

    public void sign() {

    }




}
