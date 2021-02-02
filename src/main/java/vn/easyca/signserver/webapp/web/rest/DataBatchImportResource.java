package vn.easyca.signserver.webapp.web.rest;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import vn.easyca.signserver.core.dto.ImportP12FileDTO;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.core.exception.CertificateAppException;
import vn.easyca.signserver.core.services.P12ImportService;
import vn.easyca.signserver.pki.cryptotoken.impl.P12CryptoToken;
import vn.easyca.signserver.pki.cryptotoken.error.CryptoTokenException;
import vn.easyca.signserver.pki.cryptotoken.error.InitCryptoTokenException;
import vn.easyca.signserver.pki.sign.utils.FileUtils;
import vn.easyca.signserver.webapp.domain.Certificate;
import vn.easyca.signserver.webapp.domain.SignatureImage;
import vn.easyca.signserver.webapp.domain.UserEntity;
import vn.easyca.signserver.webapp.security.AuthoritiesConstants;
import vn.easyca.signserver.webapp.service.*;
import vn.easyca.signserver.webapp.service.dto.CertImportErrorDTO;
import vn.easyca.signserver.webapp.service.dto.CertImportSuccessDTO;
import vn.easyca.signserver.webapp.service.dto.SignatureImageDTO;
import vn.easyca.signserver.webapp.service.mapper.SignatureImageMapper;
import vn.easyca.signserver.webapp.utils.ExcelUtils;
import vn.easyca.signserver.webapp.utils.FileIOHelper;
import vn.easyca.signserver.webapp.web.rest.vm.request.ImageFileImportVM;
import vn.easyca.signserver.webapp.web.rest.vm.response.BaseResponseVM;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/api/data")
public class DataBatchImportResource {
    private static final Logger log = LoggerFactory.getLogger(DataBatchImportResource.class);

    private final CertificateService certificateService;
    private final P12ImportService p12ImportService;
    private final SignatureImageService signatureImageService;
    private final UserApplicationService userApplicationService;
    private final SignatureImageMapper signatureImageMapper;

    public DataBatchImportResource(CertificateService certificateService, P12ImportService p12ImportService,
                                   SignatureImageService signatureImageService, UserApplicationService userApplicationService,
                                   SignatureImageMapper signatureImageMapper) {
        this.certificateService = certificateService;
        this.p12ImportService = p12ImportService;
        this.signatureImageService = signatureImageService;
        this.userApplicationService = userApplicationService;
        this.signatureImageMapper = signatureImageMapper;
    }

