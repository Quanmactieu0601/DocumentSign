package vn.easyca.signserver.core.services;

import com.google.common.base.Strings;
import javafx.application.Application;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.easyca.signserver.core.domain.CertificateDTO;
import vn.easyca.signserver.core.dto.ImportP12FileDTO;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.core.exception.CertificateAppException;
import vn.easyca.signserver.core.factory.CryptoTokenProxyFactory;
import vn.easyca.signserver.pki.cryptotoken.CryptoToken;
import vn.easyca.signserver.pki.cryptotoken.impl.P12CryptoToken;
import vn.easyca.signserver.core.domain.TokenInfo;
import vn.easyca.signserver.core.utils.CertUtils;
import vn.easyca.signserver.pki.cryptotoken.error.*;
import vn.easyca.signserver.webapp.config.SystemDbConfiguration;
import vn.easyca.signserver.webapp.domain.Certificate;
import vn.easyca.signserver.webapp.domain.UserEntity;
import vn.easyca.signserver.webapp.repository.CertificateRepository;
import vn.easyca.signserver.webapp.repository.UserRepository;
import vn.easyca.signserver.webapp.security.AuthenticatorTOTPService;
import vn.easyca.signserver.webapp.service.CertificateService;
import vn.easyca.signserver.webapp.service.SystemConfigCachingService;
import vn.easyca.signserver.webapp.service.UserApplicationService;
import vn.easyca.signserver.webapp.service.dto.CertRequestInfoDTO;
import vn.easyca.signserver.webapp.utils.DateTimeUtils;
import vn.easyca.signserver.webapp.utils.MappingHelper;
import vn.easyca.signserver.webapp.utils.ParserUtils;
import vn.easyca.signserver.webapp.utils.SymmetricEncryptors;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.server.ExportException;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.*;

@Service
public class P12ImportService {
    private final Log log = LogFactory.getLog(P12ImportService.class);

    private final CertificateService certificateService;
    private final UserApplicationService userApplicationService;
    private final SymmetricEncryptors symmetricService;
    private final CertificateRepository certificateRepository;
    private final SystemConfigCachingService systemConfigCachingService;
    private final AuthenticatorTOTPService authenticatorTOTPService;
    private final CertificateGenerateService certificateGenerateService;
    private final CryptoTokenProxyFactory cryptoTokenProxyFactory;

    @Autowired
    public P12ImportService(CertificateService certificateService, UserApplicationService userApplicationService,
                            SymmetricEncryptors symmetricService, CertificateRepository certificateRepository, SystemConfigCachingService systemConfigCachingService, AuthenticatorTOTPService authenticatorTOTPService, CertificateGenerateService certificateGenerateService, UserRepository userRepository, CryptoTokenProxyFactory cryptoTokenProxyFactory) {
        this.certificateService = certificateService;
        this.userApplicationService = userApplicationService;
        this.symmetricService = symmetricService;
        this.certificateRepository = certificateRepository;
        this.systemConfigCachingService = systemConfigCachingService;
        this.authenticatorTOTPService = authenticatorTOTPService;
        this.certificateGenerateService = certificateGenerateService;
        this.cryptoTokenProxyFactory = cryptoTokenProxyFactory;
    }

    public CertificateDTO insert(ImportP12FileDTO input) throws ApplicationException {
        SystemDbConfiguration dbConfiguration = systemConfigCachingService.getConfig();
        P12CryptoToken p12CryptoToken = new P12CryptoToken();
        try {
            p12CryptoToken.initPkcs12(input.getP12Base64(), input.getPin());
        } catch (InitCryptoTokenException e) {
            throw new CertificateAppException(e);
        }
        String alias = null;
        try {
            alias = getAlias(input.getAliasName(), p12CryptoToken);
        } catch (CryptoTokenException e) {
            throw new CertificateAppException("Can not get alias from certificate", e);
        }

        X509Certificate x509Certificate = null;
        try {
            x509Certificate = (X509Certificate) p12CryptoToken.getCertificate(alias);
        } catch (KeyStoreException e) {
            throw new CertificateAppException("certificate has error", e);
        }
        String serial = x509Certificate.getSerialNumber().toString(16);
        Optional<Certificate> certBySerial = certificateRepository.findOneBySerialAndActiveStatus(serial, Certificate.ACTIVATED);
        if (certBySerial.isPresent())
            throw new ApplicationException(-1, "Certificate is already exist");
        String base64Cert = null;
        try {
            base64Cert = CertUtils.encodeBase64X509(x509Certificate);
        } catch (CertificateEncodingException e) {
            throw new CertificateAppException("certificate encoding exception", e);
        }
        CertificateDTO certificateDTO = new CertificateDTO();
        certificateDTO.setRawData(base64Cert);
        certificateDTO.setOwnerId(input.getOwnerId());
        certificateDTO.setSerial(serial);
        certificateDTO.setAlias(alias);
        certificateDTO.setTokenType(CertificateDTO.PKCS_12);
        certificateDTO.setSubjectInfo(x509Certificate.getSubjectDN().toString());
        // Lưu thêm mã pin khi tạo cert
        if (dbConfiguration.getSaveTokenPassword())
            certificateDTO.setEncryptedPin(symmetricService.encrypt(input.getPin()));

        certificateDTO.setSecretKey(authenticatorTOTPService.generateEncryptedTOTPKey());

        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setData(input.getP12Base64());
        certificateDTO.setTokenInfo(tokenInfo);
        certificateDTO.setValidDate(DateTimeUtils.convertToLocalDateTime(x509Certificate.getNotBefore()));
        certificateDTO.setExpiredDate(DateTimeUtils.convertToLocalDateTime(x509Certificate.getNotAfter()));
        certificateDTO.setActiveStatus(1);

        CertificateDTO result = certificateService.save(certificateDTO);

        try {
            userApplicationService.createUser(input.getOwnerId(), input.getOwnerId(), input.getOwnerId());
        } catch (Exception ignored) {
            log.error("Create user error" + input.getOwnerId(), ignored);
        }
        return result;
    }

