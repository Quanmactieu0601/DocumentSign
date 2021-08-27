package vn.easyca.signserver.core.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.nashorn.internal.parser.Token;
import org.springframework.stereotype.Service;
import vn.easyca.signserver.core.domain.CertificateDTO;
import vn.easyca.signserver.core.dto.CertificateGenerateDTO;
import vn.easyca.signserver.core.dto.OptionalDTO;
import vn.easyca.signserver.core.dto.sign.TokenInfoDTO;
import vn.easyca.signserver.core.dto.sign.newrequest.SigningContainerRequest;
import vn.easyca.signserver.core.dto.sign.newrequest.SigningRequest;
import vn.easyca.signserver.core.dto.sign.newrequest.SigningRequestContent;
import vn.easyca.signserver.core.dto.sign.newrequest.VisibleRequestContent;
import vn.easyca.signserver.core.dto.sign.newresponse.SigningResponse;
import vn.easyca.signserver.core.dto.sign.request.SignElement;
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
import vn.easyca.signserver.webapp.web.rest.vm.request.sign.SignElementVM;
import vn.easyca.signserver.webapp.web.rest.vm.request.sign.SigningVM;
import vn.easyca.signserver.webapp.web.rest.vm.request.sign.TokenVM;
import vn.easyca.signserver.webapp.web.rest.vm.response.BaseResponseVM;

