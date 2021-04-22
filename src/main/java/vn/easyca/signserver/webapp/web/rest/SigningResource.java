package vn.easyca.signserver.webapp.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.easyca.signserver.core.dto.sign.newrequest.SigningRequest;
import vn.easyca.signserver.core.dto.sign.newrequest.SigningRequestContent;
import vn.easyca.signserver.core.dto.sign.newrequest.VisibleRequestContent;
import vn.easyca.signserver.core.dto.sign.newresponse.SigningResponse;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.core.services.OfficeSigningService;
import vn.easyca.signserver.core.services.PDFSigningService;
import vn.easyca.signserver.core.services.SigningService;
import vn.easyca.signserver.core.dto.sign.request.SignRequest;
import vn.easyca.signserver.core.dto.sign.response.PDFSigningDataRes;
import vn.easyca.signserver.core.dto.sign.response.SignDataResponse;
import vn.easyca.signserver.core.dto.sign.response.SignResultElement;
import vn.easyca.signserver.webapp.enm.*;
import vn.easyca.signserver.webapp.service.*;
import vn.easyca.signserver.webapp.service.dto.VaccinationCertDTO;
import vn.easyca.signserver.webapp.utils.AccountUtils;
import vn.easyca.signserver.webapp.web.rest.vm.request.sign.*;
import vn.easyca.signserver.webapp.web.rest.vm.response.BaseResponseVM;
import vn.easyca.signserver.core.services.XMLSigningService;
import vn.easyca.signserver.webapp.enm.TransactionType;
import vn.easyca.signserver.webapp.enm.Method;

import java.util.Base64;
import java.util.List;


@Scope("request")
@RestController
@RequestMapping("/api/sign")
public class SigningResource extends BaseResource {
    private final SigningService signService;
    private static final Logger log = LoggerFactory.getLogger(SigningResource.class);
    private final OfficeSigningService officeSigningService;
    private final PDFSigningService pdfSigningService;
    private final XMLSigningService xmlSigningService;
    private final AsyncTransactionService asyncTransactionService;
    private final FileResourceService fileResourceService;

    public SigningResource(SigningService signService, PDFSigningService pdfSigningService, XMLSigningService xmlSigningService,
                           OfficeSigningService officeSigningService, AsyncTransactionService asyncTransactionService, FileResourceService fileResourceService) {
        this.signService = signService;
        this.pdfSigningService = pdfSigningService;
        this.xmlSigningService = xmlSigningService;
        this.officeSigningService = officeSigningService;
        this.asyncTransactionService = asyncTransactionService;
        this.fileResourceService = fileResourceService;
    }


    @PostMapping(value = "/pdf", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Object> signPDF(@RequestBody SigningRequest<VisibleRequestContent> signingRequest) {
        log.info(" --- signPDF --- ");
        try {
            PDFSigningDataRes signResponse = signService.signPDFFile(signingRequest);
            String resource = Base64.getEncoder().encodeToString(signResponse.getContent());
            status = TransactionStatus.SUCCESS;
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + signingRequest.getSigningRequestContents().get(0).getDocumentName() + "_signed" + ".pdf")
                .body(resource);
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

    @PostMapping(value = "/vaccinationCertSigning")
    public ResponseEntity<BaseResponseVM> signVaccinationCert(@RequestBody VaccinationCertDTO vaccinationCertDTO) {
        log.info(" --- Sign Vaccination Cert --- ");
        try {
            SigningRequest<VisibleRequestContent> signingRequest = vaccinationCertDTO.createSigningRequest(fileResourceService);
            PDFSigningDataRes signResponse = signService.signPDFFile(signingRequest);
            String resource = Base64.getEncoder().encodeToString(signResponse.getContent());
            status = TransactionStatus.SUCCESS;
            return ResponseEntity.ok(new BaseResponseVM(BaseResponseVM.STATUS_OK, resource, "Ký tệp chứng nhận thành công"));
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



    @PostMapping(value = "/hash")
    public ResponseEntity<BaseResponseVM> signHash(@RequestBody SigningVM<String> signingVM) {
        log.info(" --- signHash --- ");
        try {
            SignRequest<String> request = signingVM.getDTO(String.class);
            Object signingDataResponse = signService.signHash(request);
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

    @PostMapping(value = "/raw")
    public ResponseEntity<BaseResponseVM> signRaw(@RequestBody SigningVM<String> signingVM) {
        log.info(" --- signRaw --- ");
        try {
            SignRequest<String> request = signingVM.getDTO(String.class);
            SignDataResponse<List<SignResultElement>> signResponse = signService.signRaw(request);
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

    @PostMapping(value = "/office")
    public ResponseEntity<BaseResponseVM> signOffice(@RequestBody SigningRequest<SigningRequestContent> signingRequest) {
        log.info(" --- signOffice --- ");
        try {
            SigningResponse signingDataResponse = officeSigningService.sign(signingRequest);
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

    @PostMapping(value = "/xml")
    public ResponseEntity<BaseResponseVM> signXML(@RequestBody SigningRequest<SigningRequestContent> signingRequest) {
        log.info(" --- signXML --- ");
        try {
            SigningResponse signingDataResponse = xmlSigningService.sign(signingRequest);
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

    @PostMapping(value = "/invisiblePdf")
    public ResponseEntity<BaseResponseVM> invisibleSignPdf(@RequestBody SigningRequest<SigningRequestContent> signingRequest) {
        log.info(" --- invisiblePdf --- ");
        try {
            SigningResponse signingDataResponse = pdfSigningService.invisibleSign(signingRequest);
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
