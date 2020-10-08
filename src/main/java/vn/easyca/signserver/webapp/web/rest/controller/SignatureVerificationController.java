package vn.easyca.signserver.webapp.web.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.easyca.signserver.application.services.SignatureVerificationService;
import vn.easyca.signserver.application.dto.SignatureVerificationRequest;
import vn.easyca.signserver.webapp.web.rest.vm.request.SignatureVerificationVM;
import vn.easyca.signserver.webapp.web.rest.vm.response.BaseResponseVM;

@Controller
@RequestMapping("/api/verification")
public class SignatureVerificationController {

    @Autowired
    SignatureVerificationService verificationService;

    @PostMapping(value = "/hash")
    public ResponseEntity<BaseResponseVM> verifyHash(@RequestBody SignatureVerificationVM signatureVerificationVM) {
        try {

            SignatureVerificationRequest request = signatureVerificationVM.mapToDTO();
            Object result = verificationService.verifyHash(request);
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(result));
        } catch (Exception e) {
            return ResponseEntity.ok(BaseResponseVM.CreateNewErrorResponse(e.getMessage()));
        }

    }

    @PostMapping(value = "/raw")
    public ResponseEntity<BaseResponseVM> verifyRaw(@RequestBody SignatureVerificationVM signatureVerificationVM) {
        try {
            SignatureVerificationRequest request = signatureVerificationVM.mapToDTO();
            Object result = verificationService.verifyRaw(request);
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(result));
        } catch (Exception e) {
            return ResponseEntity.ok(BaseResponseVM.CreateNewErrorResponse(e.getMessage()));
        }

    }
}
