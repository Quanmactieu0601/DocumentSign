package vn.easyca.signserver.webapp.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
            asyncTransactionService.newThread("/api/certificate/hash", TransactionType.BUSINESS, Action.VERIFY, Extension.HASH, Method.POST,
                TransactionStatus.SUCCESS, null, AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(result));
        } catch (ApplicationException applicationException) {
            log.error(applicationException.getMessage(), applicationException);
            asyncTransactionService.newThread("/api/certificate/hash", TransactionType.BUSINESS, Action.VERIFY, Extension.HASH, Method.POST,
                TransactionStatus.FAIL, applicationException.getMessage(), AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, applicationException.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            asyncTransactionService.newThread("/api/certificate/hash", TransactionType.BUSINESS, Action.VERIFY, Extension.HASH, Method.POST,
                TransactionStatus.FAIL, e.getMessage(), AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        }
    }

    @PostMapping(value = "/raw")
    public ResponseEntity<BaseResponseVM> verifyRaw(@RequestBody SignatureVerificationVM signatureVerificationVM) {
        log.info(" --- verifyRaw ---");
        try {
            SignatureVerificationRequest request = signatureVerificationVM.mapToDTO();
            Object result = verificationService.verifyRaw(request);
            asyncTransactionService.newThread("/api/certificate/raw", TransactionType.BUSINESS, Action.VERIFY, Extension.RAW, Method.POST,
                TransactionStatus.SUCCESS, null, AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(result));
        } catch (ApplicationException applicationException) {
            log.error(applicationException.getMessage(), applicationException);
            asyncTransactionService.newThread("/api/certificate/raw", TransactionType.BUSINESS, Action.VERIFY, Extension.RAW, Method.POST,
                TransactionStatus.FAIL, applicationException.getMessage(), AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, applicationException.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            asyncTransactionService.newThread("/api/certificate/raw", TransactionType.BUSINESS, Action.VERIFY, Extension.RAW, Method.POST,
                TransactionStatus.FAIL, e.getMessage(), AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        }
    }

    @PostMapping(value = "/pdf")
    public ResponseEntity<BaseResponseVM> verifyPdf(@RequestParam("file") MultipartFile file) {
        log.info(" --- verifyPdf ---");
        try {
            VerificationResponseDTO result = verificationService.verifyPDF(file.getInputStream());
            status = TransactionStatus.SUCCESS;
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(result));
        } catch (ApplicationException applicationException) {
            message = applicationException.getMessage();
            log.error(message, applicationException);
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, message));
        } catch (Exception e) {
            message = e.getMessage();
            log.error(message, e);
            return ResponseEntity.ok(new BaseResponseVM(-1, null, message));
        } finally {
            asyncTransactionService.newThread("/api/pdf", TransactionType.BUSINESS, Action.VERIFY, Extension.PDF, Method.POST,
                status, message, AccountUtils.getLoggedAccount());
        }
    }
}
