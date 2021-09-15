package vn.easyca.signserver.webapp.service.impl.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.easyca.signserver.core.domain.CertificateDTO;
import vn.easyca.signserver.core.dto.OptionalDTO;
import vn.easyca.signserver.core.dto.sign.TokenInfoDTO;
import vn.easyca.signserver.core.dto.sign.newrequest.SigningRequest;
import vn.easyca.signserver.core.dto.sign.newrequest.VisibleRequestContent;
import vn.easyca.signserver.core.dto.sign.response.PDFSigningDataRes;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.core.services.SigningService;
import vn.easyca.signserver.webapp.service.CertPackageService;
import vn.easyca.signserver.webapp.service.CertificateService;
import vn.easyca.signserver.webapp.service.SigningWrapRequestHandle;
import vn.easyca.signserver.webapp.service.dto.CertPackageDTO;
import vn.easyca.signserver.webapp.utils.CommonUtils;
import vn.easyca.signserver.webapp.web.rest.vm.response.SigningResult;

import java.util.*;

@Service
@Transactional
public class PDFSigningRequest implements SigningWrapRequestHandle {
    private final int RESULT_OK = 0;
    private final int RESULT_ERROR = 1;

    private final CertificateService certificateService;
    private final CertPackageService certPackageService;
    private final SigningService signingService;
    public PDFSigningRequest(CertificateService certificateService, CertPackageService certPackageService, SigningService signingService) {
        this.certificateService = certificateService;
        this.certPackageService = certPackageService;
        this.signingService = signingService;
    }

    @Override
    public SigningResult sign(Object requestValue, TokenInfoDTO tokenInfo, OptionalDTO optional, String key) throws ApplicationException {
        VisibleRequestContent visibleRequestContent = mapper.convertValue(requestValue, VisibleRequestContent.class);
        List<VisibleRequestContent> lstTemp = new ArrayList<>();
        lstTemp.add(visibleRequestContent);
        SigningRequest signingPdfRequest = new SigningRequest();
        signingPdfRequest.setSigningRequestContents(lstTemp);
        signingPdfRequest.setOptional(optional);
        signingPdfRequest.setTokenInfo(tokenInfo);
        PDFSigningDataRes signResponse = signingService.signPDFFile(signingPdfRequest);
        String resource = Base64.getEncoder().encodeToString(signResponse.getContent());

        SigningResult result = new SigningResult(resource, key, "Ký thành công", RESULT_OK);
        return result;
    }
}