import java.util.*;

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

    public Object sign(SigningRequest<SigningContainerRequest<Object, String>> signingRequest) throws Exception {
        List<Object> signingResults = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        TokenInfoDTO tokenInfo = signingRequest.getTokenInfo();
        OptionalDTO optional = signingRequest.getOptional();
        List<Object> result = new ArrayList<>();
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
            switch (request.getType().toString()) {
                case "pdf": {
                    try {
                        checkSignTurn(signedTurn + signedCurrentCount, signingTurnOfPackage);
                        VisibleRequestContent visibleRequestContent = mapper.convertValue(requestValue, VisibleRequestContent.class);
                        List<VisibleRequestContent> lstTemp = new ArrayList<>();
                        lstTemp.add(visibleRequestContent);
                        SigningRequest signingPdfRequest = new SigningRequest();
                        signingPdfRequest.setSigningRequestContents(lstTemp);
                        signingPdfRequest.setOptional(optional);
                        signingPdfRequest.setTokenInfo(tokenInfo);
                        PDFSigningDataRes signResponse = signingService.signPDFFile(signingPdfRequest);
                        String resource = Base64.getEncoder().encodeToString(signResponse.getContent());
                        signedCurrentCount++;
                        listResultSigningResponse.add(new BaseResponseVM(BaseResponseVM.STATUS_OK, resource, "Ký tệp pdf thành công"));
                        break;
                    } catch (Exception e) {
                        String fileName = " Thứ tự " + index;
                        listResultSigningResponse.add(new BaseResponseVM(-1, "Tệp - " + fileName + " ký lỗi", e.getMessage()));
                        break;
                    }
                }
                case "raw": {
                    try {
                        checkSignTurn(signedTurn + signedCurrentCount, signingTurnOfPackage);
                        SigningVM<String> signingVM = mapper.convertValue(requestValue, SigningVM.class);
                        TokenVM tokenVM = new TokenVM();
                        tokenVM.setSerial(tokenInfo.getSerial());
                        tokenVM.setPin(tokenInfo.getPin());
                        signingVM.setTokenInfo(tokenVM);
                        signingVM.setOptional(optional);
                        SignRequest<String> signingRawRequest = signingVM.getDTO(String.class);
                        SignDataResponse<List<SignResultElement>> signResponse = signingService.signRaw(signingRawRequest);
                        signedCurrentCount++;
                        listResultSigningResponse.add(BaseResponseVM.createNewSuccessResponse(signResponse));
                        break;
                    }catch (Exception e) {
                        String fileName = "Thứ tự thứ " + index;
                        listResultSigningResponse.add(new BaseResponseVM(-1, "Tệp - " + fileName + " ký lỗi", e.getMessage()));
                        break;
                    }
                }
                case "hash": {
                    try{
                        checkSignTurn(signedTurn + signedCurrentCount, signingTurnOfPackage);
                        SigningVM<String> signingVM = mapper.convertValue(requestValue, SigningVM.class);
                        TokenVM tokenVM = new TokenVM();
                        tokenVM.setSerial(tokenInfo.getSerial());
                        tokenVM.setPin(tokenInfo.getPin());
                        signingVM.setTokenInfo(tokenVM);
                        signingVM.setOptional(optional);
                        SignRequest<String> signHashRequest = signingVM.getDTO(String.class);
                        Object res = signingService.signHash(signHashRequest, false);
                        signedCurrentCount++;
                        listResultSigningResponse.add(BaseResponseVM.createNewSuccessResponse(res));
                        break;
                    } catch (Exception e) {
                        String fileName = "Thứ tự thứ " + index;
                        listResultSigningResponse.add(new BaseResponseVM(-1, "Tệp - " + fileName + " ký lỗi", e.getMessage()));
                        break;
                    }
                }
                case "office": {
                    try {
                        checkSignTurn(signedTurn + signedCurrentCount, signingTurnOfPackage);
                        SigningRequestContent signingRequestConvert = mapper.convertValue(requestValue, SigningRequestContent.class);
                        List<SigningRequestContent> lstTemp = new ArrayList<>();
                        lstTemp.add(signingRequestConvert);
                        SigningRequest signingRequestOffice = new SigningRequest();
                        signingRequestOffice.setSigningRequestContents(lstTemp);
                        signingRequestOffice.setTokenInfo(tokenInfo);
                        signingRequestOffice.setOptional(optional);
                        SigningResponse signingDataResponse = officeSigningService.sign(signingRequestOffice);
                        BaseResponseVM.createNewSuccessResponse(signingDataResponse);
                        signedCurrentCount++;
                        listResultSigningResponse.add(signingDataResponse);
                        break;
                    } catch (Exception e){
                        String fileName = "Thứ tự thứ " + index;
                        listResultSigningResponse.add(new BaseResponseVM(-1, "Tệp - " + fileName + " ký lỗi", e.getMessage()));
                        break;
                    }

                }
                case "xml": {
                    try {
                        checkSignTurn(signedTurn + signedCurrentCount, signingTurnOfPackage);
                        SigningRequestContent signingRequestContent = mapper.convertValue(requestValue, SigningRequestContent.class);
                        List<SigningRequestContent> lstTemp = new ArrayList<>();
                        lstTemp.add(signingRequestContent);
                        SigningRequest signingRequestXml = new SigningRequest();
                        signingRequestXml.setSigningRequestContents(lstTemp);
                        signingRequestXml.setOptional(optional);
                        signingRequestXml.setTokenInfo(tokenInfo);
                        SigningResponse signingDataResponse = xmlSigningService.sign(signingRequestXml);
                        signedCurrentCount++;
                        listResultSigningResponse.add(BaseResponseVM.createNewSuccessResponse(signingDataResponse));
                        break;
                    } catch (Exception e) {
                        String fileName = "Thứ tự thứ " + index;
                        listResultSigningResponse.add(new BaseResponseVM(-1, "Tệp - " + fileName + " ký lỗi", e.getMessage()));
                        break;
                    }
                }
            }
            index++;
        }

        // update sign turn
        if (signedCurrentCount > 0) {
            certificateService.updateSignTurn(certificateDTO.getId(), signedCurrentCount);
        }

        return listResultSigningResponse;
    }


    private boolean checkSignTurn(Integer signedTurn, Integer signingTurnOfPackage) throws ApplicationException {
        if (signedTurn >= signingTurnOfPackage) {
            throw new ApplicationException("Đã ký quá số lượt.");
        }
        return true;
    }

}
