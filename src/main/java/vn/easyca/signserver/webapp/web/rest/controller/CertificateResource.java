package vn.easyca.signserver.webapp.web.rest.controller;

import com.google.gson.Gson;
import io.github.jhipster.web.util.PaginationUtil;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import vn.easyca.signserver.core.domain.CertificateDTO;
import vn.easyca.signserver.core.dto.*;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.core.exception.CertificateAppException;
import vn.easyca.signserver.core.services.P12ImportService;
import vn.easyca.signserver.core.services.CertificateGenerateService;
import vn.easyca.signserver.core.utils.CommonUtils;
import vn.easyca.signserver.pki.cryptotoken.P12CryptoToken;
import vn.easyca.signserver.pki.cryptotoken.error.CryptoTokenException;
import vn.easyca.signserver.pki.cryptotoken.error.InitCryptoTokenException;
import vn.easyca.signserver.webapp.domain.SignatureImage;
import vn.easyca.signserver.webapp.domain.UserEntity;
import vn.easyca.signserver.webapp.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.easyca.signserver.webapp.domain.Certificate;
import vn.easyca.signserver.webapp.security.AuthoritiesConstants;
import vn.easyca.signserver.webapp.enm.Method;
import vn.easyca.signserver.webapp.service.dto.CertImportErrorDTO;
import vn.easyca.signserver.webapp.service.dto.CertImportSuccessDTO;
import vn.easyca.signserver.webapp.service.dto.SignatureImageDTO;
import vn.easyca.signserver.webapp.service.mapper.SignatureImageMapper;
import vn.easyca.signserver.webapp.utils.*;
import vn.easyca.signserver.webapp.enm.TransactionType;
import vn.easyca.signserver.webapp.web.rest.mapper.CertificateGeneratorVMMapper;
import vn.easyca.signserver.webapp.web.rest.vm.request.CertificateGeneratorVM;
import vn.easyca.signserver.webapp.web.rest.vm.request.CsrGeneratorVM;
import vn.easyca.signserver.webapp.web.rest.vm.request.P12ImportVM;
import vn.easyca.signserver.webapp.web.rest.vm.request.sign.CsrsGeneratorVM;
import vn.easyca.signserver.webapp.web.rest.vm.response.CertificateGeneratorResultVM;
import vn.easyca.signserver.webapp.web.rest.vm.response.BaseResponseVM;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/certificate")
public class CertificateResource {
    private static final Logger log = LoggerFactory.getLogger(CertificateResource.class);

    private final CertificateGenerateService p11GeneratorService;
    private final CertificateService certificateService;
    private final AsyncTransactionService asyncTransactionService;
    private final P12ImportService p12ImportService;
    private final SignatureImageService signatureImageService;
    private final UserApplicationService userApplicationService;
    private final SignatureTemplateService signatureTemplateService;
    private final SignatureImageMapper signatureImageMapper;

    public CertificateResource(CertificateGenerateService p11GeneratorService, CertificateService certificateService,
                               AsyncTransactionService asyncTransactionService, P12ImportService p12ImportService,
                               SignatureImageService signatureImageService, UserApplicationService userApplicationService,
                               SignatureTemplateService signatureTemplateService, SignatureImageMapper signatureImageMapper) {
        this.p11GeneratorService = p11GeneratorService;
        this.certificateService = certificateService;
        this.asyncTransactionService = asyncTransactionService;
        this.p12ImportService = p12ImportService;
        this.signatureImageService = signatureImageService;
        this.userApplicationService = userApplicationService;
        this.signatureTemplateService = signatureTemplateService;
        this.signatureImageMapper = signatureImageMapper;
    }

