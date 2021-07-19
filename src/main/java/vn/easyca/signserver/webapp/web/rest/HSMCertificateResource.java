package vn.easyca.signserver.webapp.web.rest;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.easyca.signserver.core.services.CertificateGenerateService;
import vn.easyca.signserver.webapp.enm.*;
import vn.easyca.signserver.webapp.security.AuthoritiesConstants;
import vn.easyca.signserver.webapp.service.AsyncTransactionService;
import vn.easyca.signserver.webapp.service.FileResourceService;
import vn.easyca.signserver.webapp.service.UserApplicationService;
import vn.easyca.signserver.webapp.service.dto.CertRequestInfoDTO;
import vn.easyca.signserver.webapp.utils.AccountUtils;
import vn.easyca.signserver.webapp.utils.ExcelUtils;
import vn.easyca.signserver.webapp.web.rest.vm.response.BaseResponseVM;

import java.io.InputStream;
import java.util.List;

@Scope("request")
@RestController
@RequestMapping("/api/hsm-certificate")
public class HSMCertificateResource extends BaseResource {
    private static final Logger log = LoggerFactory.getLogger(HSMCertificateResource.class);

    private final AsyncTransactionService asyncTransactionService;
    private final ExcelUtils excelUtils;
    private final FileResourceService fileResourceService;
    private final CertificateGenerateService p11GeneratorService;
    private final UserApplicationService userApplicationService;

    public HSMCertificateResource(AsyncTransactionService asyncTransactionService, ExcelUtils excelUtils,
                                  FileResourceService fileResourceService, CertificateGenerateService p11GeneratorService,
                                  UserApplicationService userApplicationService) {
        this.asyncTransactionService = asyncTransactionService;
        this.excelUtils = excelUtils;
        this.fileResourceService = fileResourceService;
        this.p11GeneratorService = p11GeneratorService;
        this.userApplicationService = userApplicationService;
    }

    @PostMapping("/generate-bulk-csr")
    @PreAuthorize("hasAnyAuthority(\""+AuthoritiesConstants.ADMIN+"\", \""+AuthoritiesConstants.SUPER_ADMIN+"\")")
    public ResponseEntity<BaseResponseVM> generateBulkCSR(@RequestParam("file") MultipartFile file) {
        try {
            log.info("--- generate-bulk-csr ---");
//            String resultFileName = String.format("Certificate-Request-Infomation_%s.xlsx", DateTimeUtils.getCurrentTimeStamp());
            List<CertRequestInfoDTO> dtos = ExcelUtils.convertCertRequest(file.getInputStream());
            p11GeneratorService.generateBulkCSR(dtos);
            byte[] byteData = excelUtils.exportCsrFileFormat2(dtos, CertRequestInfoDTO.STEP_2);
            status = TransactionStatus.SUCCESS;
            return ResponseEntity.ok(BaseResponseVM.createNewSuccessResponse(byteData));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            message = e.getMessage();
            return ResponseEntity.ok(BaseResponseVM.createNewErrorResponse(message));
        } finally {
            asyncTransactionService.newThread("/api/hsm-certificate/generate-bulk-csr", TransactionType.BUSINESS, Action.CREATE, Extension.CSR, Method.POST,
                status, message, AccountUtils.getLoggedAccount());
        }
    }

    @GetMapping("/download-csr-template")
    @PreAuthorize("hasAnyAuthority(\""+AuthoritiesConstants.ADMIN+"\", \""+AuthoritiesConstants.SUPER_ADMIN+"\")")
    public ResponseEntity<Object> getTemplateFileCertificate() {
        try {
            log.info("--- download-certificate-request-template ---");
            InputStream inputStream = fileResourceService.getTemplateFile("/templates/excel/Certificate-Request-Infomation.xlsx");
            return ResponseEntity.ok(BaseResponseVM.createNewSuccessResponse(IOUtils.toByteArray(inputStream)));
        } catch (Exception e) {
            log.debug(e.getMessage());
            return ResponseEntity.ok(BaseResponseVM.createNewErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/import-cert-to-hsm")
    @PreAuthorize("hasAnyAuthority(\""+AuthoritiesConstants.ADMIN+"\", \""+AuthoritiesConstants.SUPER_ADMIN+"\")")
    public ResponseEntity<BaseResponseVM> importCertToHSM(@RequestParam("file") MultipartFile file) {
        try {
            log.info("--- import-cert-to-hsm ---");
            List<CertRequestInfoDTO> dtos = ExcelUtils.convertCertRequest(file.getInputStream());
            String currentUser = AccountUtils.getLoggedAccount();
            p11GeneratorService.installCertIntoHsm(dtos, currentUser);
            byte[] byteData = excelUtils.exportCsrFileFormat2(dtos, CertRequestInfoDTO.STEP_4);
            status = TransactionStatus.SUCCESS;
            return ResponseEntity.ok(BaseResponseVM.createNewSuccessResponse(byteData));
        } catch (Exception e) {
            message = e.getMessage();
            return ResponseEntity.ok(BaseResponseVM.createNewErrorResponse(message));
        } finally {
            asyncTransactionService.newThread("/api/hsm-certificate/import-cert-to-hsm", TransactionType.BUSINESS, Action.CREATE, Extension.CERT, Method.POST,
                status, message, AccountUtils.getLoggedAccount());
        }
    }
}
