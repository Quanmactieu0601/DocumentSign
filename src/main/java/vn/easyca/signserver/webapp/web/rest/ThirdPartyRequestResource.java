package vn.easyca.signserver.webapp.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.easyca.signserver.core.dto.CertificateGenerateDTO;
import vn.easyca.signserver.core.dto.sign.newrequest.SigningRequest;
import vn.easyca.signserver.core.dto.sign.newrequest.SigningRequestContent;
import vn.easyca.signserver.core.dto.sign.newrequest.VisibleRequestContent;
import vn.easyca.signserver.core.dto.sign.newresponse.SigningResponse;
import vn.easyca.signserver.core.dto.sign.request.SignRequest;
import vn.easyca.signserver.core.dto.sign.response.PDFSigningDataRes;
import vn.easyca.signserver.core.dto.sign.response.SignDataResponse;
import vn.easyca.signserver.core.dto.sign.response.SignResultElement;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.core.services.ThirdPartyRequestService;
import vn.easyca.signserver.webapp.enm.*;
import vn.easyca.signserver.webapp.security.AuthoritiesConstants;
import vn.easyca.signserver.webapp.service.AsyncTransactionService;
import vn.easyca.signserver.webapp.utils.AccountUtils;
import vn.easyca.signserver.webapp.utils.MappingHelper;
import vn.easyca.signserver.webapp.web.rest.mapper.CertificateGeneratorVMMapper;
import vn.easyca.signserver.webapp.web.rest.vm.request.CertificateGeneratorVM;
import vn.easyca.signserver.webapp.web.rest.vm.request.sign.SigningVM;
import vn.easyca.signserver.webapp.web.rest.vm.response.BaseResponseVM;
import vn.easyca.signserver.webapp.web.rest.vm.response.CertificateGeneratorResultVM;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


@RestController
@RequestMapping("/api/thirdPartyRequest")
public class ThirdPartyRequestResource extends BaseResource {
    private static final Logger log = LoggerFactory.getLogger(ThirdPartyRequestResource.class);

    private final AsyncTransactionService asyncTransactionService;
    private final ThirdPartyRequestService thirdPartyRequestService;
    public ThirdPartyRequestResource(AsyncTransactionService asyncTransactionService, ThirdPartyRequestService thirdPartyRequestService) {
        this.asyncTransactionService = asyncTransactionService;
        this.thirdPartyRequestService = thirdPartyRequestService;
    }

    @PostMapping("/registerCerts")
    @PreAuthorize("hasAnyAuthority(\""+ AuthoritiesConstants.ADMIN+"\", \""+AuthoritiesConstants.SUPER_ADMIN+"\")")
    public ResponseEntity<BaseResponseVM> registerCertificates(@RequestBody List<CertificateGeneratorVM> certificateGenerators) {
        try {
            log.info("--- genCertificate ---");
            CertificateGeneratorVMMapper mapper = new CertificateGeneratorVMMapper();
            List<CertificateGenerateDTO> certificateGenerateDTOList = mapper.map(certificateGenerators);
            List<CertificateGeneratorResultVM> certificateGeneratorResultVM = new ArrayList<>();
            thirdPartyRequestService.registerCertificate(certificateGenerateDTOList);
            Object viewModel = MappingHelper.map(null, certificateGeneratorResultVM.getClass());
            status = TransactionStatus.SUCCESS;
            return ResponseEntity.ok(BaseResponseVM.createNewSuccessResponse(viewModel));
        } catch (ApplicationException applicationException) {
            log.error(applicationException.getMessage(), applicationException);
            message = applicationException.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, applicationException.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            message = e.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        } finally {
            asyncTransactionService.newThread("/api/certificate/gen/p11", TransactionType.BUSINESS, Action.CREATE, Extension.CERT, Method.POST,
                status, message, AccountUtils.getLoggedAccount());
        }
    }


    @PostMapping(value = "/signPdf")
    public ResponseEntity<Object> signPDF(@RequestBody SigningRequest<VisibleRequestContent> signingRequest) {
        log.info(" --- signPDF --- ");
        try {
            PDFSigningDataRes signResponse = thirdPartyRequestService.signPdf(signingRequest);
            String resource = Base64.getEncoder().encodeToString(signResponse.getContent());
            return ResponseEntity.ok(new BaseResponseVM(BaseResponseVM.STATUS_OK, resource, "Ký tệp pdf thành công"));
        } catch (ApplicationException applicationException) {
            log.error(applicationException.getMessage(), applicationException);
            message = applicationException.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, applicationException.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            message = e.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        } finally {
            asyncTransactionService.newThread("/api/sign/pdf", TransactionType.BUSINESS, Action.SIGN, Extension.PDF, Method.POST,
                status, message, AccountUtils.getLoggedAccount());
        }
    }


