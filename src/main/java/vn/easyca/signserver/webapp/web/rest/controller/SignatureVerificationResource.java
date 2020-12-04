package vn.easyca.signserver.webapp.web.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.core.services.SignatureVerificationService;
import vn.easyca.signserver.core.dto.SignatureVerificationRequest;
import vn.easyca.signserver.webapp.enm.Method;
import vn.easyca.signserver.webapp.enm.Status;
import vn.easyca.signserver.webapp.enm.TransactionType;
import vn.easyca.signserver.webapp.service.AsyncTransactionService;
import vn.easyca.signserver.webapp.utils.AccountUtils;
import vn.easyca.signserver.webapp.web.rest.vm.request.SignatureVerificationVM;
import vn.easyca.signserver.webapp.web.rest.vm.response.BaseResponseVM;

@Controller
@RequestMapping("/api/verification")
public class SignatureVerificationResource {
    private static final Logger log = LoggerFactory.getLogger(SignatureVerificationResource.class);
    private final SignatureVerificationService verificationService;
    private final AsyncTransactionService asyncTransactionService;

    public SignatureVerificationResource(SignatureVerificationService verificationService, AsyncTransactionService asyncTransactionService) {
        this.verificationService = verificationService;
        this.asyncTransactionService = asyncTransactionService;
    }

    @PostMapping(value = "/hash")
    public ResponseEntity<BaseResponseVM> verifyHash(@RequestBody SignatureVerificationVM signatureVerificationVM) {
        try {
            SignatureVerificationRequest request = signatureVerificationVM.mapToDTO();
            Object result = verificationService.verifyHash(request);
            asyncTransactionService.newThread("/api/certificate/hash", TransactionType.SYSTEM, Method.POST,
               Status.SUCCESS, null, AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(result));
        } catch (ApplicationException applicationException) {
            log.error(applicationException.getMessage(), applicationException);
            asyncTransactionService.newThread("/api/certificate/hash", TransactionType.SYSTEM, Method.POST,
                Status.FAIL, applicationException.getMessage(), AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, applicationException.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            asyncTransactionService.newThread("/api/certificate/hash", TransactionType.SYSTEM, Method.POST,
                Status.FAIL, e.getMessage(), AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        }
    }

    @PostMapping(value = "/raw")
    public ResponseEntity<BaseResponseVM> verifyRaw(@RequestBody SignatureVerificationVM signatureVerificationVM) {
        try {
            SignatureVerificationRequest request = signatureVerificationVM.mapToDTO();
            Object result = verificationService.verifyRaw(request);
            asyncTransactionService.newThread("/api/certificate/raw", TransactionType.SYSTEM, Method.POST,
                Status.SUCCESS, null, AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(result));
        } catch (ApplicationException applicationException) {
            log.error(applicationException.getMessage(), applicationException);
            asyncTransactionService.newThread("/api/certificate/raw", TransactionType.SYSTEM, Method.POST,
                Status.FAIL, applicationException.getMessage(), AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, applicationException.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            asyncTransactionService.newThread("/api/certificate/raw", TransactionType.SYSTEM, Method.POST,
                Status.FAIL, e.getMessage(), AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        }
    }
}
