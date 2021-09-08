package vn.easyca.signserver.core.services;

import org.springframework.stereotype.Service;
import vn.easyca.signserver.core.domain.TokenInfo;
import vn.easyca.signserver.core.dto.CertificateGenerateDTO;
import vn.easyca.signserver.core.dto.sign.TokenInfoDTO;
import vn.easyca.signserver.core.dto.sign.newrequest.ExtraInfo;
import vn.easyca.signserver.core.dto.sign.newrequest.VisibleRequestContent;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.webapp.service.CertPackageService;
import vn.easyca.signserver.webapp.service.CertificateService;
import vn.easyca.signserver.webapp.service.impl.request.*;
import vn.easyca.signserver.webapp.web.rest.mapper.CertificateGeneratorVMMapper;
import vn.easyca.signserver.webapp.web.rest.vm.request.sign.QuickSignVM;
import vn.easyca.signserver.webapp.web.rest.vm.request.sign.TokenVM;
import vn.easyca.signserver.webapp.web.rest.vm.response.P12CertificateRegisterResult;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuickSigningWrapService extends ThirdPartyRequestService {

    private final int RESULT_OK = 0;
    private final int RESULT_ERROR = 1;

    public QuickSigningWrapService(SigningService signingService, CertificateGenerateService certificateGenerateService, CertificateService certificateService, CertPackageService certPackageService, OfficeSigningService officeSigningService, XMLSigningService xmlSigningService, PDFSigningService pdfSigningService, PDFSigningRequest pdfSigningRequest, RawSigningRequest rawSigningRequest, HashSigningRequest hashRequestSigning, HashSigningRequest hashSigningRequest, OfficeSigningRequest officeSigningRequest, XmlSigningRequest xmlSigningRequest) {
        super(signingService, certificateGenerateService, certificateService, certPackageService, officeSigningService, xmlSigningService, pdfSigningService, pdfSigningRequest, rawSigningRequest, hashRequestSigning, hashSigningRequest, officeSigningRequest, xmlSigningRequest);
    }

    public Object quickSign(QuickSignVM quickSignVM) throws ApplicationException {

        // Register
        CertificateGeneratorVMMapper mapper = new CertificateGeneratorVMMapper();
        CertificateGenerateDTO certificateGenerateDTO = mapper.map(quickSignVM);
        List<CertificateGenerateDTO> certificateGenerateList = new ArrayList<>();
        certificateGenerateList.add(certificateGenerateDTO);
        List<P12CertificateRegisterResult> p12CertificateRegisterResultList = this.registerCertificate(certificateGenerateList);

        Object resultResponse = null;
        // Sign
        if (p12CertificateRegisterResultList.get(0).getStatus() == RESULT_OK ) {
            String serial = p12CertificateRegisterResultList.get(0).getSerial();
            String pin = p12CertificateRegisterResultList.get(0).getPin();

            TokenInfoDTO tokenInfo = new TokenInfoDTO();
            tokenInfo.setSerial(serial);
            tokenInfo.setPin(pin);
            quickSignVM.getSigningElement().setTokenInfo(tokenInfo);

            try {
                resultResponse = this.sign(quickSignVM.getSigningElement());
            } catch (Exception ex) {
                throw new ApplicationException("Lỗi khi ký : " + ex.getMessage());
            }
        } else {
            resultResponse = p12CertificateRegisterResultList.get(0);
        }

        return resultResponse;
    }
}
