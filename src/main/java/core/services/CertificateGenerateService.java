package core.services;

import core.domain.*;
import core.dto.*;
import core.exception.ApplicationException;
import core.factory.CryptoTokenProxyException;
import core.factory.CryptoTokenProxyFactory;
import core.interfaces.CertificateRequester;
import core.utils.CertUtils;
import java.rmi.ServerException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pki.cryptotoken.CryptoToken;
import pki.cryptotoken.error.*;
import pki.cryptotoken.utils.CSRGenerator;
import pki.cryptotoken.utils.Pkcs12Utils;
import pki.sign.utils.StringUtils;
import ra.lib.dto.RegisterInputDto;
import ra.lib.dto.RegisterResultDto;
import study.config.HsmConfig;
import study.domain.Certificate;
import study.domain.UserEntity;
import study.enm.SystemConfigKey;
import study.repository.CertificateRepository;
import study.repository.UserRepository;
import study.security.AuthenticatorTOTPService;
import study.service.CertificateService;
import study.service.SystemConfigService;
import study.service.UserApplicationService;
import study.service.dto.CertRequestInfoDTO;
import study.service.dto.SystemConfigDTO;
import study.service.mapper.CertificateMapper;
import study.utils.*;
import study.utils.CommonUtils;
import study.utils.DateTimeUtils;
import study.utils.ParserUtils;
import study.utils.SymmetricEncryptors;
import study.web.rest.vm.request.CsrGeneratorVM;
import study.web.rest.vm.request.P12ImportVM;
import study.web.rest.vm.request.sign.CsrsGeneratorVM;
import study.web.rest.vm.response.P12CertificateRegisterResult;

@Service
public class CertificateGenerateService {

    private final Logger log = LoggerFactory.getLogger(CertificateGenerateService.class);

    private static final int CERT_TYPE = 2;
    private final int RESULT_OK = 0;
    private final int RESULT_ERROR = 1;
    private static final String CERT_METHOD = "SOFT_TOKEN";

    final CertificateRequester certificateRequester;
    final UserApplicationService userApplicationService;
    final CertificateRepository certificateRepository;
    private final UserRepository userRepository;
    private final CryptoTokenProxyFactory cryptoTokenProxyFactory;
    private final HsmConfig hsmConfig;
    private final CertificateMapper mapper;
    private final AuthenticatorTOTPService authenticatorTOTPService;
    private final SymmetricEncryptors symmetricService;

    private final CertificateService certificateService;

    private final SystemConfigService systemConfigService;

    public CertificateGenerateService(
        CertificateRequester certificateRequester,
        UserApplicationService userApplicationService,
        CertificateRepository certificateRepository,
        UserRepository userRepository,
        CryptoTokenProxyFactory cryptoTokenProxyFactory,
        HsmConfig hsmConfig,
        CertificateMapper mapper,
        AuthenticatorTOTPService authenticatorTOTPService,
        SymmetricEncryptors symmetricService,
        CertificateService certificateService,
        SystemConfigService systemConfigService
    ) {
        this.certificateRequester = certificateRequester;
        this.userApplicationService = userApplicationService;
        this.certificateRepository = certificateRepository;
        this.userRepository = userRepository;
        this.cryptoTokenProxyFactory = cryptoTokenProxyFactory;
        this.hsmConfig = hsmConfig;
        this.mapper = mapper;
        this.authenticatorTOTPService = authenticatorTOTPService;
        this.symmetricService = symmetricService;
        this.certificateService = certificateService;
        this.systemConfigService = systemConfigService;
    }

    public CertificateGenerateResult genCertificate(CertificateGenerateDTO dto) throws ApplicationException {
        CertificateGenerateResult result = new CertificateGenerateResult();
        // create new cert.
        try {
            result.setCert(createCert(dto));
        } catch (CertificateRequester.CertificateRequesterException e) {
            throw ApplicationException.throwServerInternalError("can not create new certificate. check log for know detail reason", e);
        } catch (CryptoTokenException e) {
            throw ApplicationException.throwCryptoTokenError(e);
        } catch (CSRGenerator.CSRGeneratorException e) {
            throw ApplicationException.throwGenCSRError(e);
        } catch (CryptoTokenProxyException e) {
            throw new ApplicationException(-1, "Cannot resolve token");
        }
        // create new user
        try {
            result.setUser(createUser(dto));
        } catch (Exception e) {
            throw new ApplicationException(ApplicationException.APPLICATION_ERROR_CODE, "Create User fail");
        }
        return result;
    }

