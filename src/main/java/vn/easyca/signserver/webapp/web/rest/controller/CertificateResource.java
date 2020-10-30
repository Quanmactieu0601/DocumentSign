package vn.easyca.signserver.webapp.web.rest.controller;

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
import vn.easyca.signserver.core.domain.Certificate;
import vn.easyca.signserver.core.dto.CertDTO;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.core.services.P12ImportService;
import vn.easyca.signserver.core.services.CertificateGenerateService;
import vn.easyca.signserver.core.services.CertificateService;
import vn.easyca.signserver.core.dto.CertificateGenerateResult;
import vn.easyca.signserver.core.dto.CertificateGenerateDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.easyca.signserver.core.dto.ImportP12FileDTO;
import vn.easyca.signserver.webapp.security.AuthoritiesConstants;
import vn.easyca.signserver.webapp.service.dto.UserDTO;
import vn.easyca.signserver.webapp.utils.DateTimeUtils;
import vn.easyca.signserver.webapp.utils.ExcelUtils;
import vn.easyca.signserver.infrastructure.database.jpa.entity.CertificateEntity;
import vn.easyca.signserver.webapp.enm.TransactionType;
import vn.easyca.signserver.webapp.service.TransactionService;
import vn.easyca.signserver.webapp.service.dto.TransactionDTO;
import vn.easyca.signserver.webapp.web.rest.mapper.CertificateGeneratorVMMapper;
import vn.easyca.signserver.webapp.utils.MappingHelper;
import vn.easyca.signserver.webapp.web.rest.vm.request.CertificateGeneratorVM;
import vn.easyca.signserver.webapp.web.rest.vm.request.CsrGeneratorVM;
import vn.easyca.signserver.webapp.web.rest.vm.request.P12ImportVM;
import vn.easyca.signserver.webapp.web.rest.vm.request.sign.CsrsGeneratorVM;
import vn.easyca.signserver.webapp.web.rest.vm.response.CertificateGeneratorResultVM;
import vn.easyca.signserver.webapp.web.rest.vm.response.BaseResponseVM;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/certificate")
@ComponentScan("vn.easyca.signserver.core.services")
public class CertificateResource {
    String code = null;
    String message = null;
    private static final Logger log = LoggerFactory.getLogger(CertificateResource.class);

    private static final String ENTITY_NAME = "certificate";

    private final CertificateGenerateService p11GeneratorService;
    private final CertificateService certificateService;

    private final P12ImportService p12ImportService;
    private final TransactionService transactionService;

    public CertificateResource(CertificateGenerateService p11GeneratorService, CertificateService certificateService, P12ImportService p12ImportService, TransactionService transactionService) {
        this.p11GeneratorService = p11GeneratorService;
        this.certificateService = certificateService;
        this.p12ImportService = p12ImportService;
        this.transactionService = transactionService;
    }

    @GetMapping()
    public ResponseEntity<List<CertificateEntity>> getAll(Pageable pageable) {
        Page<CertificateEntity> page = certificateService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<CertificateEntity>> getAllCertificatesByFilter(Pageable pageable, @RequestParam(required = false) String alias, @RequestParam(required = false) String ownerId, @RequestParam(required = false) String serial, @RequestParam(required = false) String validDate, @RequestParam(required = false) String expiredDate) {
        try {
            Page<CertificateEntity> page = certificateService.findByFilter(pageable, alias, ownerId, serial, validDate, expiredDate);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(null, null, HttpStatus.OK);
        }
    }

    @GetMapping("ownerId/{ownerId}")
    public ResponseEntity<List<CertificateEntity>> findByOwnerId(@PathVariable String ownerId) {
        List<CertificateEntity> certificateEntityList = certificateService.getByOwnerId(ownerId);
        return new ResponseEntity<>(certificateEntityList, HttpStatus.OK);
    }

    @PostMapping("/import/p12")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<BaseResponseVM> importP12File(@RequestBody P12ImportVM p12ImportVM) {
        TransactionDTO transactionDTO = new TransactionDTO("/api/certificate/import/p12", TransactionType.IMPORT_CERT);

        try {
            ImportP12FileDTO serviceInput = MappingHelper.map(p12ImportVM, ImportP12FileDTO.class);
            p12ImportService.insert(serviceInput);
            code = "200";
            message = "Insert P12File successfully";
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse("OK"));
        } catch (ApplicationException e) {
            log.error(e.getMessage(), e);
            code = "400";
            message = e.getMessage();
            return ResponseEntity.ok(BaseResponseVM.CreateNewErrorResponse(e));
        } finally {
            transactionDTO.setCode(code);
            transactionDTO.setMessage(message);
            transactionService.save(transactionDTO);
        }
    }

    @PostMapping("/gen/p11")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<BaseResponseVM> genCertificate(@RequestBody CertificateGeneratorVM certificateGeneratorVM) {
        TransactionDTO transactionDTO = new TransactionDTO("/api/certificate/gen/p11", TransactionType.IMPORT_CERT);
        try {
            CertificateGeneratorVMMapper mapper = new CertificateGeneratorVMMapper();
            CertificateGenerateDTO dto = mapper.map(certificateGeneratorVM);
            CertificateGenerateResult result = p11GeneratorService.genCertificate(dto);
            CertificateGeneratorResultVM certificateGeneratorResultVM = new CertificateGeneratorResultVM();
            Object viewModel = MappingHelper.map(result, certificateGeneratorResultVM.getClass());
            code = "200";
            message = "Gen Certificate Successfully";
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(viewModel));
        } catch (ApplicationException applicationException) {
            log.error(applicationException.getMessage(), applicationException);
            code = "400";
            message = applicationException.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, applicationException.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            code = "400";
            message = e.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        } finally {
            transactionDTO.setCode(code);
            transactionDTO.setMessage(message);
            transactionService.save(transactionDTO);
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
            List<CertDTO> dtos = ExcelUtils.convertExcelToCertDTO(file.getInputStream());
            p11GeneratorService.saveCerts(dtos);
            //TODO: hien tai moi chi luu chu chua dua ra thong bao loi chi tiet tung cert (neu xay ra loi)
            return ResponseEntity.status(HttpStatus.OK).body(BaseResponseVM.CreateNewSuccessResponse(null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new BaseResponseVM(-1, null, e.getMessage()));
        }
    }

    @GetMapping("/get-by-serial")
    public ResponseEntity<BaseResponseVM> getBase64Cert(@RequestParam String serial) {
        TransactionDTO transactionDTO = new TransactionDTO("/api/certificate/get-by-serial", TransactionType.IMPORT_CERT);
        try {
            Certificate certificate = certificateService.getBySerial(serial);
            code = "200";
            message = "Get Base64Cert Successfully";
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(certificate.getRawData()));
        } catch (ApplicationException applicationException) {
            log.error(applicationException.getMessage(), applicationException);
            code = "400";
            message = applicationException.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, applicationException.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            code = "400";
            message = e.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        } finally {
            transactionDTO.setCode(code);
            transactionDTO.setMessage(message);
            transactionService.save(transactionDTO);
        }
    }

    @PutMapping("/update-active-status")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<BaseResponseVM> updateActiveStatus(@RequestBody Long id) {
        try {
            certificateService.updateActiveStatus(id);
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(null));
        } catch (Exception e) {
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        }
    }

}
