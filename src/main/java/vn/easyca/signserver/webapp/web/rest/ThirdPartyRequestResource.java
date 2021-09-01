package vn.easyca.signserver.webapp.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.easyca.signserver.core.dto.CertificateGenerateDTO;
import vn.easyca.signserver.core.dto.sign.newrequest.SigningContainerRequest;
import vn.easyca.signserver.core.dto.sign.newrequest.SigningRequest;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.core.services.ThirdPartyRequestService;
import vn.easyca.signserver.webapp.enm.*;
import vn.easyca.signserver.webapp.security.AuthoritiesConstants;
import vn.easyca.signserver.webapp.service.AsyncTransactionService;
import vn.easyca.signserver.webapp.utils.AccountUtils;
import vn.easyca.signserver.webapp.utils.MappingHelper;
import vn.easyca.signserver.webapp.web.rest.mapper.CertificateGeneratorVMMapper;
import vn.easyca.signserver.webapp.web.rest.vm.request.CertificateGeneratorVM;
import vn.easyca.signserver.webapp.web.rest.vm.response.BaseResponseVM;
import vn.easyca.signserver.webapp.web.rest.vm.response.CertificateGeneratorResultVM;
import vn.easyca.signserver.webapp.web.rest.vm.response.P12CertificateRegisterResult;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/thirdPartyRequest")
public class ThirdPartyRequestResource extends BaseResource {
    private static final Logger log = LoggerFactory.getLogger(ThirdPartyRequestResource.class);

    private final AsyncTransactionService asyncTransactionService;
    private final ThirdPartyRequestService thirdPartyRequestService;
    public ThirdPartyRequestResource(AsyncTransactionService asyncTransactionService, ThirdPartyRequestService thirdPartyRequestService)  {
        this.asyncTransactionService = asyncTransactionService;
        this.thirdPartyRequestService = thirdPartyRequestService;
    }

    @PostMapping("/registerCerts")
    @PreAuthorize("hasAnyAuthority(\""+ AuthoritiesConstants.ADMIN+"\", \""+AuthoritiesConstants.SUPER_ADMIN+"\")")
    public ResponseEntity<BaseResponseVM> registerCertificates(@Valid @RequestBody List<CertificateGeneratorVM> certificateGenerators) {
        try {
            log.info("--- genCertificate ---");
            CertificateGeneratorVMMapper mapper = new CertificateGeneratorVMMapper();
            List<CertificateGenerateDTO> certificateGenerateDTOList = mapper.map(certificateGenerators);
            List<P12CertificateRegisterResult> result = thirdPartyRequestService.registerCertificate(certificateGenerateDTOList);
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
            asyncTransactionService.newThread("/api/certificate/gen/p11", TransactionType.BUSINESS, Action.CREATE, Extension.CERT, Method.POST,
                status, message, AccountUtils.getLoggedAccount());
        }
    }


    @PostMapping(value = "/sign")
    public ResponseEntity<Object> sign(@RequestBody SigningRequest<SigningContainerRequest<Object, String>> signingRequest) throws Exception {
        try {
            Object res = thirdPartyRequestService.sign(signingRequest);
            return ResponseEntity.ok(new BaseResponseVM(BaseResponseVM.STATUS_OK, res, ""));
        } catch (ApplicationException applicationException) {
            log.error(applicationException.getMessage(), applicationException);
            message = applicationException.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, applicationException.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            message = e.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        } finally {
            asyncTransactionService.newThread("/api/thirdParty/sign", TransactionType.BUSINESS, Action.SIGN, Extension.PDF, Method.POST,
                status, message, AccountUtils.getLoggedAccount());
        }
    }



    @PostMapping(value = "/quickSign")
    public ResponseEntity<Object> quickSign(@RequestBody SigningRequest<SigningContainerRequest<Object, String>> signingRequest) throws Exception {
        try {
            Object res = thirdPartyRequestService.sign(signingRequest);
            return ResponseEntity.ok(new BaseResponseVM(BaseResponseVM.STATUS_OK, res, ""));
        } catch (ApplicationException applicationException) {
            log.error(applicationException.getMessage(), applicationException);
            message = applicationException.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, applicationException.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            message = e.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        } finally {
            asyncTransactionService.newThread("/api/thirdParty/sign", TransactionType.BUSINESS, Action.SIGN, Extension.PDF, Method.POST,
                status, message, AccountUtils.getLoggedAccount());
        }
    }




}
