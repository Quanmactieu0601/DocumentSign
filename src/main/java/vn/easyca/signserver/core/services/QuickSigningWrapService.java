package vn.easyca.signserver.core.services;
import vn.easyca.signserver.core.dto.CertificateGenerateDTO;
import vn.easyca.signserver.core.dto.sign.newrequest.ExtraInfo;
import vn.easyca.signserver.core.dto.sign.newrequest.VisibleRequestContent;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.webapp.domain.Certificate;
import vn.easyca.signserver.webapp.service.CertPackageService;
import vn.easyca.signserver.webapp.service.CertificateService;
import vn.easyca.signserver.webapp.web.rest.vm.request.CertificateGeneratorVM;
import vn.easyca.signserver.webapp.web.rest.vm.request.sign.QuickSignVM;

import java.util.ArrayList;
import java.util.List;

public class QuickSigningWrapService extends ThirdPartyRequestService {

    public QuickSigningWrapService(SigningService signingService, CertificateGenerateService certificateGenerateService, CertificateService certificateService, CertPackageService certPackageService, OfficeSigningService officeSigningService, XMLSigningService xmlSigningService, PDFSigningService pdfSigningService) {
        super(signingService, certificateGenerateService, certificateService, certPackageService, officeSigningService, xmlSigningService, pdfSigningService);
    }

    public Object quickSign(QuickSignVM quickSignVM) throws ApplicationException {
        List<CertificateGenerateDTO> registerRequest = new ArrayList<>();
//        Certificate p12Cer = this.registerCertificate(registerRequest).get(0);

        // lưu p12 + gói đăng ký


        // ký số
        if (quickSignVM.getType().equals("pdf")) {
            VisibleRequestContent visibleRequestContent = new VisibleRequestContent();
            visibleRequestContent.setDocumentName("123");
            visibleRequestContent.setImageSignature("");
            visibleRequestContent.setExtraInfo(new ExtraInfo());
            visibleRequestContent.setImageSignature("");
            visibleRequestContent.setData(quickSignVM.getData());
        }
        return null;
    }
}
