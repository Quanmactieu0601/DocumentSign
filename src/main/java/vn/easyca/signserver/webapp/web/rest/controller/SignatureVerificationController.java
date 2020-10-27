package vn.easyca.signserver.webapp.web.rest.controller;

import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.core.services.SignatureVerificationService;
import vn.easyca.signserver.core.dto.SignatureVerificationRequest;
import vn.easyca.signserver.webapp.domain.Transaction;
import vn.easyca.signserver.webapp.enm.TransactionType;
import vn.easyca.signserver.webapp.service.dto.TransactionDTO;
import vn.easyca.signserver.webapp.web.rest.vm.request.SignatureVerificationVM;
import vn.easyca.signserver.webapp.web.rest.vm.response.BaseResponseVM;

import java.util.logging.LogManager;

@Controller
@RequestMapping("/api/verification")
public class SignatureVerificationController {

    private static final Logger log = LoggerFactory.getLogger(SignatureVerificationController.class);
    private final SignatureVerificationService verificationService;

    public SignatureVerificationController(SignatureVerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @PostMapping(value = "/hash")
    public ResponseEntity<BaseResponseVM> verifyHash(@RequestBody SignatureVerificationVM signatureVerificationVM) {
        TransactionDTO transactionDTO = new TransactionDTO("/api/verification/hash", TransactionType.SYSTEM);
        try {
            SignatureVerificationRequest request = signatureVerificationVM.mapToDTO();
            Object result = verificationService.verifyHash(request);
            transactionDTO.setCode("200");
            transactionDTO.setMessage("verification hash successful");
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(result));
        } catch (ApplicationException applicationException) {
            log.error(applicationException.getMessage(), applicationException);
            transactionDTO.setCode("400");
            transactionDTO.setMessage("ApplicationException");
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, applicationException.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            transactionDTO.setCode("400");
            transactionDTO.setMessage("Exception");
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        }

    }

    @PostMapping(value = "/raw")
    public ResponseEntity<BaseResponseVM> verifyRaw(@RequestBody SignatureVerificationVM signatureVerificationVM) {
        TransactionDTO transactionDTO = new TransactionDTO("/api/verification/raw",TransactionType.SYSTEM);
        try {
            SignatureVerificationRequest request = signatureVerificationVM.mapToDTO();
            Object result = verificationService.verifyRaw(request);
            transactionDTO.setCode("200");
            transactionDTO.setMessage("Verification Raw Successfully");
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(result));
        } catch (ApplicationException applicationException) {
            log.error(applicationException.getMessage(), applicationException);
            transactionDTO.setCode("400");
            transactionDTO.setMessage("ApplicationException");
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, applicationException.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            transactionDTO.setCode("400");
            transactionDTO.setMessage("Exception");
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        }

    }
}
