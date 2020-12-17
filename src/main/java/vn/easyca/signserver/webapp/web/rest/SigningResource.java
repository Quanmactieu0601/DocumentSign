package vn.easyca.signserver.webapp.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.easyca.signserver.core.dto.sign.newrequest.SigningRequest;
import vn.easyca.signserver.core.dto.sign.newresponse.SigningResponse;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.core.services.OfficeSigningService;
import vn.easyca.signserver.core.services.PDFSigningService;
import vn.easyca.signserver.core.services.SigningService;
import vn.easyca.signserver.core.dto.sign.request.content.PDFSignContent;
import vn.easyca.signserver.core.dto.sign.request.SignRequest;
import vn.easyca.signserver.core.dto.sign.response.PDFSigningDataRes;
import vn.easyca.signserver.core.dto.sign.response.SignDataResponse;
import vn.easyca.signserver.core.dto.sign.response.SignResultElement;
import vn.easyca.signserver.webapp.enm.*;
import vn.easyca.signserver.webapp.service.*;
import vn.easyca.signserver.webapp.utils.AccountUtils;
import vn.easyca.signserver.webapp.web.rest.vm.request.sign.*;
import vn.easyca.signserver.webapp.web.rest.vm.response.BaseResponseVM;
import vn.easyca.signserver.core.services.XMLSigningService;
import vn.easyca.signserver.webapp.enm.TransactionType;
import vn.easyca.signserver.webapp.enm.Method;

import java.util.List;


@RestController
@RequestMapping("/api/sign")
public class SigningResource {
    private final SigningService signService;
    private static final Logger log = LoggerFactory.getLogger(SigningResource.class);
    private final OfficeSigningService officeSigningService;
    private final PDFSigningService pdfSigningService;
    private final XMLSigningService xmlSigningService;
    private final AsyncTransactionService asyncTransactionService;

    public SigningResource(SigningService signService, PDFSigningService pdfSigningService, XMLSigningService xmlSigningService,
                           OfficeSigningService officeSigningService, AsyncTransactionService asyncTransactionService) {
        this.signService = signService;
        this.pdfSigningService = pdfSigningService;
        this.xmlSigningService = xmlSigningService;
        this.officeSigningService = officeSigningService;
        this.asyncTransactionService = asyncTransactionService;
    }

