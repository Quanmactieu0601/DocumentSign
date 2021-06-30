package vn.easyca.signserver.webapp.web.rest;

import io.github.jhipster.web.util.PaginationUtil;
import org.springframework.context.annotation.Scope;
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
import vn.easyca.signserver.core.factory.CryptoTokenProxy;
import vn.easyca.signserver.core.factory.CryptoTokenProxyFactory;
import vn.easyca.signserver.core.services.P12ImportService;
import vn.easyca.signserver.core.services.CertificateGenerateService;
import vn.easyca.signserver.webapp.enm.*;
import vn.easyca.signserver.webapp.service.*;
import vn.easyca.signserver.core.dto.CertificateGenerateResult;
import vn.easyca.signserver.core.dto.CertificateGenerateDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.easyca.signserver.webapp.domain.Certificate;
import vn.easyca.signserver.webapp.security.AuthoritiesConstants;
import vn.easyca.signserver.webapp.utils.AccountUtils;
import vn.easyca.signserver.webapp.utils.DateTimeUtils;
import vn.easyca.signserver.webapp.utils.ExcelUtils;
import vn.easyca.signserver.webapp.enm.Method;
import vn.easyca.signserver.webapp.service.mapper.SignatureImageMapper;
import vn.easyca.signserver.webapp.utils.*;
import vn.easyca.signserver.webapp.enm.TransactionType;
import vn.easyca.signserver.webapp.web.rest.errors.BadRequestAlertException;
import vn.easyca.signserver.webapp.web.rest.mapper.CertificateGeneratorVMMapper;
import vn.easyca.signserver.webapp.web.rest.vm.request.CertificateGeneratorVM;
import vn.easyca.signserver.webapp.web.rest.vm.request.CsrGeneratorVM;
import vn.easyca.signserver.webapp.web.rest.vm.request.P12ImportVM;
import vn.easyca.signserver.webapp.web.rest.vm.request.P12PinVM;
import vn.easyca.signserver.webapp.web.rest.vm.request.sign.CsrsGeneratorVM;
import vn.easyca.signserver.webapp.web.rest.vm.response.CertificateGeneratorResultVM;
import vn.easyca.signserver.webapp.web.rest.vm.response.BaseResponseVM;

import java.io.*;
import java.util.Base64;
import java.util.List;
import java.io.ByteArrayInputStream;
import java.util.Optional;

@Scope("request")
@RestController
@RequestMapping("/api/certificate")
public class CertificateResource extends BaseResource {
    private static final Logger log = LoggerFactory.getLogger(CertificateResource.class);

    private final CertificateGenerateService p11GeneratorService;
    private final CertificateService certificateService;
    private final AsyncTransactionService asyncTransactionService;
    private final P12ImportService p12ImportService;
    private final SignatureImageService signatureImageService;
    private final UserApplicationService userApplicationService;
    private final SignatureTemplateService signatureTemplateService;
    private final SignatureImageMapper signatureImageMapper;
    private final ExcelUtils excelUtils;
    private final CryptoTokenProxyFactory cryptoTokenProxyFactory;

    public CertificateResource(CertificateGenerateService p11GeneratorService, CertificateService certificateService,
                               AsyncTransactionService asyncTransactionService, P12ImportService p12ImportService,
                               SignatureImageService signatureImageService, UserApplicationService userApplicationService,
                               SignatureTemplateService signatureTemplateService, SignatureImageMapper signatureImageMapper,
                               ExcelUtils excelUtils, CryptoTokenProxyFactory cryptoTokenProxyFactory) {
        this.p11GeneratorService = p11GeneratorService;
        this.certificateService = certificateService;
        this.asyncTransactionService = asyncTransactionService;
        this.p12ImportService = p12ImportService;
        this.signatureImageService = signatureImageService;
        this.userApplicationService = userApplicationService;
        this.signatureTemplateService = signatureTemplateService;
        this.signatureImageMapper = signatureImageMapper;
        this.excelUtils = excelUtils;
        this.cryptoTokenProxyFactory = cryptoTokenProxyFactory;
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
        log.info(" --- getAllCertificatesByFilter ---");
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
        try {
            List<Certificate> certificateList = certificateService.getByOwnerId(ownerId);
            return new ResponseEntity<>(certificateList, HttpStatus.OK);
        } catch (Exception e) {
            throw new BadRequestAlertException(e.getMessage(), "certificate", "findByOwnerId");
        }
    }