    private CertificateGenerateResult.Cert createCert(CertificateGenerateDTO dto)
        throws CertificateRequester.CertificateRequesterException, CryptoTokenException, CSRGenerator.CSRGeneratorException, CryptoTokenProxyException, ApplicationException {
        //        String alias = CertUtils.genRandomAlias();
        String alias = dto.getOwnerId();
        CryptoToken cryptoToken = cryptoTokenProxyFactory.resolveP11Token(null);
        KeyPair keyPair = cryptoToken.genKeyPair(alias, dto.getKeyLen());
        String csr = new CSRGenerator()
            .genCsr(
                dto.getSubjectDN().toString(),
                cryptoToken.getProviderName(),
                keyPair.getPrivate(),
                keyPair.getPublic(),
                null,
                false,
                false
            );
        RawCertificate rawCertificate = certificateRequester.request(
            csr,
            dto.getCertPackage(CERT_METHOD, CERT_TYPE),
            dto.getSubjectDN(),
            dto.getOwnerInfo()
        );
        CertificateDTO certificateDTO = saveAndInstallCert(rawCertificate.getCert(), alias, alias, cryptoToken);
        return new CertificateGenerateResult.Cert(certificateDTO.getSerial(), certificateDTO.getRawData());
    }

    @Transactional
    public CertificateDTO saveAndInstallCert(String certValue, String alias, String ownerId, CryptoToken cryptoToken)
        throws ApplicationException {
        try {
            X509Certificate x509Certificate = null;
            x509Certificate = CertUtils.decodeBase64X509(certValue);
            if (x509Certificate == null) throw new ApplicationException("Cannot init X509Certificate from cert - alias: " + alias);

            String serial = x509Certificate.getSerialNumber().toString(16);
            Optional<Certificate> certBySerial = certificateRepository.findOneBySerialAndActiveStatus(serial, Certificate.ACTIVATED);
            if (certBySerial.isPresent()) throw new ApplicationException(-1, "Certificate is already exist");

            CertificateDTO certificateDTO = new CertificateDTO();
            certificateDTO.setRawData(certValue);
            certificateDTO.setSerial(x509Certificate.getSerialNumber().toString(16));
            certificateDTO.setSubjectInfo(x509Certificate.getSubjectDN().toString());
            certificateDTO.setTokenType(CertificateDTO.PKCS_11);
            certificateDTO.setAlias(alias);
            certificateDTO.setOwnerId(ownerId);
            certificateDTO.setModifiedDate(new Date());
            certificateDTO.setValidDate(DateTimeUtils.convertToLocalDateTime(x509Certificate.getNotBefore()));
            certificateDTO.setExpiredDate(DateTimeUtils.convertToLocalDateTime(x509Certificate.getNotAfter()));
            certificateDTO.setActiveStatus(1);
            certificateDTO.setSecretKey(authenticatorTOTPService.generateEncryptedTOTPKey());

            String identificationRegex = "CMND:([^,]+)";
            String personalId = ParserUtils.getElementContentNameInCertificate(
                x509Certificate.getSubjectDN().toString(),
                identificationRegex
            );
            if (personalId != null) {
                certificateDTO.setPersonalId(personalId);
            }
            // Create random password for hsm certificate
            String rawPin = CommonUtils.genRandomHsmCertPin();
            certificateDTO.setRawPin(rawPin);
            certificateDTO.setEncryptedPin(symmetricService.encrypt(rawPin));

            TokenInfo tokenInfo = new TokenInfo().setName(hsmConfig.getName());
            if (hsmConfig.getSlot() != null && !hsmConfig.getSlot().isEmpty()) tokenInfo.setSlot(Long.parseLong(hsmConfig.getSlot()));
            tokenInfo.setPassword(hsmConfig.getModulePin());
            tokenInfo.setLibrary(hsmConfig.getLibrary());
            if (hsmConfig.getAttributes() != null) tokenInfo.setP11Attrs(hsmConfig.getAttributes());
            certificateDTO.setTokenInfo(tokenInfo);

            webapp.domain.Certificate entity = mapper.map(certificateDTO);
            certificateRepository.save(entity);
            cryptoToken.installCert(alias, x509Certificate);
            return certificateDTO;
        } catch (Exception e) {
            throw new ApplicationException(
                ApplicationException.APPLICATION_ERROR_CODE,
                String.format("Install cert into HSM has error - alias: %s", alias),
                e
            );
        }
    }

    private CertificateGenerateResult.User createUser(CertificateGenerateDTO dto) throws ApplicationException {
        String username = dto.getOwnerId();
        String password = dto.getPassword();
        if (password == null || password.isEmpty()) password = username; // TODO: change to random password
        boolean createdUserResult = userApplicationService.createUser(username, password, dto.getOwnerName());
        return new CertificateGenerateResult.User(username, password, createdUserResult);
    }

