package vn.easyca.signserver.webapp.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.easyca.signserver.webapp.enm.*;
import vn.easyca.signserver.webapp.security.AuthoritiesConstants;
import vn.easyca.signserver.webapp.service.AsyncTransactionService;
import vn.easyca.signserver.webapp.service.FileResourceService;
import vn.easyca.signserver.webapp.service.dto.CertRequestInfoDTO;
import vn.easyca.signserver.webapp.utils.AccountUtils;
import vn.easyca.signserver.webapp.utils.DateTimeUtils;
import vn.easyca.signserver.webapp.utils.ExcelUtils;
import vn.easyca.signserver.webapp.web.rest.vm.response.BaseResponseVM;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

@Scope("request")
@RestController
@RequestMapping("/api/hsm-certificate")
public class HSMCertificateResource extends BaseResource{
    private static final Logger log = LoggerFactory.getLogger(HSMCertificateResource.class);

    private final CertificateGenerateService p11GeneratorService;
    private final AsyncTransactionService asyncTransactionService;
    private final ExcelUtils excelUtils;
    private final FileResourceService fileResourceService;

    public HSMCertificateResource(CertificateGenerateService p11GeneratorService,
                                  AsyncTransactionService asyncTransactionService,
                                  ExcelUtils excelUtils, FileResourceService fileResourceService) {
        this.p11GeneratorService = p11GeneratorService;
        this.asyncTransactionService = asyncTransactionService;
        this.excelUtils = excelUtils;
        this.fileResourceService = fileResourceService;
    }

    @PostMapping("/generate-bulk-csr")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public  ResponseEntity<Object> generateBulkCSR(@RequestParam("file") MultipartFile file) {
        try {
            log.info("--- generate-bulk-csr ---");
            String resultFileName = String.format("Certificate-Request-Infomation_%s.xlsx", DateTimeUtils.getCurrentTimeStamp());
            List<CertRequestInfoDTO> dtos = ExcelUtils.convertCertRequest(file.getInputStream());
            p11GeneratorService.generateBulkCSR(dtos);
            byte[] byteData = excelUtils.exportCsrFileFormat2(dtos);
            InputStreamResource result = new InputStreamResource(new ByteArrayInputStream(byteData));
            status = TransactionStatus.SUCCESS;
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resultFileName)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .contentLength(byteData.length)
                .body(result);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            message = e.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        } finally {
            asyncTransactionService.newThread("/api/hsm-certificate/generate-bulk-csr", TransactionType.BUSINESS, Action.CREATE, Extension.CSR, Method.POST,
                status, message, AccountUtils.getLoggedAccount());
        }
    }

    @GetMapping("/download-csr-template")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Object> getTemplateFileCertificate() {
        try {
            InputStream inputStream = fileResourceService.getTemplateFile("/templates/excel/Certificate-Request-Infomation.xlsx");
            byte[] isr = new byte[inputStream.available()];
            inputStream.read(isr);
            return new ResponseEntity<>(isr, null, HttpStatus.OK);
        } catch (Exception e) {
            log.debug(e.getMessage());
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        }
    }
}
