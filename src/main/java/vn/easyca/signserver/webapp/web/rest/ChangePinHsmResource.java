package vn.easyca.signserver.webapp.web.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.easyca.signserver.core.dto.ChangePinHsmUserRequest;
import vn.easyca.signserver.core.services.CertificateGenerateService;
import vn.easyca.signserver.core.services.ChangeHsmCertPinService;
import vn.easyca.signserver.webapp.enm.*;
import vn.easyca.signserver.webapp.service.AsyncTransactionService;
import vn.easyca.signserver.webapp.utils.AccountUtils;
import vn.easyca.signserver.webapp.web.rest.vm.response.BaseResponseVM;

@RestController
@RequestMapping(value = "/hsm")
public class ChangePinHsmResource extends BaseResource {

    private final ChangeHsmCertPinService changeHsmCertPinService;
    private final AsyncTransactionService asyncTransactionService;

    public ChangePinHsmResource(ChangeHsmCertPinService changeHsmCertPinService, AsyncTransactionService asyncTransactionService) {
        this.changeHsmCertPinService = changeHsmCertPinService;
        this.asyncTransactionService = asyncTransactionService;
    }

    @PutMapping(value = "/changePinUserHsm")
    public ResponseEntity<BaseResponseVM> changePinForHsmUser(@RequestBody ChangePinHsmUserRequest request){
        try{
            changeHsmCertPinService.changePinCertForUser(request);
            status = TransactionStatus.SUCCESS;
            return ResponseEntity.ok(BaseResponseVM.createNewSuccessResponse("Thành công!"));
        }catch (Exception e) {
            message = e.getMessage();
            return ResponseEntity.ok(BaseResponseVM.createNewErrorResponse(message));
        } finally {
            asyncTransactionService.newThread("/api/hsm-certificate/import-cert-to-hsm", TransactionType.BUSINESS, Action.CREATE, Extension.CERT, Method.POST,
                status, message, AccountUtils.getLoggedAccount());
        }
    }
}