    @PostMapping(value = "/pdf", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Object> signPDF(@RequestParam MultipartFile file, SigningVM<PDFSigningContentVM> signingVM) {
        log.info(" --- signPDF --- {}", signingVM);
        try {
            byte[] fileData = file.getBytes();
            SignRequest<PDFSignContent> signRequest = signingVM.getDTO(PDFSignContent.class);
            signRequest.getSignElements().get(0).getContent().setFileData(fileData);
            PDFSigningDataRes signResponse = signService.signPDFFile(signRequest);
            ByteArrayResource resource = new ByteArrayResource(signResponse.getContent());
            asyncTransactionService.newThread("/api/sign/pdf", TransactionType.BUSINESS, Action.SIGN, Extension.PDF, Method.POST,
                TransactionStatus.SUCCESS, null, AccountUtils.getLoggedAccount());
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName() + ".pdf")
                .contentLength(resource.contentLength()) //
                .body(resource);
        } catch (ApplicationException applicationException) {
            log.error(applicationException.getMessage(), applicationException);
            asyncTransactionService.newThread("/api/sign/pdf", TransactionType.BUSINESS, Action.SIGN, Extension.PDF, Method.POST,
                TransactionStatus.FAIL, applicationException.getMessage(), AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, applicationException.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            asyncTransactionService.newThread("/api/sign/pdf", TransactionType.BUSINESS, Action.SIGN, Extension.PDF, Method.POST,
                TransactionStatus.FAIL, e.getMessage(), AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        }
    }

    @PostMapping(value = "/hash")
    public ResponseEntity<BaseResponseVM> signHash(@RequestBody SigningVM<String> signingVM) {
        log.info(" --- signHash --- {}", signingVM);
        try {
            SignRequest<String> request = signingVM.getDTO(String.class);
            Object signingDataResponse = signService.signHash(request);
            asyncTransactionService.newThread("/api/sign/hash", TransactionType.BUSINESS, Action.SIGN, Extension.HASH, Method.POST,
                TransactionStatus.SUCCESS, null, AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(signingDataResponse));
        } catch (ApplicationException applicationException) {
            log.error(applicationException.getMessage(), applicationException);
            asyncTransactionService.newThread("/api/sign/hash", TransactionType.BUSINESS, Action.SIGN, Extension.HASH, Method.POST,
                TransactionStatus.FAIL, applicationException.getMessage(), AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, applicationException.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            asyncTransactionService.newThread("/api/sign/hash", TransactionType.BUSINESS, Action.SIGN, Extension.HASH, Method.POST,
                TransactionStatus.FAIL, e.getMessage(), AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        }
    }

    @PostMapping(value = "/raw")
    public ResponseEntity<BaseResponseVM> signRaw(@RequestBody SigningVM<String> signingVM) {
        log.info(" --- signRaw --- {}", signingVM);
        try {
            SignRequest<String> request = signingVM.getDTO(String.class);
            SignDataResponse<List<SignResultElement>> signResponse = signService.signRaw(request);
            asyncTransactionService.newThread("/api/sign/raw", TransactionType.BUSINESS, Action.SIGN, Extension.RAW, Method.POST,
                TransactionStatus.SUCCESS, null, AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(signResponse));
        } catch (ApplicationException applicationException) {
            log.error(applicationException.getMessage(), applicationException);
            asyncTransactionService.newThread("/api/sign/raw", TransactionType.BUSINESS, Action.SIGN, Extension.RAW, Method.POST,
                TransactionStatus.FAIL, applicationException.getMessage(), AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, applicationException.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            asyncTransactionService.newThread("/api/sign/raw", TransactionType.BUSINESS, Action.SIGN, Extension.RAW, Method.POST,
                TransactionStatus.FAIL, e.getMessage(), AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        }
    }

    @PostMapping(value = "/office")
    public ResponseEntity<BaseResponseVM> signOffice(@RequestBody SigningRequest signingRequest) {
        log.info(" --- signOffice --- {} ", signingRequest);
        try {
            SigningResponse signingDataResponse = officeSigningService.sign(signingRequest);
            asyncTransactionService.newThread("/api/sign/office", TransactionType.BUSINESS, Action.SIGN, Extension.OOXML, Method.POST,
                TransactionStatus.SUCCESS, null, AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(signingDataResponse));
        } catch (ApplicationException applicationException) {
            log.error(applicationException.getMessage(), applicationException);
            asyncTransactionService.newThread("/api/sign/office", TransactionType.BUSINESS, Action.SIGN, Extension.OOXML, Method.POST,
                TransactionStatus.FAIL, applicationException.getMessage(), AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, applicationException.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            asyncTransactionService.newThread("/api/sign/office", TransactionType.BUSINESS, Action.SIGN, Extension.OOXML, Method.POST,
                TransactionStatus.FAIL, e.getMessage(), AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        }
    }

    @PostMapping(value = "/xml")
    public ResponseEntity<BaseResponseVM> signXML(@RequestBody SigningRequest signingRequest) {
        log.info(" --- signXML --- {} ", signingRequest);
        try {
            SigningResponse signingDataResponse = xmlSigningService.sign(signingRequest);
            asyncTransactionService.newThread("/api/sign/xml", TransactionType.BUSINESS, Action.SIGN, Extension.XML, Method.POST,
                TransactionStatus.SUCCESS, null, AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(signingDataResponse));
        } catch (ApplicationException applicationException) {
            log.error(applicationException.getMessage(), applicationException);
            asyncTransactionService.newThread("/api/sign/xml", TransactionType.BUSINESS, Action.SIGN, Extension.XML, Method.POST,
                TransactionStatus.FAIL, applicationException.getMessage(), AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, applicationException.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            asyncTransactionService.newThread("/api/sign/xml", TransactionType.BUSINESS, Action.SIGN, Extension.XML, Method.POST,
                TransactionStatus.FAIL, e.getMessage(), AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        }
    }

    @PostMapping(value = "/invisiblePdf")
    public ResponseEntity<BaseResponseVM> invisibleSignPdf(@RequestBody SigningRequest signingRequest) {
        log.info(" --- invisiblePdf --- {} ", signingRequest);
        try {
            SigningResponse signingDataResponse = pdfSigningService.invisibleSign(signingRequest);
            asyncTransactionService.newThread("/api/sign/invisiblePdf", TransactionType.BUSINESS, Action.SIGN, Extension.PDF, Method.POST,
                TransactionStatus.SUCCESS, null, AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(signingDataResponse));
        } catch (ApplicationException applicationException) {
            log.error(applicationException.getMessage(), applicationException);
            asyncTransactionService.newThread("/api/sign/invisiblePdf", TransactionType.BUSINESS, Action.SIGN, Extension.PDF, Method.POST,
                TransactionStatus.FAIL, applicationException.getMessage(), AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, applicationException.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            asyncTransactionService.newThread("/api/sign/invisiblePdf", TransactionType.BUSINESS, Action.SIGN, Extension.PDF, Method.POST,
                TransactionStatus.FAIL, e.getMessage(), AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        }
    }
}