    /**
     * Tạo CSR từ thông tin KH
     * Keypair được lưu vào HSM khi sinh CSR
     *
     * @return
     * @throws Exception
     */
    public String createCSR(CertificateGenerateDTO dto) throws Exception {
        //        String alias = CertUtils.genRandomAlias();
        String alias = dto.getOwnerId();
        CryptoToken cryptoToken = cryptoTokenProxyFactory.resolveP11Token(null);
        KeyPair keyPair = cryptoToken.genKeyPair(alias, dto.getKeyLen());
        String csr = new CSRGenerator()
            .genCsr(
                dto.getSubjectDN().toString(),
                cryptoToken.getProviderName(),
                keyPair.getPrivate(),
                keyPair.getPublic(),
                null,
                false,
                false
            );
        return csr;
    }

    /**
     * Tạo CSR từ thông tin KH
     * Keypair được lưu vào HSM khi sinh CSR
     * connection truyền từ ngoài vào
     *
     * @param dto
     * @return
     * @throws Exception
     */
    public String createCSR(CryptoToken cryptoToken, CertificateGenerateDTO dto) throws Exception {
        //        String alias = CertUtils.genRandomAlias();
        String alias = dto.getOwnerId();
        KeyPair keyPair = cryptoToken.genKeyPair(alias, dto.getKeyLen());
        String csr = new CSRGenerator()
            .genCsr(
                dto.getSubjectDN().toString(),
                cryptoToken.getProviderName(),
                keyPair.getPrivate(),
                keyPair.getPublic(),
                null,
                false,
                false
            );
        return csr;
    }

    public String createCSR(CryptoToken cryptoToken, String alias, String subjectDN, int keyLength)
        throws CryptoTokenException, CSRGenerator.CSRGeneratorException {
        //        String alias = CertUtils.genRandomAlias();
        KeyPair keyPair = cryptoToken.genKeyPair(alias, keyLength);
        String csr = new CSRGenerator()
            .genCsr(subjectDN, cryptoToken.getProviderName(), keyPair.getPrivate(), keyPair.getPublic(), "SHA256withRSA", false, false);
        return csr;
    }

    /**
     * Tạo cert từ CSR
     *
     * @param dto
     * @return
     * @throws CryptoTokenException
     */
    private CertificateGenerateResult.Cert createCertFromCSR(CertificateGenerateDTO dto)
        throws CryptoTokenException, CryptoTokenProxyException, ApplicationException {
        //        String alias = CertUtils.genRandomAlias();
        String alias = dto.getOwnerId();
        CryptoToken cryptoToken = cryptoTokenProxyFactory.resolveP11Token(null);
        RawCertificate rawCertificate = dto.getRawCertificate();
        CertificateDTO certificateDTO = saveAndInstallCert(rawCertificate.getCert(), alias, alias, cryptoToken);
        return new CertificateGenerateResult.Cert(certificateDTO.getSerial(), certificateDTO.getRawData());
    }

    public CertificateGenerateResult saveUserAndCreateCSR(CertificateGenerateDTO dto) throws Exception {
        CertificateGenerateResult result = new CertificateGenerateResult();
        result.setCsr(createCSR(dto));
        result.setUser(createUser(dto));
        return result;
    }

    /**
     * Tạo csr từ user có sẵn
     *
     * @param dto
     * @return
     * @throws Exception
     */
    public CertificateGenerateResult createCSR(CsrGeneratorVM dto) throws Exception {
        CertificateGenerateResult result = new CertificateGenerateResult();
        Optional<UserEntity> userEntityOptional = userRepository.findById(dto.getUserId());
        if (userEntityOptional.isPresent()) {
            UserEntity user = userEntityOptional.get();
            CertificateGenerateDTO certificateGenerateDTO = new CertificateGenerateDTO(
                user.getOrganizationUnit(),
                null,
                user.getLocalityName(),
                user.getOrganizationName(),
                user.getStateName(),
                user.getCountry(),
                user.getCommonName(),
                user.getLogin(),
                dto.getKeyLen()
            );
            result.setCsr(createCSR(certificateGenerateDTO));
        }
        return result;
    }