    @PostMapping(value = "/signHash")
    public ResponseEntity<BaseResponseVM> signHash(@RequestBody SigningVM<String> signingVM) {
        log.info(" --- signHash --- ");
        try {
            SignRequest<String> request = signingVM.getDTO(String.class);
            Object signingDataResponse = thirdPartyRequestService.signHash(request, false);
            status = TransactionStatus.SUCCESS;
            return ResponseEntity.ok(BaseResponseVM.createNewSuccessResponse(signingDataResponse));
        } catch (ApplicationException applicationException) {
            log.error(applicationException.getMessage(), applicationException);
            message = applicationException.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, applicationException.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            message = e.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        } finally {
            asyncTransactionService.newThread("/api/sign/hash", TransactionType.BUSINESS, Action.SIGN, Extension.HASH, Method.POST,
                status, message, AccountUtils.getLoggedAccount());
        }
    }

    @PostMapping(value = "/signRaw")
    public ResponseEntity<BaseResponseVM> signRaw(@RequestBody SigningVM<String> signingVM) {
        log.info(" --- signRaw --- ");
        try {
            SignRequest<String> request = signingVM.getDTO(String.class);
            SignDataResponse<List<SignResultElement>> signResponse = thirdPartyRequestService.signRaw(request);
            status = TransactionStatus.SUCCESS;
            return ResponseEntity.ok(BaseResponseVM.createNewSuccessResponse(signResponse));
        } catch (ApplicationException applicationException) {
            log.error(applicationException.getMessage(), applicationException);
            message = applicationException.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, applicationException.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            message = e.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        } finally {
            asyncTransactionService.newThread("/api/sign/raw", TransactionType.BUSINESS, Action.SIGN, Extension.RAW, Method.POST,
                status, message, AccountUtils.getLoggedAccount());
        }
    }

    @PostMapping(value = "/signOffice")
    public ResponseEntity<BaseResponseVM> signOffice(@RequestBody SigningRequest<SigningRequestContent> signingRequest) {
        log.info(" --- signOffice --- ");
        try {
            SigningResponse signingDataResponse = thirdPartyRequestService.signOffice(signingRequest);
            status = TransactionStatus.SUCCESS;
            return ResponseEntity.ok(BaseResponseVM.createNewSuccessResponse(signingDataResponse));
        } catch (ApplicationException applicationException) {
            log.error(applicationException.getMessage(), applicationException);
            message = applicationException.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, applicationException.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            message = e.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        } finally {
            asyncTransactionService.newThread("/api/sign/office", TransactionType.BUSINESS, Action.SIGN, Extension.OOXML, Method.POST,
                status, message, AccountUtils.getLoggedAccount());
        }
    }

    @PostMapping(value = "/signXml")
    public ResponseEntity<BaseResponseVM> signXML(@RequestBody SigningRequest<SigningRequestContent> signingRequest) {
        log.info(" --- signXML --- ");
        try {
            SigningResponse signingDataResponse = thirdPartyRequestService.signXml(signingRequest);
            status = TransactionStatus.SUCCESS;
            return ResponseEntity.ok(BaseResponseVM.createNewSuccessResponse(signingDataResponse));
        } catch (ApplicationException applicationException) {
            log.error(applicationException.getMessage(), applicationException);
            message = applicationException.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, applicationException.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            message = e.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        } finally {
            asyncTransactionService.newThread("/api/sign/xml", TransactionType.BUSINESS, Action.SIGN, Extension.XML, Method.POST,
                status, message, AccountUtils.getLoggedAccount());
        }
    }

    @PostMapping(value = "/signInvisiblePdf")
    public ResponseEntity<BaseResponseVM> invisibleSignPdf(@RequestBody SigningRequest<SigningRequestContent> signingRequest) {
        log.info(" --- invisiblePdf --- ");
        try {
            SigningResponse signingDataResponse = thirdPartyRequestService.invisibleSignInvisiblePdf(signingRequest);
            status = TransactionStatus.SUCCESS;
            return ResponseEntity.ok(BaseResponseVM.createNewSuccessResponse(signingDataResponse));
        } catch (ApplicationException applicationException) {
            log.error(applicationException.getMessage(), applicationException);
            message = applicationException.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, applicationException.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            message = e.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        } finally {
            asyncTransactionService.newThread("/api/sign/invisiblePdf", TransactionType.BUSINESS, Action.SIGN, Extension.PDF, Method.POST,
                status, message, AccountUtils.getLoggedAccount());
        }
    }



}
