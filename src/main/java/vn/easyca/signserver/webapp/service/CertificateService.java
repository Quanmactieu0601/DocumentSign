package vn.easyca.signserver.webapp.service;

import com.google.common.base.Strings;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.easyca.signserver.core.domain.CertificateDTO;
import vn.easyca.signserver.core.domain.TokenInfo;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.core.exception.CertificateNotFoundAppException;
import vn.easyca.signserver.core.factory.CryptoTokenProxy;
import vn.easyca.signserver.core.factory.CryptoTokenProxyFactory;
import vn.easyca.signserver.core.utils.CertUtils;
import vn.easyca.signserver.pki.sign.utils.X509Utils;
import vn.easyca.signserver.webapp.config.SystemDbConfiguration;
import vn.easyca.signserver.webapp.domain.*;
import vn.easyca.signserver.webapp.domain.Certificate;
import vn.easyca.signserver.webapp.enm.SignatureTemplateParserType;
import vn.easyca.signserver.webapp.repository.CertificateRepository;
import vn.easyca.signserver.webapp.repository.SignatureImageRepository;
import vn.easyca.signserver.webapp.repository.SignatureTemplateRepository;
import vn.easyca.signserver.webapp.repository.UserRepository;
import vn.easyca.signserver.webapp.security.AuthenticatorTOTPService;
import vn.easyca.signserver.webapp.security.SecurityUtils;
import vn.easyca.signserver.webapp.service.mapper.CertificateMapper;
import vn.easyca.signserver.webapp.utils.*;
import vn.easyca.signserver.webapp.service.parser.SignatureTemplateParseService;
import vn.easyca.signserver.webapp.service.parser.SignatureTemplateParserFactory;
import vn.easyca.signserver.webapp.utils.AccountUtils;
import vn.easyca.signserver.webapp.utils.CertificateEncryptionHelper;
import vn.easyca.signserver.webapp.utils.FileIOHelper;
import vn.easyca.signserver.webapp.utils.ParserUtils;
import vn.easyca.signserver.webapp.web.rest.CertificateResource;
import vn.easyca.signserver.webapp.web.rest.vm.request.CreateCertRsRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

@Service
public class CertificateService {

    private List<Certificate> certificateList = new ArrayList<>();
    private final CertificateRepository certificateRepository;
    private final CertificateMapper mapper;
    private final SignatureTemplateRepository signatureTemplateRepository;
    private final SignatureImageRepository signatureImageRepository;
    private final CryptoTokenProxyFactory cryptoTokenProxyFactory;
    private final AuthenticatorTOTPService authenticatorTOTPService;
    private final SystemConfigCachingService systemConfigCachingService;
    private final SymmetricEncryptors symmetricService;
    private final Environment env;
    private final UserRepository userRepository;
    private final SignatureTemplateParserFactory signatureTemplateParserFactory;
    private final FileResourceService fileResourceService;

    private UserApplicationService userApplicationService;

    public CertificateService(CertificateRepository certificateRepository, CertificateMapper mapper, SignatureTemplateRepository signatureTemplateRepository,
                              SignatureImageRepository signatureImageRepository, CryptoTokenProxyFactory cryptoTokenProxyFactory,
                              AuthenticatorTOTPService authenticatorTOTPService, SystemConfigCachingService systemConfigCachingService,
                              SymmetricEncryptors symmetricService, Environment env, UserRepository userRepository,
                              SignatureTemplateParserFactory signatureTemplateParserFactory,
                              FileResourceService fileResourceService, UserApplicationService userApplicationService) {
        this.certificateRepository = certificateRepository;
        this.mapper = mapper;
        this.signatureTemplateRepository = signatureTemplateRepository;
        this.signatureImageRepository = signatureImageRepository;
        this.cryptoTokenProxyFactory = cryptoTokenProxyFactory;
        this.authenticatorTOTPService = authenticatorTOTPService;
        this.systemConfigCachingService = systemConfigCachingService;
        this.symmetricService = symmetricService;
        this.env = env;
        this.userRepository = userRepository;
        this.signatureTemplateParserFactory = signatureTemplateParserFactory;
        this.fileResourceService = fileResourceService;
        this.userApplicationService = userApplicationService;
    }

