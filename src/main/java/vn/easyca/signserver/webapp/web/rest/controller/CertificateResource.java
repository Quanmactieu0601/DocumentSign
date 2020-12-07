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
import vn.easyca.signserver.core.services.P12ImportService;
import vn.easyca.signserver.core.services.CertificateGenerateService;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/api/certificate")
@ComponentScan("vn.easyca.signserver.core.services")
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


    @PostMapping("/importResource")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public void importResource() {
        final File folder = new File("C:\\Users\\ADMIN\\Desktop\\certificates");
        String CMND = "";
        String PIN = "";
        List<CertImportSuccessDTO> importSuccessList = new ArrayList<>();
        List<CertImportErrorDTO> importErrorList = new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            try {
                CMND = fileEntry.getName().split(".p12")[0].split("_")[0];
                PIN = fileEntry.getName().split(".p12")[0].split("_")[1];
            } catch (Exception e) {
                log.error(e.getMessage(), e);
//                CertImportErrorDTO.c(fileEntry.getName() + "-" + e.getMessage()); continue;
                importErrorList.add(new CertImportErrorDTO(fileEntry.getName(), e.getMessage()));
            }

            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                String base64Certificate = encodeCertificateToBase64(fileEntry);

                Optional<UserEntity> userId = userApplicationService.getUserWithAuthorities();
                ImportP12FileDTO p12ImportVM = new ImportP12FileDTO();
                p12ImportVM.setOwnerId(userId.get().getLogin());
                p12ImportVM.setPin(PIN);
                p12ImportVM.setP12Base64(base64Certificate);

                try {
                    Long idCertificate = p12ImportService.insert(p12ImportVM).getId();
                    importSuccessList.add(new CertImportSuccessDTO(idCertificate.toString(), CMND));
                } catch (ApplicationException e) {
                    log.error(e.getMessage(), e);
                    importErrorList.add(new CertImportErrorDTO(fileEntry.getName(), e.getMessage()));
                    continue;
                }
            }
        }
        try {
//            FileOIHelper.writeFileLine(listCMND_ID,"D://outSuccess.txt");
//            FileOIHelper.writeFileLine(listCMND_ID_Error,"D://outError.txt");
            Gson gson = new Gson();
            String jsonSuccerss = gson.toJson(importSuccessList);
            FileOIHelper.writeFileLine(jsonSuccerss, "D://outSuccess.txt");

            String jsonError = gson.toJson(importSuccessList);
            FileOIHelper.writeFileLine(jsonSuccerss, "D://outError.txt");
//            FileOIHelper.writeFileLine(listCMND_ID_Error,"D://outError.txt");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @PostMapping("/importImage")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public void importImage() {
        Optional<UserEntity> userEntity = userApplicationService.getUserWithAuthorities();
        Long userId = userEntity.get().getId();
        List<CertImportErrorDTO> importErrors = new ArrayList<>();
        Gson gson = new Gson();
        String certFilePath = "C:\\Users\\ThanhLD\\Downloads\\outSuccess.txt";
        String imgFolderPath = "E:\\Document\\EasyCA\\SignServer\\BVQ11_Data\\ImageSignature\\Mix";
        File imgFolder = new File(imgFolderPath);
        String jsonCertSuccessMapping = null;
        try {
            jsonCertSuccessMapping = new String(Files.readAllBytes(Paths.get(certFilePath)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        CertImportSuccessDTO[] importSuccessList = gson.fromJson(jsonCertSuccessMapping, CertImportSuccessDTO[].class);
        Map<String, String> mapImage = new HashMap<>();
        for (File fileEntry : imgFolder.listFiles()) {
            String filePath = fileEntry.getPath();
            String fileName = fileEntry.getName().substring(0, fileEntry.getName().indexOf(".")).trim();
            try {
                String b64Image = Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(filePath)));
                mapImage.put(fileName, b64Image);
            } catch (IOException e) {
                importErrors.add(new CertImportErrorDTO(fileName, "Khong get duoc base64"));
            }

        }
        for (CertImportSuccessDTO cert : importSuccessList) {
            if (mapImage.containsKey(cert.getPersonIdentity().trim())) {
                try {
                    Optional<Certificate> certificateOptional = certificateService.findOne(Long.parseLong(cert.getCertId()));
                    if (certificateOptional.isPresent()) {
                        Certificate certificate = certificateOptional.get();
                        if (certificate.getSignatureImageId() == null) {
                            String b64Img = mapImage.get(cert.getPersonIdentity().trim());
                            SignatureImage signatureImage = new SignatureImage();
                            signatureImage.setImgData(b64Img);
                            signatureImage.setUserId(userId);
                            SignatureImageDTO dto = signatureImageMapper.toDto(signatureImage);
                            dto = signatureImageService.save(dto);

                            certificate.setSignatureImageId(dto.getId());
                            certificateService.saveOrUpdate(certificate);
                        }

                    }
                } catch (Exception e) {
                    importErrors.add(new CertImportErrorDTO(cert.getPersonIdentity().trim(), "Loi khi luu anh va cert"));
                }
            } else {
                importErrors.add(new CertImportErrorDTO(cert.getPersonIdentity().trim(), "Khong ton tai anh"));
            }
        }
        try {
            String jsonError = gson.toJson(importErrors);
            FileOIHelper.writeFileLine(jsonError, "D://outError.txt");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void listFilesForFolder(final File folder) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                System.out.println(fileEntry.getName());
            }
        }
    }

    private static String encodeCertificateToBase64(File file) {
        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            return Base64.getEncoder().encodeToString(fileContent);
        } catch (IOException e) {
            throw new IllegalStateException("could not read file " + file, e);
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
