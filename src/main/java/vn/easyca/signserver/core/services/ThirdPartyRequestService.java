package vn.easyca.signserver.core.services;

import org.springframework.stereotype.Service;
import vn.easyca.signserver.core.domain.CertificateDTO;
import vn.easyca.signserver.core.dto.CertificateGenerateDTO;
import vn.easyca.signserver.core.dto.sign.newrequest.SigningRequest;
import vn.easyca.signserver.core.dto.sign.newrequest.SigningRequestContent;
import vn.easyca.signserver.core.dto.sign.newresponse.SigningResponse;
import vn.easyca.signserver.core.dto.sign.request.SignRequest;
import vn.easyca.signserver.core.dto.sign.response.PDFSigningDataRes;
import vn.easyca.signserver.core.dto.sign.response.SignDataResponse;
import vn.easyca.signserver.core.dto.sign.response.SignResultElement;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.ra.lib.dto.RegisterResultDto;
import vn.easyca.signserver.webapp.service.CertPackageService;
import vn.easyca.signserver.webapp.service.CertificateService;
import vn.easyca.signserver.webapp.service.dto.CertPackageDTO;
import vn.easyca.signserver.webapp.utils.CommonUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ThirdPartyRequestService {
    private final SigningService signingService;
    private final CertificateGenerateService certificateGenerateService;
    private final CertificateService certificateService;
    private final CertPackageService certPackageService;
    private final OfficeSigningService officeSigningService;
    private final XMLSigningService xmlSigningService;
    private final PDFSigningService pdfSigningService;

    public ThirdPartyRequestService(SigningService signingService, CertificateGenerateService certificateGenerateService, CertificateService certificateService, CertPackageService certPackageService, OfficeSigningService officeSigningService, XMLSigningService xmlSigningService, PDFSigningService pdfSigningService) {
        this.signingService = signingService;
        this.certificateGenerateService = certificateGenerateService;
        this.certificateService = certificateService;
        this.certPackageService = certPackageService;
        this.officeSigningService = officeSigningService;
        this.xmlSigningService = xmlSigningService;
        this.pdfSigningService = pdfSigningService;
    }

    public void registerCertificate(List<CertificateGenerateDTO> certificateGenerateDTO) throws ApplicationException {
        List<RegisterResultDto> registerResultDtoList = certificateGenerateService.genCertificates(certificateGenerateDTO);
        for (int i = 0; i <= registerResultDtoList.size(); i++) {
            String serial = registerResultDtoList.get(0).getCertSerial();
        }
    }

    public PDFSigningDataRes signPdf(SigningRequest request) throws ApplicationException {
        Long id = validate(request.getTokenInfo().getSerial());
        PDFSigningDataRes res = signingService.signPDFFile(request);
        certificateService.updateSignedTurn(id);
        return res;
    }


    public SignDataResponse<List<SignResultElement>> signRaw(SignRequest<String> request) throws ApplicationException {
        Long id = validate(request.getTokenInfoDTO().getSerial());
        SignDataResponse<List<SignResultElement>> res = signingService.signRaw(request);
        certificateService.updateSignedTurn(id);
        return res;
    }

    public SignDataResponse<List<SignResultElement>> signHash(SignRequest<String> request, boolean withDigestInfo) throws ApplicationException {
        Long id = validate(request.getTokenInfoDTO().getSerial());
        SignDataResponse<List<SignResultElement>> res = signingService.signHash(request, withDigestInfo);
        certificateService.updateSignedTurn(id);
        return res;
    }


    public SigningResponse signOffice(SigningRequest<SigningRequestContent> signingRequest) throws Exception {
        Long id = validate(signingRequest.getTokenInfo().getSerial());
        SigningResponse res = officeSigningService.sign(signingRequest);
        certificateService.updateSignedTurn(id);
        return res;
    }

    public SigningResponse signXml(SigningRequest<SigningRequestContent> signingRequest) throws ApplicationException {
        Long id = validate(signingRequest.getTokenInfo().getSerial());
        SigningResponse res = xmlSigningService.sign(signingRequest);
        certificateService.updateSignedTurn(id);
        return res;
    }

    public SigningResponse invisibleSignInvisiblePdf(SigningRequest<SigningRequestContent> signingRequest) throws Exception {
        Long id = validate(signingRequest.getTokenInfo().getSerial());
        SigningResponse res = pdfSigningService.invisibleSign(signingRequest);
        certificateService.updateSignedTurn(id);
        return res;
    }


    private Long validate(String serial) throws ApplicationException {
        CertificateDTO certificateDTO = certificateService.getBySerial(serial);

        if (certificateDTO == null) {
            throw new ApplicationException("Không tìm thấy chứng thư số");
        }

        if (certificateDTO.getPackageId() == null) {
            throw new ApplicationException("Chứng thư số chưa đăng ký gói dịch vụ");
        }

        Optional<CertPackageDTO> certPackage = certPackageService.findOne(certificateDTO.getPackageId());
        Integer signedTurn = certificateDTO.getSignedTurnCount();
        Integer signingTurnOfPackage = certPackage.get().getSigningTurn();
        if (signedTurn >= signingTurnOfPackage) {
            throw new ApplicationException("Đã ký quá số lượt.");
        }

        Date signDate = new Date();
        if (CommonUtils.isExpired(certificateDTO.getX509Certificate(), signDate)) {
            throw new ApplicationException("Hạn sử dụng của chứng thư số không hợp lệ");
        }
        return certificateDTO.getId();
    }

}