    public List<Certificate> getByOwnerId(String ownerId) throws ApplicationException {
        Long id = Long.parseLong(ownerId);
        Optional<UserEntity> userEntityOptional = userRepository.findById(id);
        if (!userEntityOptional.isPresent())
            throw new ApplicationException(String.format("User is not exist - ownerId %s", ownerId));
        UserEntity userEntity = userEntityOptional.get();
        Set<Authority> userAuthority = userEntity.getAuthorities();
        if (userAuthority.stream().anyMatch(ua -> "ROLE_ADMIN".equals(ua.getName()) || "ROLE_SUPER_ADMIN".equals(ua.getName()))) {
            certificateList = certificateRepository.findAll();
        } else {
            certificateList = certificateRepository.findByOwnerId(ownerId);
        }
        return certificateList;
    }

    public CertificateDTO getBySerial(String serial) throws CertificateNotFoundAppException {
        Optional<Certificate> certificate = certificateRepository.findOneBySerialAndActiveStatus(serial, Certificate.ACTIVATED);
        CertificateDTO certificateDTO = null;
        if (certificate.isPresent()) {
            certificateDTO = mapper.map(certificate.get());
        }
        if (certificateDTO == null)
            throw new CertificateNotFoundAppException();
        return certificateDTO;
    }

    @Transactional(readOnly = true)
    public Page<Certificate> findAll(Pageable pageable) {
        return certificateRepository.findAll(pageable);
    }

    @Transactional
    public void updateActiveStatus(long id) {
        Certificate certificate = certificateRepository.getOne(id);
        if (certificate.getActiveStatus() == 1) {
            certificate.setActiveStatus(0);
        } else {
            certificate.setActiveStatus(1);
        }
        certificateRepository.save(certificate);
    }

    @Transactional
    public void updateOwnerId(String ownerId, long id) {
        Certificate certificate = certificateRepository.getOne(id);
        certificate.setOwnerId(ownerId);
        certificateRepository.save(certificate);
    }

    @Transactional
    public void updateSignTurn(String serial, Integer signedCurrentCount) {
        if (signedCurrentCount > 0) {
            Certificate certificate = certificateRepository.findOneBySerialAndActiveStatus(serial, Certificate.ACTIVATED).get();
            certificate.setSignedTurnCount(certificate.getSignedTurnCount() + signedCurrentCount);
            certificateRepository.save(certificate);
        }
    }

    @Transactional
    public void updateSignedTurn(String serial) {
        Certificate certificate = certificateRepository.findOneBySerialAndActiveStatus(serial, Certificate.ACTIVATED).get();
        certificate.setSignedTurnCount(certificate.getSignedTurnCount() + 1);
        certificateRepository.save(certificate);
    }

    public boolean checkEnoughSigningCountRemain(int signingCountRemain, int signingProfile, int numSignature) {
        if (signingProfile == -1) {
            return true;
        } else {
            int numSignatureToSign = signingCountRemain + numSignature;
            if ((signingCountRemain < signingProfile) && (numSignatureToSign <= signingProfile)) {
                return true;
            }
            return false;
        }
    }


