package vn.easyca.signserver.webapp.web.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.easyca.signserver.core.dto.ChangePinUserRequest;
import vn.easyca.signserver.core.services.CertificateGenerateService;
import vn.easyca.signserver.webapp.enm.*;
import vn.easyca.signserver.webapp.service.AsyncTransactionService;;
import vn.easyca.signserver.webapp.web.rest.vm.response.BaseResponseVM;

@RestController
@RequestMapping(value = "/api")
public class ChangePinNotLoginResource extends BaseResource {

    private final CertificateGenerateService certificateGenerateService;
    private final AsyncTransactionService asyncTransactionService;

    public ChangePinNotLoginResource(CertificateGenerateService certificateGenerateService, AsyncTransactionService asyncTransactionService) {
        this.certificateGenerateService = certificateGenerateService;
        this.asyncTransactionService = asyncTransactionService;
    }

    @PutMapping(value = "/changePinUserNotLogin")
    public ResponseEntity<BaseResponseVM> changePinForNoLoginUser(@RequestBody ChangePinUserRequest request){
        try{
            certificateGenerateService.changePinCertForNoLoginUser(request);
            status = TransactionStatus.SUCCESS;
            return ResponseEntity.ok(BaseResponseVM.createNewSuccessResponse("Thành công!"));
        }catch (Exception e) {
            message = e.getMessage();
            return ResponseEntity.ok(BaseResponseVM.createNewErrorResponse(message));
        }finally {
            asyncTransactionService.newThread("/api/changePinUserNotLogin", TransactionType.BUSINESS, Action.MODIFY, null, Method.PUT,
                status, message, "Anonymous");
        }
    }
}