    @PostMapping("/import/p12")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<BaseResponseVM> importP12File(@RequestBody P12ImportVM p12ImportVM) {
        try {
            log.info("--- importP12File ---");
            ImportP12FileDTO serviceInput = MappingHelper.map(p12ImportVM, ImportP12FileDTO.class);
            p12ImportService.insert(serviceInput);

            asyncTransactionService.newThread("/api/certificate/import/p12", TransactionType.BUSINESS, Action.CREATE, Extension.CERT, Method.POST,
                TransactionStatus.SUCCESS, null, AccountUtils.getLoggedAccount());
            return ResponseEntity.ok(BaseResponseVM.createNewSuccessResponse("OK"));
        } catch (ApplicationException  e) {
            log.error(e.getMessage(), e);
            message = e.getMessage();
            return ResponseEntity.ok(BaseResponseVM.createNewErrorResponse(e));
        } finally {
            asyncTransactionService.newThread("/api/certificate/import/p12", TransactionType.BUSINESS, Action.CREATE, Extension.CERT, Method.POST,
                status, message, AccountUtils.getLoggedAccount());
        }
    }

    @PostMapping("/import/p12file")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<BaseResponseVM> importP12File2(@RequestParam("file") MultipartFile file, @RequestParam String ownerId, @RequestParam String pin) {
        try {
            String base64File = "";
            base64File = Base64.getEncoder().encodeToString(file.getBytes());

            P12ImportVM p12ImportVM = new P12ImportVM();
            p12ImportVM.setP12Base64(base64File);
            p12ImportVM.setOwnerId(ownerId);
            p12ImportVM.setPin(pin);

            log.info("importP12File: {}", p12ImportVM);
            ImportP12FileDTO serviceInput = MappingHelper.map(p12ImportVM, ImportP12FileDTO.class);
            p12ImportService.insertP12(serviceInput);
            status = TransactionStatus.SUCCESS;
            return ResponseEntity.ok(BaseResponseVM.createNewSuccessResponse("OK"));
        } catch (ApplicationException | FileNotFoundException e) {
            log.error(e.getMessage(), e);
            message = e.getMessage();
            return ResponseEntity.ok(BaseResponseVM.createNewErrorResponse((ApplicationException) e));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            message = e.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        } finally {
            asyncTransactionService.newThread("/api/certificate/import/p12file", TransactionType.BUSINESS, Action.CREATE, Extension.CERT, Method.POST,
                status, message, AccountUtils.getLoggedAccount());
        }
    }

    @PostMapping("/gen/p11")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<BaseResponseVM> genCertificate(@RequestBody CertificateGeneratorVM certificateGeneratorVM) {
        try {
            log.info("--- genCertificate ---");
            CertificateGeneratorVMMapper mapper = new CertificateGeneratorVMMapper();
            CertificateGenerateDTO dto = mapper.map(certificateGeneratorVM);
            CertificateGenerateResult result = p11GeneratorService.genCertificate(dto);
            CertificateGeneratorResultVM certificateGeneratorResultVM = new CertificateGeneratorResultVM();
            Object viewModel = MappingHelper.map(result, certificateGeneratorResultVM.getClass());
            status = TransactionStatus.SUCCESS;
            return ResponseEntity.ok(BaseResponseVM.createNewSuccessResponse(viewModel));
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
            log.info("--- createCSRAndUser ---");
            CertificateGeneratorVMMapper mapper = new CertificateGeneratorVMMapper();
            CertificateGenerateDTO dto = mapper.map(certificateGeneratorVM);
            CertificateGenerateResult result = p11GeneratorService.saveUserAndCreateCSR(dto);
            CertificateGeneratorResultVM certificateGeneratorResultVM = new CertificateGeneratorResultVM();
            Object viewModel = MappingHelper.map(result, certificateGeneratorResultVM.getClass());
            status = TransactionStatus.SUCCESS;
            return ResponseEntity.ok(BaseResponseVM.createNewSuccessResponse(viewModel));
        } catch (ApplicationException applicationException) {
            log.error(applicationException.getMessage(), applicationException);
            message = applicationException.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, applicationException.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            message = e.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        } finally {
            asyncTransactionService.newThread("/api/certificate/createCSRAndUser", TransactionType.BUSINESS, Action.CREATE, Extension.CSR, Method.POST,
                status, message, AccountUtils.getLoggedAccount());
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
            log.info("--- createCSR ---");
            CertificateGenerateResult result = p11GeneratorService.createCSR(csrGeneratorVM);
            CertificateGeneratorResultVM certificateGeneratorResultVM = new CertificateGeneratorResultVM();
            Object viewModel = MappingHelper.map(result, certificateGeneratorResultVM.getClass());
            status = TransactionStatus.SUCCESS;
            return ResponseEntity.ok(BaseResponseVM.createNewSuccessResponse(viewModel));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            message = e.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        } finally {
            asyncTransactionService.newThread("/api/certificate/createCSR", TransactionType.BUSINESS, Action.CREATE, Extension.CSR, Method.POST,
                status, message, AccountUtils.getLoggedAccount());
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
            log.info("--- exportCsr ---");
            List<CertDTO> csrResult = p11GeneratorService.createCSRs(dto);
            byte[] byteData = excelUtils.exportCsrFileFormat1(csrResult);
            InputStreamResource file = new InputStreamResource(new ByteArrayInputStream(byteData));
            status = TransactionStatus.SUCCESS;
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
//                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .contentType(MediaType.parseMediaType("text/plain;charset=ISO-8859-1"))
                .body(file);
        } catch (Exception e) {
            log.error(e.getMessage());
            message = e.getMessage();
            return null;
        } finally {
            asyncTransactionService.newThread("/api/certificate/exportCsr", TransactionType.BUSINESS, Action.CREATE, Extension.CSR, Method.POST,
                status, message, AccountUtils.getLoggedAccount());
        }
    }