    public CertificateDTO insertP12(ImportP12FileDTO input) throws ApplicationException {
        Optional<UserEntity> userEntity = userApplicationService.getUserWithAuthoritiesByLogin(input.getOwnerId());
        boolean check = userEntity.isPresent();
        if (!check) throw new ApplicationException("Don't have valid account");

        P12CryptoToken p12CryptoToken = new P12CryptoToken();
        try {
            p12CryptoToken.initPkcs12(input.getP12Base64(), input.getPin());
        } catch (InitCryptoTokenException e) {
            throw new CertificateAppException(e);
        }
        String alias = null;
        try {
            alias = getAlias(input.getAliasName(), p12CryptoToken);
        } catch (CryptoTokenException e) {
            throw new CertificateAppException("Can not get alias from certificate", e);
        }

        X509Certificate x509Certificate = null;
        try {
            x509Certificate = (X509Certificate) p12CryptoToken.getCertificate(alias);
        } catch (KeyStoreException e) {
            throw new CertificateAppException("certificate has error", e);
        }
        String serial = x509Certificate.getSerialNumber().toString(16);
        Optional<Certificate> certBySerial = certificateRepository.findOneBySerialAndActiveStatus(serial, Certificate.ACTIVATED);
        if (certBySerial.isPresent())
            throw new ApplicationException(-1, "Certificate is already exist");
        String base64Cert = null;
        try {
            base64Cert = CertUtils.encodeBase64X509(x509Certificate);
        } catch (CertificateEncodingException e) {
            throw new CertificateAppException("certificate encoding exception", e);
        }
        CertificateDTO certificateDTO = new CertificateDTO();
        certificateDTO.setSubjectInfo(x509Certificate.getSubjectDN().toString());
        certificateDTO.setRawData(base64Cert);
        certificateDTO.setOwnerId(input.getOwnerId());
        certificateDTO.setSerial(serial);
        certificateDTO.setAlias(alias);
        certificateDTO.setTokenType(CertificateDTO.PKCS_12);
        // Lưu thêm mã pin khi tạo cert
        certificateDTO.setEncryptedPin(symmetricService.encrypt(input.getPin()));
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setData(input.getP12Base64());
        certificateDTO.setTokenInfo(tokenInfo);
        certificateDTO.setValidDate(DateTimeUtils.convertToLocalDateTime(x509Certificate.getNotBefore()));
        certificateDTO.setExpiredDate(DateTimeUtils.convertToLocalDateTime(x509Certificate.getNotAfter()));
        certificateDTO.setActiveStatus(1);

        // Set cmnd cho cts
        String identificationRegex = "CMND:([^,]+)";
        String personalId = ParserUtils.getElementContentNameInCertificate(x509Certificate.getSubjectDN().toString(), identificationRegex);
        if(personalId != null) {
            certificateDTO.setPersonalId(personalId);
        }
        certificateDTO.setPersonalId(personalId);
        CertificateDTO result = certificateService.save(certificateDTO);
        return result;
    }

    private String getAlias(String inputAlias, P12CryptoToken cryptoToken) throws CryptoTokenException {
        if (inputAlias != null && !inputAlias.isEmpty())
            return inputAlias;
        List<String> aliases = cryptoToken.getAliases();
        if (aliases != null && aliases.size() > 0)
            return aliases.get(0);
        throw new CryptoTokenException("Can not found alias in crypto token");
    }

    public byte[] importListCerts(InputStream inputStream) throws ApplicationException, IOException {
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        int rows = sheet.getPhysicalNumberOfRows();
        DataFormatter formatter = new DataFormatter(Locale.US);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        for (int i = 2; i < rows; i++) {
            Row row = sheet.getRow(i);
            try {
                if (row != null) {
                    boolean isP12 = Strings.isNullOrEmpty(row.getCell(14).getStringCellValue());
                    if (isP12) {
                        String pin = formatter.formatCellValue(row.getCell(18, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK));
                        String base64Certificate = formatter.formatCellValue(row.getCell(15, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)).replaceAll("\n", "");
                        Optional<UserEntity> userId = userApplicationService.getUserWithAuthorities();
                        ImportP12FileDTO p12ImportVM = new ImportP12FileDTO();
                        p12ImportVM.setOwnerId(userId.get().getLogin());
                        p12ImportVM.setPin(pin);
                        p12ImportVM.setP12Base64(base64Certificate);
                        ImportP12FileDTO serviceInput = MappingHelper.map(p12ImportVM, ImportP12FileDTO.class);

                        CertificateDTO certificateDTO = this.insertP12(serviceInput);
                        row.createCell(19).setCellValue("Imported successfully ");

                    } else {
                        String certValue = row.getCell(15).getStringCellValue();
                        String alias = row.getCell(13).getStringCellValue();
                        String ownerId = userApplicationService.getUserWithAuthorities().get().getLogin();
                        CryptoToken cryptoToken = cryptoTokenProxyFactory.resolveP11Token(null);
                        certificateGenerateService.saveAndInstallCert(certValue, alias, ownerId, cryptoToken);
                        row.createCell(19).setCellValue("Imported successfully ");
                    }
                }
            } catch (Exception ex ) {
                row.createCell(19).setCellValue("Imported error");
                row.createCell(20).setCellValue(ex.getCause().getMessage());
            }
        }

        try {
            workbook.write(bos);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } finally {
            bos.close();
        }
        return bos.toByteArray();
    }
}