    @GetMapping()
    public ResponseEntity<List<Certificate>> getAll(Pageable pageable) {
        log.info("find All Certificate");
        Page<Certificate> page = certificateService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Certificate>> getAllCertificatesByFilter(Pageable pageable, @RequestParam(required = false) String alias, @RequestParam(required = false) String ownerId, @RequestParam(required = false) String serial, @RequestParam(required = false) String validDate, @RequestParam(required = false) String expiredDate) {
        log.info("search Certificate");
        try {
            Page<Certificate> page = certificateService.findByFilter(pageable, alias, ownerId, serial, validDate, expiredDate);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, null, HttpStatus.OK);
        }
    }

    @GetMapping("ownerId/{ownerId}")
    public ResponseEntity<List<Certificate>> findByOwnerId(@PathVariable String ownerId) {
        log.info("get cert by owner Id: {}", ownerId);
        List<Certificate> certificateList = certificateService.getByOwnerId(ownerId);
        return new ResponseEntity<>(certificateList, HttpStatus.OK);
    }

    @PostMapping("/import/p12")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<BaseResponseVM> importP12File(@RequestBody P12ImportVM p12ImportVM) {
        try {
            log.info("importP12File: {}", p12ImportVM);
            ImportP12FileDTO serviceInput = MappingHelper.map(p12ImportVM, ImportP12FileDTO.class);
            p12ImportService.insert(serviceInput);
            asyncTransactionService.newThread("/api/certificate/import/p12", TransactionType.IMPORT_CERT, Method.POST,
                "200", "OK", AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse("OK"));
        } catch (ApplicationException e) {
            log.error(e.getMessage(), e);
            asyncTransactionService.newThread("/api/certificate/import/p12", TransactionType.IMPORT_CERT, Method.POST,
                "400", e.getMessage(), AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(BaseResponseVM.CreateNewErrorResponse(e));
        }
    }

    @PostMapping("/gen/p11")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<BaseResponseVM> genCertificate(@RequestBody CertificateGeneratorVM certificateGeneratorVM) {
        try {
            log.info("genCertificate: {}", certificateGeneratorVM);
            CertificateGeneratorVMMapper mapper = new CertificateGeneratorVMMapper();
            CertificateGenerateDTO dto = mapper.map(certificateGeneratorVM);
            CertificateGenerateResult result = p11GeneratorService.genCertificate(dto);
            CertificateGeneratorResultVM certificateGeneratorResultVM = new CertificateGeneratorResultVM();
            Object viewModel = MappingHelper.map(result, certificateGeneratorResultVM.getClass());
            asyncTransactionService.newThread("/api/certificate/gen/p11", TransactionType.IMPORT_CERT, Method.POST,
                "200", "OK", AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(viewModel));
        } catch (ApplicationException applicationException) {
            log.error(applicationException.getMessage(), applicationException);
            asyncTransactionService.newThread("/api/certificate/gen/p11", TransactionType.IMPORT_CERT, Method.POST,
                "400", applicationException.getMessage(), AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, applicationException.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            asyncTransactionService.newThread("/api/certificate/gen/p11", TransactionType.IMPORT_CERT, Method.POST,
                "400", e.getMessage(), AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        }
    }

    /**
     * Tạo CSR và User (nếu chưa tồn tại) theo thông tin gửi lên
     *
     * @param certificateGeneratorVM
     * @return
     */
    @PostMapping("/createCSRAndUser")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<BaseResponseVM> createCSR(@RequestBody CertificateGeneratorVM certificateGeneratorVM) {
        try {
            log.info("createCSRAndUser: {}", certificateGeneratorVM);
            CertificateGeneratorVMMapper mapper = new CertificateGeneratorVMMapper();
            CertificateGenerateDTO dto = mapper.map(certificateGeneratorVM);
            CertificateGenerateResult result = p11GeneratorService.saveUserAndCreateCSR(dto);
            CertificateGeneratorResultVM certificateGeneratorResultVM = new CertificateGeneratorResultVM();
            Object viewModel = MappingHelper.map(result, certificateGeneratorResultVM.getClass());
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(viewModel));
        } catch (ApplicationException applicationException) {
            log.error(applicationException.getMessage(), applicationException);
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, applicationException.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        }
    }

    /**
     * Tạo CSR từ user có sẵn
     *
     * @param csrGeneratorVM
     * @return
     */
    @PostMapping("/createCSR")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<BaseResponseVM> createCSR(@RequestBody CsrGeneratorVM csrGeneratorVM) {
        try {
            log.info("createCSR: {}", csrGeneratorVM);
            CertificateGenerateResult result = p11GeneratorService.createCSR(csrGeneratorVM);
            CertificateGeneratorResultVM certificateGeneratorResultVM = new CertificateGeneratorResultVM();
            Object viewModel = MappingHelper.map(result, certificateGeneratorResultVM.getClass());
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(viewModel));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        }
    }

    /**
     * tạo csr từ các user có sẵn và trả về file excel
     *
     * @param
     * @return
     */
    @PostMapping("/exportCsr")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Resource> createCSRs(@RequestBody CsrsGeneratorVM dto) {
        String filename = "EasyCA-CSR-Export" + DateTimeUtils.getCurrentTimeStamp() + ".xlsx";
        try {
            log.info("exportCsr: {}", dto);
            List<CertDTO> csrResult = p11GeneratorService.createCSRs(dto);
            byte[] byteData = ExcelUtils.exportCsrFile(csrResult);
            InputStreamResource file = new InputStreamResource(new ByteArrayInputStream(byteData));
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    @PostMapping("/uploadCert")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<BaseResponseVM> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            log.info("uploadCert");
            List<CertDTO> dtos = ExcelUtils.convertExcelToCertDTO(file.getInputStream());
            p11GeneratorService.saveCerts(dtos);
            //TODO: hien tai moi chi luu chu chua dua ra thong bao loi chi tiet tung cert (neu xay ra loi)
//            return ResponseEntity.status(HttpStatus.OK).body(BaseResponseVM.CreateNewSuccessResponse(null));
            return ResponseEntity.ok(new BaseResponseVM(HttpStatus.OK.value(), null, null));
        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new BaseResponseVM(-1, null, e.getMessage()));
            return ResponseEntity.ok(new BaseResponseVM(HttpStatus.EXPECTATION_FAILED.value(), null, e.getMessage()));
        }
    }

