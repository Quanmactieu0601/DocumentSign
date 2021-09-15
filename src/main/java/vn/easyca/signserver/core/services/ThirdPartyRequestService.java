package vn.easyca.signserver.core.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import vn.easyca.signserver.core.domain.CertificateDTO;
import vn.easyca.signserver.core.dto.CertificateGenerateDTO;
import vn.easyca.signserver.core.dto.OptionalDTO;
import vn.easyca.signserver.core.dto.sign.TokenInfoDTO;
import vn.easyca.signserver.core.dto.sign.newrequest.SigningContainerRequest;
import vn.easyca.signserver.core.dto.sign.newrequest.SigningRequest;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.webapp.service.CertPackageService;
import vn.easyca.signserver.webapp.service.CertificateService;
import vn.easyca.signserver.webapp.service.dto.CertPackageDTO;
import vn.easyca.signserver.webapp.service.impl.request.*;
import vn.easyca.signserver.webapp.utils.CommonUtils;
import vn.easyca.signserver.webapp.web.rest.vm.response.BaseResponseVM;
import vn.easyca.signserver.webapp.web.rest.vm.response.P12CertificateRegisterResult;
import vn.easyca.signserver.webapp.web.rest.vm.response.SigningResult;

import java.lang.reflect.Field;
import java.util.*;

@Service
public class ThirdPartyRequestService {
    private final SigningService signingService;
    private final CertificateGenerateService certificateGenerateService;
    private final CertificateService certificateService;
    private final CertPackageService certPackageService;
    private final PDFSigningRequest pdfSigningRequest;
    private final RawSigningRequest rawSigningRequest;
    private final HashSigningRequest hashSigningRequest;
    private final OfficeSigningRequest officeSigningRequest;
    private final XmlSigningRequest xmlSigningRequest;

    public ThirdPartyRequestService(SigningService signingService, CertificateGenerateService certificateGenerateService, CertificateService certificateService, CertPackageService certPackageService, OfficeSigningService officeSigningService, XMLSigningService xmlSigningService, PDFSigningService pdfSigningService, PDFSigningRequest pdfSigningRequest, RawSigningRequest rawSigningRequest, HashSigningRequest hashRequestSigning, HashSigningRequest hashSigningRequest, OfficeSigningRequest officeSigningRequest, XmlSigningRequest xmlSigningRequest) {
        this.signingService = signingService;
        this.certificateGenerateService = certificateGenerateService;
        this.certificateService = certificateService;
        this.certPackageService = certPackageService;
        this.pdfSigningRequest = pdfSigningRequest;
        this.rawSigningRequest = rawSigningRequest;
        this.hashSigningRequest = hashSigningRequest;
        this.officeSigningRequest = officeSigningRequest;
        this.xmlSigningRequest = xmlSigningRequest;
    }

    public List<P12CertificateRegisterResult> registerCertificate(List<CertificateGenerateDTO> certificateGenerateDTO) throws ApplicationException {
        List<P12CertificateRegisterResult> registerResultDtoList = certificateGenerateService.genCertificates(certificateGenerateDTO);
        return registerResultDtoList;
    }

    public Object sign(SigningRequest<SigningContainerRequest<Object, String>> signingRequest) throws Exception {
        TokenInfoDTO tokenInfo = signingRequest.getTokenInfo();
        OptionalDTO optional = signingRequest.getOptional();
        List<Object> result = new ArrayList<>();

        if (tokenInfo == null) {
            throw new ApplicationException("Thiếu tokenInfo");
        }
        CertificateDTO certificateDTO = certificateService.getBySerial(tokenInfo.getSerial());

        if (certificateDTO == null) {
            throw new ApplicationException("Không tìm thấy chứng thư số");
        }

        if (certificateDTO.getPackageId() == null) {
            throw new ApplicationException("Chứng thư số chưa đăng ký gói dịch vụ");
        }

        Date signDate = new Date();
        if (CommonUtils.isExpired(certificateDTO.getX509Certificate(), signDate)) {
            throw new ApplicationException("Hạn sử dụng của chứng thư số không hợp lệ");
        }

        Optional<CertPackageDTO> certPackage = certPackageService.findOne(certificateDTO.getPackageId());
        Integer signedTurn = certificateDTO.getSignedTurnCount();
        Integer signingTurnOfPackage = certPackage.get().getSigningTurn();
        Integer signedCurrentCount = 0;

        List<Object> listResultSigningResponse = new ArrayList<>();
        Integer index = 1;
        for (SigningContainerRequest request : signingRequest.getSigningRequestContents()) {
            Object requestValue = request.getRequest();
            Object signingResult = "";

            try {

                if (signedTurn + signedCurrentCount >= signingTurnOfPackage) {
                    throw new ApplicationException("Đã ký quá số lượt.");
                }

                switch (request.getType().toString()) {
                    case "pdf": {
                        signingResult = pdfSigningRequest.sign(requestValue, tokenInfo, optional, request.getKey());
                        break;
                    }
                    case "raw": {
                        signingResult = rawSigningRequest.sign(requestValue, tokenInfo, optional, request.getKey());
                        break;
                    }
                    case "hash": {
                        signingResult = hashSigningRequest.sign(requestValue, tokenInfo, optional, request.getKey());
                        break;
                    }
                    case "office": {
                        signingResult = officeSigningRequest.sign(requestValue, tokenInfo, optional, request.getKey());
                        break;
                    }
                    case "xml": {
                        signingResult = xmlSigningRequest.sign(requestValue, tokenInfo, optional, request.getKey());
                        break;
                    }
                }
                signedCurrentCount++;
                listResultSigningResponse.add(signingResult);
            } catch (Exception e) {
                listResultSigningResponse.add(new SigningResult("", request.getKey(), "Ký tệp lỗi " + e.getMessage(), -1));
            }
            index++;
        }

        // update sign turn
        if (signedCurrentCount > 0) {
            certificateService.updateSignTurn(certificateDTO.getId(), signedCurrentCount);
        }
        return listResultSigningResponse;
    }


}