    /**
     * Tạo nhiều csr từ list userid.
     *
     * @param dto
     * @return
     * @throws Exception
     */
    public List<CertDTO> createCSRs(CsrsGeneratorVM dto) throws Exception {
        List<CertDTO> result = new ArrayList<>();
        int keyLength = dto.getKeyLen();
        CryptoToken cryptoToken = cryptoTokenProxyFactory.resolveP11Token(null);
        CertDTO certDto = null;
        String crs = null;
        for (Long userId : dto.getUserIds()) {
            Optional<UserEntity> userEntityOptional = userRepository.findById(userId);
            if (userEntityOptional.isPresent()) {
                UserEntity user = userEntityOptional.get();
                try {
                    CertificateGenerateDTO certificateGenerateDTO = new CertificateGenerateDTO(
                        user.getOrganizationUnit(),
                        null,
                        user.getLocalityName(),
                        user.getOrganizationName(),
                        user.getStateName(),
                        user.getCountry(),
                        user.getCommonName(),
                        user.getLogin(),
                        keyLength
                    );
                    crs = createCSR(cryptoToken, certificateGenerateDTO);
                    certDto = new CertDTO(userId, user.getLogin(), crs, null);
                    //TODO: update csr status of user here
                } catch (Exception ex) {
                    certDto = new CertDTO(userId, user.getLogin(), ex.getMessage());
                }
            } else certDto = new CertDTO(userId, null, "Tài khoản không tồn tại");
            result.add(certDto);
        }
        return result;
    }

    public void saveCerts(List<CertDTO> dtos) throws CryptoTokenException, CryptoTokenProxyException, ApplicationException {
        CryptoToken cryptoToken = cryptoTokenProxyFactory.resolveP11Token(null);
        for (CertDTO dto : dtos) {
            Optional<UserEntity> userEntityOptional = userRepository.findOneByLogin(dto.getOwnerId());
            if (userEntityOptional.isPresent()) {
                UserEntity user = userEntityOptional.get();
                saveAndInstallCert(dto.getCert(), dto.getOwnerId(), dto.getOwnerId(), cryptoToken);
                //TODO: update csr status of user here
            }
        }
    }

    /**
     * Tạo private key ở HSM và CSR thông qua file upload chứa thông tin CTS
     * @param certRequestInfoDTOs
     * @throws Exception
     */
    public void generateBulkCSR(List<CertRequestInfoDTO> certRequestInfoDTOs) throws Exception {
        CryptoToken cryptoToken = cryptoTokenProxyFactory.resolveP11Token(null);
        int keyLength = 2048;
        for (CertRequestInfoDTO dto : certRequestInfoDTOs) {
            String alias = CommonUtils.genRandomAlias();
            String subjectDN = dto.getSubjectDN();
            String csr = createCSR(cryptoToken, alias, subjectDN, keyLength);
            dto.setAlias(alias);
            dto.setCsrValue(csr);
        }
    }

    /**
     * Lưu bản ghi certificate vào DB và cài đặt Cert (tương ứng với CSR đã tạo ở generateBulkCSR) vào HSM
     *
     * @param dtos
     * @param currentUser
     * @throws ApplicationException
     */
    public void installCertIntoHsm(List<CertRequestInfoDTO> dtos, String currentUser) throws ApplicationException {
        CryptoToken cryptoToken = cryptoTokenProxyFactory.resolveP11Token(null);
        for (CertRequestInfoDTO dto : dtos) {
            CertificateDTO result = saveAndInstallCert(dto.getCertValue(), dto.getAlias(), currentUser, cryptoToken);
            dto.setSerial(result.getSerial());
            dto.setPin(result.getRawPin());
        }
    }

    public boolean changePinCertForNoLoginUser(ChangePinUserRequest request) throws Exception {
        log.info("Change pin for hsm user not login, serial: {}", request.getSerial());
        String requestType = request.getRequestType();
        if (requestType.equals("request")) {
            String masterKey = request.getMasterKey();
            if (StringUtils.isNullOrEmpty(masterKey)) {
                throw new Exception("Master key request must be not null!");
            }
            Optional<SystemConfigDTO> masterKeyOptional = systemConfigService.findByComIdAndKey(1L, SystemConfigKey.MASTER_KEY);
            if (!masterKeyOptional.isPresent()) {
                throw new Exception("Master Key system must be not null");
            }
            SystemConfigDTO masterKeySystem = masterKeyOptional.get();
            String masterKeyFromSystem = masterKeySystem.getValue();
            if (StringUtils.isNullOrEmpty(masterKeyFromSystem)) {
                throw new Exception("Master key system not existed!");
            }
            if (!masterKey.equals(masterKeyFromSystem)) {
                throw new Exception("Master key invalid!");
            }
            return true;
        } else if (requestType.equals("confirm")) {
            try {
                certificateService.changePIN(request.getSerial(), request.getOldPin(), request.getNewPin(), null);
                return true;
            } catch (Exception ex) {
                throw new Exception(ex.getMessage());
            }
        } else {
            log.error("Request type invalid!");
            throw new Exception("Request type invalid!");
        }
    }
}