    @GetMapping("/get-by-serial")
    public ResponseEntity<BaseResponseVM> getBase64Cert(@RequestParam String serial) {
        try {
            log.info("getBase64Cert by serial: {}", serial);
            CertificateDTO certificateDTO = certificateService.getBySerial(serial);
            asyncTransactionService.newThread("/api/certificate/get-by-serial", TransactionType.IMPORT_CERT, Method.GET,
                "200", "OK", AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(certificateDTO.getRawData()));
        } catch (ApplicationException applicationException) {
            log.error(applicationException.getMessage(), applicationException);
            asyncTransactionService.newThread("/api/certificate/get-by-serial", TransactionType.IMPORT_CERT, Method.GET,
                "400", applicationException.getMessage(), AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, applicationException.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            asyncTransactionService.newThread("/api/certificate/get-by-serial", TransactionType.IMPORT_CERT, Method.GET,
                "400", e.getMessage(), AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        }
    }

    @PutMapping("/update-active-status")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<BaseResponseVM> updateActiveStatus(@RequestBody Long id) {
        log.info("updateActiveStatus:  certid {}", id);
        try {
            certificateService.updateActiveStatus(id);
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(null));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        }
    }

    @GetMapping("/getImage")
    public ResponseEntity<BaseResponseVM> getImage(@RequestParam String serial, @RequestParam String pin) {
        log.info(" --- getImage --- serial: {}", serial);
        try {
            String base64Image = certificateService.getSignatureImage(serial, pin);
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(base64Image));
        } catch (ApplicationException e) {
            log.error(e.getMessage());
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        }
    }
}