    @PostMapping("/uploadCert")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")" + "|| hasAuthority(\"" + AuthoritiesConstants.SUPER_ADMIN + "\")")
    public ResponseEntity<BaseResponseVM> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            log.info("--- uploadCert ---");
            List<CertDTO> dtos = ExcelUtils.convertExcelToCertDTO(file.getInputStream());
            p11GeneratorService.saveCerts(dtos);
            //TODO: hien tai moi chi luu chu chua dua ra thong bao loi chi tiet tung cert (neu xay ra loi)
//            return ResponseEntity.status(HttpStatus.OK).body(BaseResponseVM.createNewSuccessResponse(null));
            status = TransactionStatus.SUCCESS;
            return ResponseEntity.ok(new BaseResponseVM(HttpStatus.OK.value(), null, null));
        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new BaseResponseVM(-1, null, e.getMessage()));
            message = e.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(HttpStatus.EXPECTATION_FAILED.value(), null, e.getMessage()));
        } finally {
            asyncTransactionService.newThread("/api/certificate/uploadCert", TransactionType.BUSINESS, Action.CREATE, Extension.CERT, Method.POST,
                status, message, AccountUtils.getLoggedAccount());
        }
    }

    @GetMapping("/get-by-serial")
    public ResponseEntity<BaseResponseVM> getBase64Cert(@RequestParam String serial) {
        try {
            log.info("getBase64Cert by serial: {}", serial);
            CertificateDTO certificateDTO = certificateService.getBySerial(serial);
            status = TransactionStatus.SUCCESS;
            return ResponseEntity.ok(BaseResponseVM.createNewSuccessResponse(certificateDTO.getRawData()));
        } catch (ApplicationException applicationException) {
            log.error(applicationException.getMessage(), applicationException);
            message = applicationException.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, applicationException.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            message = e.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        } finally {
            asyncTransactionService.newThread("/api/certificate/get-by-serial", TransactionType.BUSINESS, Action.GET_INFO, Extension.CERT, Method.GET,
                status, message, AccountUtils.getLoggedAccount());
        }
    }

    @PutMapping("/update-active-status")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<BaseResponseVM> updateActiveStatus(@RequestBody Long id) {
        log.info("updateActiveStatus:  certid {}", id);
        try {
            certificateService.updateActiveStatus(id);
            status = TransactionStatus.SUCCESS;
            return ResponseEntity.ok(BaseResponseVM.createNewSuccessResponse(null));
        } catch (Exception e) {
            message = e.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        } finally {
            asyncTransactionService.newThread("/api/certificate/update-active/status", TransactionType.BUSINESS, Action.MODIFY, Extension.CERT, Method.PUT,
                status, message, AccountUtils.getLoggedAccount());
        }
    }

    @GetMapping("/getImage")
    public ResponseEntity<BaseResponseVM> getSignatureTemplateImage(@RequestParam String serial, @RequestParam String pin) {
        log.info(" --- getImage --- serial: {}", serial);
        try {
            String base64Image = certificateService.getSignatureImage(serial, pin);
            status = TransactionStatus.SUCCESS;
            return ResponseEntity.ok(BaseResponseVM.createNewSuccessResponse(base64Image));
        } catch (ApplicationException e) {
            log.error(e.getMessage());
            message = e.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(e.getCode(), null, e.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage());
            message = e.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        } finally {
            asyncTransactionService.newThread("/api/certificate/getImage", TransactionType.BUSINESS, Action.GET_INFO, Extension.SIGN_TEMPLATE, Method.GET,
                status, message, AccountUtils.getLoggedAccount());
        }
    }

    @GetMapping("/getImageByTemplateId")
    public ResponseEntity<BaseResponseVM> getSignatureImageByTemplateId(@RequestParam String serial, @RequestParam String pin, @RequestParam Long templateId) {
        log.info(" --- getImage --- serial: {}", serial);
        try {
            String base64Image = certificateService.getSignatureImageByTemplateId(serial, pin, templateId);
            status = TransactionStatus.SUCCESS;
            return ResponseEntity.ok(BaseResponseVM.createNewSuccessResponse(base64Image));
        } catch (ApplicationException e) {
            log.error(e.getMessage());
            message = e.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(e.getCode(), null, e.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage());
            message = e.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        } finally {
            asyncTransactionService.newThread("/api/certificate/getImage", TransactionType.BUSINESS, Action.GET_INFO, Extension.SIGN_TEMPLATE, Method.GET,
                status, message, AccountUtils.getLoggedAccount());
        }
    }

    @GetMapping("/getQRCodeOTP")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<BaseResponseVM> getQRCodeOTP(@RequestParam String serial, @RequestParam String pin) {
        log.info(" --- getQRCodeOTP --- serial: {}", serial);
        try {
            String base64Image = certificateService.getBase64OTPQRCode(serial, pin);
            return ResponseEntity.ok(BaseResponseVM.createNewSuccessResponse(base64Image));
        } catch (ApplicationException e) {
            log.error(e.getMessage());
            message = e.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(e.getCode(), null, e.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage());
            message = e.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        } finally {
            asyncTransactionService.newThread("/api/certificate/getQRCodeOTP", TransactionType.BUSINESS, Action.GET_INFO, Extension.QR_CODE, Method.GET,
                status, message, AccountUtils.getLoggedAccount());
        }
    }

    @PostMapping("/changeCertPIN")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<BaseResponseVM> changeCertPIN(@RequestBody P12PinVM p12PinVM) {
        try {
            log.info(" --- changeCertPIN --- serial: {}", p12PinVM.serial);
            certificateService.changePIN(p12PinVM.serial, p12PinVM.oldPIN, p12PinVM.newPIN, p12PinVM.otpCode);
            status = TransactionStatus.SUCCESS;
            return ResponseEntity.ok(BaseResponseVM.createNewSuccessResponseWithMsg("Change Certificate PIN successfully!"));
        } catch (ApplicationException e) {
            log.error(e.getMessage());
            message = e.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(e.getCode(), null, e.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage());
            message = e.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        } finally {
            asyncTransactionService.newThread("/api/certificate/changeCertPIN", TransactionType.BUSINESS, Action.MODIFY, Extension.CERT, Method.POST,
                status, message, AccountUtils.getLoggedAccount());
        }
    }

    @GetMapping("/get")
    public ResponseEntity<BaseResponseVM> getCertificate(String pin, String serial) {
        try {
            CertificateDTO certificateDTO = certificateService.getBySerial(serial);
            CryptoTokenProxy cryptoTokenProxy = cryptoTokenProxyFactory.resolveCryptoTokenProxy(certificateDTO, pin);
            return ResponseEntity.ok(BaseResponseVM.createNewSuccessResponse(cryptoTokenProxy.getBase64Certificate()));
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return ResponseEntity.ok(BaseResponseVM.createNewErrorResponse("Can not found certificate"));
        }
    }
}