    @PostMapping("/importP12")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.SUPER_ADMIN + "\")")
    public void importResource(@RequestParam String absoluteFolderPath) {
        final File folder = new File(absoluteFolderPath);
        String CMND = "";
        String PIN = "";
        List<CertImportSuccessDTO> importSuccessList = new ArrayList<>();
        List<CertImportErrorDTO> importErrorList = new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            try {
                final String regex = "([^._]+)_([^._]+)";
                final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
                final Matcher matcher = pattern.matcher(fileEntry.getName());
                String[] infor = new String[3];

                while (matcher.find()) {
                    for (int i = 0; i <= matcher.groupCount(); i++) {
                        infor[i] = matcher.group(i);
                    }
                }
                CMND = infor[1];
                PIN = infor[2];
            } catch (Exception e) {
                log.error(e.getMessage(), e);
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
            Gson gson = new Gson();
            String jsonSuccerss = gson.toJson(importSuccessList);
            FileIOHelper.writeFile(jsonSuccerss, absoluteFolderPath + "/outSuccess.txt");

            String jsonError = gson.toJson(importErrorList);
            FileIOHelper.writeFile(jsonError, absoluteFolderPath + "/outError.txt");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }


    @PostMapping("/importP12FileSelected")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.SUPER_ADMIN + "\")")
    public ResponseEntity<Resource> importP12FileSelected(@RequestParam("files") MultipartFile[] files) {
        String CMND = "";
        String PIN = "";
        List<CertImportSuccessDTO> importSuccessList = new ArrayList<>();
        List<CertImportErrorDTO> importErrorList = new ArrayList<>();
        for (final MultipartFile fileEntry : files) {
            try {
//                final String regex = "([^._]+)_([^._]+)";
                final String regex = "([^._]+)(?:_|-)([^._]+)";
                final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
                final Matcher matcher = pattern.matcher(fileEntry.getOriginalFilename());
                String[] infor = new String[3];

                while (matcher.find()) {
                    for (int i = 0; i <= matcher.groupCount(); i++) {
                        infor[i] = matcher.group(i);
                    }
                }
                CMND = infor[1].trim();
                PIN = infor[2].trim();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                importErrorList.add(new CertImportErrorDTO(fileEntry.getOriginalFilename(), e.getMessage()));
            }


            String base64Certificate = "";
            try {
                base64Certificate = Base64.getEncoder().encodeToString(fileEntry.getBytes());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                importErrorList.add(new CertImportErrorDTO(fileEntry.getOriginalFilename(), e.getMessage()));
            }


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
                importErrorList.add(new CertImportErrorDTO(fileEntry.getOriginalFilename(), e.getMessage()));
                continue;
            }
        }
        try {
            Gson gson = new Gson();
            String jsonSuccerss = gson.toJson(importSuccessList);
            String jsonError = gson.toJson(importErrorList);

//            FileIOHelper.writeFile(jsonSuccerss, "D:/" + "/outSuccess.txt");
//            FileIOHelper.writeFile(jsonError, "D:/" + "/outError.txt");

            byte[] successFileByteContent = jsonSuccerss.getBytes();
            byte[] errorFileByteContent = jsonError.getBytes();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(baos);

            ZipEntry entry = new ZipEntry("successFile.txt");
            entry.setSize(successFileByteContent.length);

            ZipEntry entry1 = new ZipEntry("errorFile.txt");
            entry1.setSize(errorFileByteContent.length);

            zos.putNextEntry(entry);
            zos.write(successFileByteContent);

            zos.putNextEntry(entry1);
            zos.write(errorFileByteContent);
            zos.closeEntry();
            zos.close();

            byte[] zipData = baos.toByteArray();
            InputStreamResource file = new InputStreamResource(new ByteArrayInputStream(zipData));

            String filename = "EasyCA-CSR-Export";
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/zip"))
                .body(file);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @PostMapping("/importImage")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.SUPER_ADMIN + "\")")
    public void importImage(String certificateImportingResultPath, String imagePath) {
        Optional<UserEntity> userEntity = userApplicationService.getUserWithAuthorities();
        Long userId = userEntity.get().getId();
        List<CertImportErrorDTO> importErrors = new ArrayList<>();
        Gson gson = new Gson();
        String certFilePath = certificateImportingResultPath; //"C:\\Users\\ThanhLD\\Downloads\\outSuccess.txt";
        String imgFolderPath = imagePath; // "E:\\Document\\EasyCA\\SignServer\\BVQ11_Data\\ImageSignature\\Mix";
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
            FileIOHelper.writeFile(jsonError, imagePath + "/outError.txt");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }


    @PostMapping("/importImageSelected")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.SUPER_ADMIN + "\")")
    public ResponseEntity<Resource> importImageSelected(@RequestParam("imageFiles") MultipartFile[] imageFiles, @RequestParam("successFiles") MultipartFile[] successFiles) {
        Optional<UserEntity> userEntity = userApplicationService.getUserWithAuthorities();
        Long userId = userEntity.get().getId();
        List<CertImportErrorDTO> importErrors = new ArrayList<>();
        Gson gson = new Gson();

        String jsonCertSuccessMapping = null;
        try {
            jsonCertSuccessMapping = new String(successFiles[0].getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        CertImportSuccessDTO[] importSuccessList = gson.fromJson(jsonCertSuccessMapping, CertImportSuccessDTO[].class);
        Map<String, String> mapImage = new HashMap<>();
        for (MultipartFile fileEntry : imageFiles) {
//            String filePath = fileEntry.getPath();
            String fileName = fileEntry.getOriginalFilename().substring(0, fileEntry.getOriginalFilename().indexOf(".")).trim();
            try {
                String b64Image = Base64.getEncoder().encodeToString(fileEntry.getBytes());
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
                        } else {
                            importErrors.add(new CertImportErrorDTO(cert.getPersonIdentity().trim(), "Anh da duoc import"));
                        }
                    }
                } catch (Exception e) {
                    importErrors.add(new CertImportErrorDTO(cert.getPersonIdentity().trim(), "Loi khi luu anh va cert"));
                }
            } else {
                importErrors.add(new CertImportErrorDTO(cert.getPersonIdentity().trim(), "Khong ton tai anh"));
            }
        }

        String jsonError = gson.toJson(importErrors);
        InputStreamResource file = new InputStreamResource(new ByteArrayInputStream(jsonError.getBytes()));
        String filename = "EasyCA-CSR-Export";
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .contentType(MediaType.parseMediaType("application/zip"))
            .body(file);
    }

    @PostMapping("/exportSerial")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public void exportSerial(String p12FolderPath) {
        File folder = new File(p12FolderPath); // "E:\\Document\\EasyCA\\SignServer\\BVQ11_Data\\P12"
        String CMND = "";
        String PIN = "";
        List<String> result = new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            try {
                String regex = "([^._]+)_([^._]+)";
                Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
                Matcher matcher = pattern.matcher(fileEntry.getName());
                String[] infor = new String[3];

                while (matcher.find()) {
                    for (int i = 0; i <= matcher.groupCount(); i++) {
                        infor[i] = matcher.group(i);
                    }
                }
                CMND = infor[1].trim();
                PIN = infor[2].trim();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                String base64Certificate = encodeCertificateToBase64(fileEntry);
                try {
                    P12CryptoToken p12CryptoToken = new P12CryptoToken();
                    try {
                        p12CryptoToken.initPkcs12(base64Certificate, PIN);
                    } catch (InitCryptoTokenException e) {
                        throw new CertificateAppException(e);
                    }
                    X509Certificate x509Certificate = null;
                    try {
                        x509Certificate = (X509Certificate) p12CryptoToken.getCertificate(p12CryptoToken.getAliases().get(0));
                    } catch (KeyStoreException e) {
                        throw new CertificateAppException("certificate has error", e);
                    } catch (CryptoTokenException e) {
                        e.printStackTrace();
                    }
                    String serial = x509Certificate.getSerialNumber().toString(16);
                    result.add("thanhthanh" + CMND.trim() + "," + serial);
                } catch (ApplicationException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            FileIOHelper.writeFileLine(result, p12FolderPath + "/outSuccess.txt");
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
}