    @Transactional(readOnly = true)
    public Page<Certificate> findByFilter(Pageable pageable, String alias, String ownerId, String serial, String validDate, String expiredDate, Integer type) {
        Optional<UserEntity> userEntityOptional = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin().get());
        if (userEntityOptional.isPresent()) {
            Set<Authority> userAuthority = userEntityOptional.get().getAuthorities();
            boolean isAdmin = userAuthority.stream().anyMatch(ua -> "ROLE_ADMIN".equals(ua.getName()) || "ROLE_SUPER_ADMIN".equals(ua.getName()));
            if (!isAdmin) {
                ownerId = userEntityOptional.get().getLogin();
            }
        }
        return certificateRepository.findByFilter(pageable, alias, ownerId, serial, validDate, expiredDate, type);
    }

    @Transactional
    public CertificateDTO save(CertificateDTO certificateDTO) {
        Certificate entity = mapper.map(certificateDTO);
        certificateRepository.save(entity);
        return mapper.map(entity);
    }

    public String getSignatureImage(String serial, String pin) throws ApplicationException {
        Optional<Certificate> certificateOptional = certificateRepository.findOneBySerialAndActiveStatus(serial, Certificate.ACTIVATED);
        if (!certificateOptional.isPresent())
            throw new ApplicationException("Certificate is not found");
        CertificateDTO certificateDTO = mapper.map(certificateOptional.get());
        CryptoTokenProxy cryptoTokenProxy = cryptoTokenProxyFactory.resolveCryptoTokenProxy(certificateDTO, pin);
        cryptoTokenProxy.getCryptoToken().checkInitialized();
        Optional<UserEntity> userEntity = userRepository.findOneWithAuthoritiesByLogin(AccountUtils.getLoggedAccount());
        Optional<SignatureTemplate> signatureTemplateOptional = signatureTemplateRepository.findFirstByUserIdOrderByCreatedDateDesc(userEntity.get().getId());
        if (!signatureTemplateOptional.isPresent()) {
            throw new ApplicationException("Signature template is not configured");
        }
        SignatureTemplate signatureTemplate = signatureTemplateOptional.get();
        Long signImageId = certificateDTO.getSignatureImageId();
        String signatureImageData = "";
        String htmlTemplate = signatureTemplate.getHtmlTemplate();
        if (signImageId != null) {
            Optional<SignatureImage> signatureImage = signatureImageRepository.findById(signImageId);
            if (signatureImage.isPresent())
                signatureImageData = signatureImage.get().getImgData();
        }
        X509Certificate x509Certificate = cryptoTokenProxy.getX509Certificate();
        String subjectDN = x509Certificate.getSubjectDN().getName();

        SignatureTemplateParseService signatureTemplateParseService = signatureTemplateParserFactory.resolve(signatureTemplate.getCoreParser());
        String htmlContent = signatureTemplateParseService.buildSignatureTemplate(subjectDN, htmlTemplate, signatureImageData,null);
        Integer width = signatureTemplate.getWidth();
        Integer height = signatureTemplate.getHeight();
        return ParserUtils.convertHtmlContentToImageByProversion(htmlContent, width, height, signatureTemplate.getTransparency(), env);
    }

    public String getSignatureImage(String serial, String pin, Object data) throws ApplicationException {
        Optional<Certificate> certificateOptional = certificateRepository.findOneBySerialAndActiveStatus(serial, Certificate.ACTIVATED);
        if (!certificateOptional.isPresent())
            throw new ApplicationException("Certificate is not found");
        CertificateDTO certificateDTO = mapper.map(certificateOptional.get());
        CryptoTokenProxy cryptoTokenProxy = cryptoTokenProxyFactory.resolveCryptoTokenProxy(certificateDTO, pin);
        cryptoTokenProxy.getCryptoToken().checkInitialized();
        Optional<UserEntity> userEntity = userRepository.findOneWithAuthoritiesByLogin(AccountUtils.getLoggedAccount());
        Optional<SignatureTemplate> signatureTemplateOptional = signatureTemplateRepository.findOneByUserIdAndActivated(userEntity.get().getId(),true);
        if (!signatureTemplateOptional.isPresent()) {
            throw new ApplicationException("Signature template is not configured");
        }
        SignatureTemplate signatureTemplate = signatureTemplateOptional.get();

        Long signImageId = certificateDTO.getSignatureImageId();
        String signatureImageData = "";
        String htmlTemplate = signatureTemplate.getHtmlTemplate();
        if (signImageId != null) {
            Optional<SignatureImage> signatureImage = signatureImageRepository.findById(signImageId);
            if (signatureImage.isPresent())
                signatureImageData = signatureImage.get().getImgData();
        }
        X509Certificate x509Certificate = cryptoTokenProxy.getX509Certificate();
        String subjectDN = x509Certificate.getSubjectDN().getName();

        SignatureTemplateParseService signatureTemplateParseService = signatureTemplateParserFactory.resolve(signatureTemplate.getCoreParser());
        String htmlContent = signatureTemplateParseService.buildSignatureTemplate(subjectDN, htmlTemplate, signatureImageData, data);
        Integer width = signatureTemplate.getWidth();
        Integer height = signatureTemplate.getHeight();
        return ParserUtils.convertHtmlContentToImageByProversion(htmlContent, width, height, signatureTemplate.getTransparency(), env);
    }


    public String getSignatureImageByTemplateId(String serial, String pin, Long templateId) throws ApplicationException, IOException {
        Optional<Certificate> certificateOptional = certificateRepository.findOneBySerialAndActiveStatus(serial, Certificate.ACTIVATED);
        if (!certificateOptional.isPresent())
            throw new ApplicationException("Certificate is not found");
        CertificateDTO certificateDTO = mapper.map(certificateOptional.get());
        CryptoTokenProxy cryptoTokenProxy = cryptoTokenProxyFactory.resolveCryptoTokenProxy(certificateDTO, pin);
        cryptoTokenProxy.getCryptoToken().checkInitialized();
        Optional<UserEntity> userEntity = userRepository.findOneWithAuthoritiesByLogin(AccountUtils.getLoggedAccount());

        String htmlContent = "";
        Integer width = 180;
        Integer height = 125;
        boolean isTransparency = false;
        String signatureImageData = "";
        X509Certificate x509Certificate = cryptoTokenProxy.getX509Certificate();
        String subjectDN = x509Certificate.getSubjectDN().getName();
        Long signImageId = certificateDTO.getSignatureImageId();
        if (signImageId != null) {
            Optional<SignatureImage> signatureImage = signatureImageRepository.findById(signImageId);
            if (signatureImage.isPresent()) {
                signatureImageData = signatureImage.get().getImgData();
            }
        }

        Long DEFAULT_OPTION = 0L;
        if (templateId == DEFAULT_OPTION | templateId == null) {
            try (InputStream inputFileStream = fileResourceService.getTemplateFile("/templates/signature/signatureTemplate_2.0.html")) {
                htmlContent = IOUtils.toString(inputFileStream, StandardCharsets.UTF_8.name());
                SignatureTemplateParseService signatureTemplateParseService = signatureTemplateParserFactory.resolve(SignatureTemplateParserType.DEFAULT);
                htmlContent = signatureTemplateParseService.buildSignatureTemplate(subjectDN, htmlContent, signatureImageData, null);

                // th: ko co anh chu ky, thay doi kich thuoc anh
                if (signatureImageData.equals("")) {
                    height = 70;
                    htmlContent = htmlContent.replaceFirst("class=\"hand-sign\"", "class=\"hand-sign\" hidden");
                }

                return ParserUtils.convertHtmlContentToImageByProversion(htmlContent, width, height, isTransparency, env);
            } catch (IOException ioe) {
                throw new ApplicationException("Error reading file");
            }
        }

        Optional<SignatureTemplate> signatureTemplateOptional = signatureTemplateRepository.findById(templateId);
        if (!signatureTemplateOptional.isPresent()) {
            throw new ApplicationException("Signature template is not configured");
        }
        SignatureTemplate signatureTemplate = signatureTemplateOptional.get();
        String htmlTemplate = signatureTemplate.getHtmlTemplate();
        SignatureTemplateParseService signatureTemplateParseService = signatureTemplateParserFactory.resolve(signatureTemplate.getCoreParser());


        htmlContent = signatureTemplateParseService.buildSignatureTemplate(subjectDN, htmlTemplate, signatureImageData, null);
        width = signatureTemplate.getWidth();
        height = signatureTemplate.getHeight();
        return ParserUtils.convertHtmlContentToImageByProversion(htmlContent, width, height, signatureTemplate.getTransparency(), env);
    }


    public Optional<Certificate> findOne(Long id) {
        return certificateRepository.findById(id);
    }

    @Transactional
    public void saveOrUpdate(Certificate certificate) {
        certificateRepository.save(certificate);
    }

    public String getBase64OTPQRCode(String serial, String pin) throws ApplicationException {
        Optional<Certificate> certificateOptional = certificateRepository.findOneBySerialAndActiveStatus(serial, Certificate.ACTIVATED);
        if (!certificateOptional.isPresent())
            throw new ApplicationException(String.format("Certificate is not exist - serial: %s", serial));
        CertificateDTO certificateDTO = mapper.map(certificateOptional.get());
        CryptoTokenProxy cryptoTokenProxy = cryptoTokenProxyFactory.resolveCryptoTokenProxy(certificateDTO, pin);
        cryptoTokenProxy.getCryptoToken().checkInitialized();
        String urlQrCode = authenticatorTOTPService.getQRCodeFromEncryptedSecretKey(serial, certificateOptional.get().getSecretKey());
        try {
            return FileIOHelper.getBase64EncodedImage(urlQrCode);
        } catch (IOException e) {
            throw new ApplicationException("Can't convert URL to base64 image", e);
        }
    }

    // TODO: Call this function to change P12 password
    public void changePIN(String serial, String oldPin, String newPin, String otp) throws ApplicationException {
        try {
            if (StringUtils.isBlank(serial) || StringUtils.isBlank(oldPin) || StringUtils.isBlank(newPin)) {
                throw new ApplicationException("Please enter required info (serial | old PIN | new PIN)");
            } else if (oldPin.equals(newPin)) {
                throw new ApplicationException("new PIN have to different old PIN");
            }
            SystemDbConfiguration dbConfiguration = systemConfigCachingService.getConfig();
            Optional<Certificate> certificateOptional = certificateRepository.findOneBySerialAndActiveStatus(serial, Certificate.ACTIVATED);
            if (!certificateOptional.isPresent())
                throw new ApplicationException("Certificate is not found");
            CertificateDTO certificateDTO = mapper.map(certificateOptional.get());
            CryptoTokenProxy cryptoTokenProxy = cryptoTokenProxyFactory.resolveCryptoTokenProxy(certificateDTO, oldPin, otp);
            cryptoTokenProxy.getCryptoToken().checkInitialized();
            if (CertificateDTO.PKCS_11.equals(certificateDTO.getTokenType())) {
                // TODO: change PIN HSM
                certificateDTO.setEncryptedPin(symmetricService.encrypt(newPin));
            } else {
                KeyStore newKs = KeyStore.getInstance("PKCS12");
                newKs.load(null, null);

                KeyStore currentKs = cryptoTokenProxy.getCryptoToken().getKeyStore();
                Enumeration<String> aliases = currentKs.aliases();

                while (aliases.hasMoreElements()) {
                    String alias = aliases.nextElement();
                    Key privateKey = currentKs.getKey(alias, oldPin.toCharArray());
                    java.security.cert.Certificate[] certificateChain = currentKs.getCertificateChain(alias);
                    newKs.setKeyEntry(alias, privateKey, newPin.toCharArray(), certificateChain);
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                newKs.store(baos, newPin.toCharArray());
                byte[] output = baos.toByteArray();
                X509Certificate x509Certificate = (X509Certificate) newKs.getCertificate(certificateDTO.getAlias());

                String base64Cert = CertUtils.encodeBase64X509(x509Certificate);

                certificateDTO.setRawData(base64Cert);
                TokenInfo tokenInfo = new TokenInfo();
                tokenInfo.setData(Base64.getEncoder().encodeToString(output));
                certificateDTO.setTokenInfo(tokenInfo);
                if (dbConfiguration.getSaveTokenPassword())
                    certificateDTO.setEncryptedPin(symmetricService.encrypt(newPin));
            }
            this.save(certificateDTO);
        } catch (IOException e) {
            throw new ApplicationException("IOException", e);
        } catch (NoSuchAlgorithmException e) {
            throw new ApplicationException("NoSuchAlgorithmException", e);
        } catch (UnrecoverableKeyException e) {
            throw new ApplicationException("UnrecoverableKeyException", e);
        } catch (CertificateEncodingException e) {
            throw new ApplicationException("CertificateEncodingException", e);
        } catch (KeyStoreException | CertificateException e) {
            throw new ApplicationException("KeyStoreException | CertificateException", e);
        }
    }

    public void updateSignatureImageInCert(Long signatureImageId, Long certId) {
        certificateRepository.updateSignatureImageInCert(signatureImageId, certId);
    }


    public String resetHsmCertificatePin(String serial, String masterKey) throws Exception {
        if (StringUtils.isBlank(serial) || StringUtils.isBlank(masterKey)) {
            throw new Exception("Serial and MasterKey must be not null!");
        }
        String masterKeySystem = env.getProperty("spring.servlet.master-key");
        if (!StringUtils.isBlank(masterKeySystem) && !masterKeySystem.equals(masterKeySystem)) {
            throw new Exception("MasterKey invalid !");
        }
        Optional<Certificate> certificateOptional = certificateRepository.findOneBySerial(serial);
        if (!certificateOptional.isPresent()) {
            throw new Exception("Certificate is not found!");
        }
        CertificateDTO certificate = mapper.map(certificateOptional.get());
        if (!certificate.getTokenType().equals(CertificateDTO.PKCS_11)) {
            throw new Exception("Certificate token type is invalid!");
        }
        String newPin = CommonUtils.genRandomHsmCertPin();
        try {
            certificate.setEncryptedPin(symmetricService.encrypt(newPin));
            this.save(certificate);
            return newPin;
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

    public List<Certificate> getCertificateListByUserLogin() throws Exception {
        Optional<UserEntity> currentLoginUser = userApplicationService.getUserWithAuthorities();
        if(!currentLoginUser.isPresent()){
            throw new Exception("User could not be found");
        } else{
            UserEntity user = currentLoginUser.get();
            List<Certificate> list = certificateRepository.findByOwnerId(user.getLogin());
            return list;
        }
    }
    public CertificateDTO createCertFromRa(CreateCertRsRequest request) throws Exception {
        try{
            CertificateDTO certificateDTO = new CertificateDTO();
            X509Certificate certificate = X509Utils.StringToX509Certificate(request.getRawData());
            certificateDTO.setSubjectInfo(certificate.getSubjectDN().toString());
            certificateDTO.setRawData(request.getRawData());
            certificateDTO.setSerial(request.getSerial());
            certificateDTO.setValidDate(DateTimeUtils.convertToLocalDateTime(certificate.getNotBefore()));
            certificateDTO.setExpiredDate(DateTimeUtils.convertToLocalDateTime(certificate.getNotAfter()));
            certificateDTO.setActiveStatus(1);
            certificateDTO.setAuthMode(request.getAuthMode());
            certificateDTO.setType(1);
            certificateDTO.setSingingProfile(request.getSigningCount());
            String identificationRegex = "CMND:([^,]+)";
            String personalId = ParserUtils.getElementContentNameInCertificate(certificate.getSubjectDN().toString(), identificationRegex);
            if(personalId != null) {
                certificateDTO.setPersonalId(personalId);
            }
            certificateDTO.setPersonalId(personalId);
            CertificateDTO result = this.save(certificateDTO);
            return result;
        }catch (Exception ex){
            throw new Exception(ex.getMessage());
        }
    }

}
