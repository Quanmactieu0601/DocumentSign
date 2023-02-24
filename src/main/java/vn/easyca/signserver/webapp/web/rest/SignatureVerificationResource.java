package vn.easyca.signserver.webapp.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import vn.easyca.signserver.core.dto.verification.VerificationResponseDTO;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.core.services.SignatureVerificationService;
import vn.easyca.signserver.core.dto.SignatureVerificationRequest;
import vn.easyca.signserver.webapp.enm.*;
import vn.easyca.signserver.webapp.service.AsyncTransactionService;
import vn.easyca.signserver.webapp.utils.AccountUtils;
import vn.easyca.signserver.webapp.web.rest.vm.request.SignatureVerificationVM;
import vn.easyca.signserver.webapp.web.rest.vm.response.BaseResponseVM;

import java.io.File;
import java.io.FileOutputStream;

@Scope("request")
@Controller
@RequestMapping("/api/verification")
public class SignatureVerificationResource extends BaseResource {
    private static final Logger log = LoggerFactory.getLogger(SignatureVerificationResource.class);
    private final SignatureVerificationService verificationService;
    private final AsyncTransactionService asyncTransactionService;

    public SignatureVerificationResource(SignatureVerificationService verificationService, AsyncTransactionService asyncTransactionService) {
        this.verificationService = verificationService;
        this.asyncTransactionService = asyncTransactionService;
    }

    @PostMapping(value = "/hash")
    public ResponseEntity<BaseResponseVM> verifyHash(@RequestBody SignatureVerificationVM signatureVerificationVM) {
        log.info(" --- verifyHash ---");
        try {
            SignatureVerificationRequest request = signatureVerificationVM.mapToDTO();
            Object result = verificationService.verifyHash(request);
            status = TransactionStatus.SUCCESS;
            return ResponseEntity.ok(BaseResponseVM.createNewSuccessResponse(result));
        } catch (ApplicationException applicationException) {
            log.error(applicationException.getMessage(), applicationException);
            message = applicationException.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, applicationException.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            message = e.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        } finally {
            asyncTransactionService.newThread("/api/verification/hash", TransactionType.BUSINESS, Action.VERIFY, Extension.HASH, Method.POST,
                status, message, AccountUtils.getLoggedAccount());
        }
    }
// @RequestParam("file") MultipartFile file
    @PostMapping(value = "/raw")
    public ResponseEntity<BaseResponseVM> verifyRaw(@RequestBody SignatureVerificationVM signatureVerificationVM) {
        log.info(" --- verifyRaw ---");
        try {
            SignatureVerificationRequest request = signatureVerificationVM.mapToDTO();
            Object result = verificationService.verifyRaw(request);
            status = TransactionStatus.SUCCESS;
            return ResponseEntity.ok(BaseResponseVM.createNewSuccessResponse(result));
        } catch (ApplicationException applicationException) {
            log.error(applicationException.getMessage(), applicationException);
            message = applicationException.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, applicationException.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            message = e.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        } finally {
            asyncTransactionService.newThread("/api/verification/raw", TransactionType.BUSINESS, Action.VERIFY, Extension.RAW, Method.POST,
                status, message, AccountUtils.getLoggedAccount());
        }
    }

    @PostMapping(value = "/pdf")
    public ResponseEntity<BaseResponseVM> verifyPdf(@RequestParam("file") MultipartFile file) {
        log.info(" --- verifyPdf ---");
        try {
            VerificationResponseDTO result = verificationService.verifyPDF(file.getInputStream());
            status = TransactionStatus.SUCCESS;
            return ResponseEntity.ok(BaseResponseVM.createNewSuccessResponse(result));
        } catch (ApplicationException applicationException) {
            message = applicationException.getMessage();
            log.error(message, applicationException);
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, message));
        } catch (Exception e) {
            message = e.getMessage();
            log.error(message, e);
            return ResponseEntity.ok(new BaseResponseVM(-1, null, message));
        } finally {
            asyncTransactionService.newThread("/api/verification/pdf", TransactionType.BUSINESS, Action.VERIFY, Extension.PDF, Method.POST,
                status, message, AccountUtils.getLoggedAccount());
        }
    }

    @PostMapping(value = "/doc")
    public ResponseEntity<BaseResponseVM> verifyDoc(@RequestParam("file") MultipartFile file) {
        try {
            VerificationResponseDTO result = verificationService.verifyDocx(file.getInputStream());
            status = TransactionStatus.SUCCESS;
            return ResponseEntity.ok(BaseResponseVM.createNewSuccessResponse(result));
        } catch (ApplicationException applicationException) {
            message = applicationException.getMessage();
            log.error(message, applicationException);
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, message));
        } catch (Exception e) {
            message = e.getMessage();
            log.error(message, e);
            return ResponseEntity.ok(new BaseResponseVM(-1, null, message));
        } finally {
            asyncTransactionService.newThread("/api/verification/doc", TransactionType.BUSINESS, Action.VERIFY, Extension.OOXML, Method.POST,
                status, message, AccountUtils.getLoggedAccount());
        }
    }

    @PostMapping(value = "/xml")
    public ResponseEntity<BaseResponseVM> verifyXml(@RequestParam("file") MultipartFile file) {
        try {
            VerificationResponseDTO result = verificationService.verifyXml(file.getInputStream());
            status = TransactionStatus.SUCCESS;
            return ResponseEntity.ok(BaseResponseVM.createNewSuccessResponse(result));
        } catch (Exception e) {
            message = e.getMessage();
            log.error(message, e);
            return ResponseEntity.ok(new BaseResponseVM(-1, null, message));
        } finally {
            asyncTransactionService.newThread("/api/verification/xml", TransactionType.BUSINESS, Action.VERIFY, Extension.XML, Method.POST,
                status, message, AccountUtils.getLoggedAccount());
        }
    }
}
