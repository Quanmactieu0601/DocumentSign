package vn.easyca.signserver.core.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.easyca.signserver.core.interfaces.CertificateRequester;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.core.factory.CryptoTokenProxyException;
import vn.easyca.signserver.core.factory.CryptoTokenProxyFactory;
import vn.easyca.signserver.core.utils.CertUtils;
import vn.easyca.signserver.pki.cryptotoken.utils.Pkcs12Utils;
import vn.easyca.signserver.ra.lib.dto.RegisterInputDto;
import vn.easyca.signserver.ra.lib.dto.RegisterResultDto;
import vn.easyca.signserver.webapp.repository.CertificateRepository;
import vn.easyca.signserver.webapp.domain.UserEntity;
import vn.easyca.signserver.webapp.repository.UserRepository;
import vn.easyca.signserver.webapp.config.HsmConfig;
import vn.easyca.signserver.pki.cryptotoken.error.*;
import vn.easyca.signserver.pki.cryptotoken.CryptoToken;
import vn.easyca.signserver.pki.cryptotoken.utils.CSRGenerator;
import vn.easyca.signserver.core.domain.*;
import vn.easyca.signserver.core.dto.*;
import vn.easyca.signserver.webapp.security.AuthenticatorTOTPService;
import vn.easyca.signserver.webapp.service.UserApplicationService;
import vn.easyca.signserver.webapp.service.dto.CertRequestInfoDTO;
import vn.easyca.signserver.webapp.service.mapper.CertificateMapper;
import vn.easyca.signserver.webapp.utils.*;
import vn.easyca.signserver.webapp.web.rest.vm.request.CsrGeneratorVM;
import vn.easyca.signserver.webapp.web.rest.vm.request.P12ImportVM;
import vn.easyca.signserver.webapp.web.rest.vm.request.sign.CsrsGeneratorVM;
import vn.easyca.signserver.webapp.web.rest.vm.response.P12CertificateRegisterResult;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.*;

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
    private final P12ImportService p12ImportService;

    public CertificateGenerateService(CertificateRequester certificateRequester,
                                      UserApplicationService userApplicationService,
                                      CertificateRepository certificateRepository,
                                      UserRepository userRepository,
                                      CryptoTokenProxyFactory cryptoTokenProxyFactory, HsmConfig hsmConfig,
                                      CertificateMapper mapper, AuthenticatorTOTPService authenticatorTOTPService, SymmetricEncryptors symmetricService, P12ImportService p12ImportService) {
        this.certificateRequester = certificateRequester;
        this.userApplicationService = userApplicationService;
        this.certificateRepository = certificateRepository;
        this.userRepository = userRepository;
        this.cryptoTokenProxyFactory = cryptoTokenProxyFactory;
        this.hsmConfig = hsmConfig;
        this.mapper = mapper;
        this.authenticatorTOTPService = authenticatorTOTPService;
        this.symmetricService = symmetricService;
        this.p12ImportService = p12ImportService;
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


    public List<P12CertificateRegisterResult> genCertificates(List<CertificateGenerateDTO> dtos) throws ApplicationException {
        List<P12CertificateRegisterResult> result = new ArrayList<>();
        // create new cert.
        try {
            result = createP12Cert(dtos);
        } catch (CertificateRequester.CertificateRequesterException e) {
            throw ApplicationException.throwServerInternalError("can not create new certificate. check log for know detail reason", e);
        } catch (CryptoTokenException e) {
            throw ApplicationException.throwCryptoTokenError(e);
        } catch (CSRGenerator.CSRGeneratorException e) {
            throw ApplicationException.throwGenCSRError(e);
        } catch (CryptoTokenProxyException e) {
            throw new ApplicationException(-1, "Cannot resolve token");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception exception) {
            throw new ApplicationException("can not create new p12");
        }
        return result;
    }


//    private CertificateGenerateResult.Cert createCert(CertificateGenerateDTO dto) throws
//        CertificateRequester.CertificateRequesterException,
//        CryptoTokenException,
//        CSRGenerator.CSRGeneratorException, CryptoTokenProxyException, ApplicationException {
////        String alias = CertUtils.genRandomAlias();
//        String alias = dto.getOwnerId();
//        CryptoToken cryptoToken = cryptoTokenProxyFactory.resolveP11Token(null);
//        KeyPair keyPair = cryptoToken.genKeyPair(alias, dto.getKeyLen());
//        String csr = new CSRGenerator().genCsr(
//            dto.getSubjectDN().toString(),
//            cryptoToken.getProviderName(),
//            keyPair.getPrivate(),
//            keyPair.getPublic(),
//            null,
//            false,
//            false);
//        RawCertificate rawCertificate = certificateRequester.request(csr, dto.getCertPackage(CERT_METHOD, CERT_TYPE), dto.getSubjectDN(), dto.getOwnerInfo());
//        CertificateDTO certificateDTO = saveAndInstallCert(rawCertificate.getCert(), alias, alias, cryptoToken);
//        return new CertificateGenerateResult.Cert(certificateDTO.getSerial(), certificateDTO.getRawData());
//    }


    private CertificateGenerateResult.Cert createCert(CertificateGenerateDTO dto) throws
        CertificateRequester.CertificateRequesterException,
        CryptoTokenException,
        CSRGenerator.CSRGeneratorException, CryptoTokenProxyException, ApplicationException {
//        String alias = CertUtils.genRandomAlias();
        String alias = dto.getOwnerId();
        CryptoToken cryptoToken = cryptoTokenProxyFactory.resolveP11Token(null);
        KeyPair keyPair = cryptoToken.genKeyPair(alias, dto.getKeyLen());
        String csr = new CSRGenerator().genCsr(
            dto.getSubjectDN().toString(),
            cryptoToken.getProviderName(),
            keyPair.getPrivate(),
            keyPair.getPublic(),
            null,
            false,
            false);
        RawCertificate rawCertificate = certificateRequester.request(csr, dto.getCertPackage(CERT_METHOD, CERT_TYPE), dto.getSubjectDN(), dto.getOwnerInfo());
        CertificateDTO certificateDTO = saveAndInstallCert(rawCertificate.getCert(), alias, alias, cryptoToken);
        return new CertificateGenerateResult.Cert(certificateDTO.getSerial(), certificateDTO.getRawData());
    }

    private List<P12CertificateRegisterResult> createP12Cert(List<CertificateGenerateDTO> certificateGenerateDTOS) throws
        Exception {
        // xử lý p12
        List<RegisterInputDto> inputDtos = new ArrayList<>();
        Hashtable<String, KeyPair> keyPairInList = new Hashtable<>();
        for (CertificateGenerateDTO dto : certificateGenerateDTOS) {
            // tạo key pair
            CertPackage certPackage = dto.getCertPackage(CERT_METHOD, CERT_TYPE);
            SubjectDN subjectDN = dto.getSubjectDN();
            OwnerInfo ownerInfo = dto.getOwnerInfo();

            // lưu private key
            String key = dto.getTaxCode() + "_" + dto.getIdentification();
            KeyPair keyPair = Pkcs12Utils.createKeyPair(2048);
            keyPairInList.put(key, keyPair);
            String csr = Pkcs12Utils.createCSR(subjectDN.toString(), keyPair);
            RegisterInputDto registerInputDto = new RegisterInputDto();
            registerInputDto.setCsr(csr);
            registerInputDto.setCertMethod(certPackage.getCertMethod());
            registerInputDto.setCertProfile(certPackage.getCertProfile());
            registerInputDto.setCertProfileType(dto.getCertProfileType());
            registerInputDto.setCn(subjectDN.getCn());
            registerInputDto.setCustomerEmail(ownerInfo.getOwnerEmail());
            registerInputDto.setCustomerPhone(ownerInfo.getOwnerPhone());
            registerInputDto.setO(subjectDN.getO());
            registerInputDto.setOu(subjectDN.getOu());
            registerInputDto.setSt(subjectDN.getS());
            registerInputDto.setTaxCode(dto.getTaxCode());
            registerInputDto.setIdentification(dto.getIdentification());
            registerInputDto.genHash();
            inputDtos.add(registerInputDto);
        }
        List<RegisterResultDto> cers = certificateRequester.request(inputDtos);
        List<P12CertificateRegisterResult> p12CertificateImportedList = new ArrayList<>();
        // combine cer and private key
        for (RegisterResultDto result : cers) {
            if (result.getStatus() == RESULT_OK) {
                P12CertificateRegisterResult p12CertificateRegisterResult = saveP12ToDbFromResult(result, keyPairInList);
                p12CertificateImportedList.add(p12CertificateRegisterResult);
            } else {
                P12CertificateRegisterResult registerResult = MappingHelper.map(result, P12CertificateRegisterResult.class);
                p12CertificateImportedList.add(registerResult);
            }
        }
        return p12CertificateImportedList;
    }


    @Transactional
    public P12CertificateRegisterResult saveP12ToDbFromResult(RegisterResultDto certResult, Hashtable<String, KeyPair> keyPairInList) throws Exception {
        X509Certificate cert = CertUtils.decodeBase64X509(certResult.getCert());
        String alias = CommonUtils.getCN(cert);
        String ownerId = AccountUtils.getLoggedAccount();
        String key = certResult.getTaxCode() + "_" + certResult.getIdentification();
        KeyPair keyPairCer = keyPairInList.get(key);
        String pin = CommonUtils.genRandomHsmCertPin();
        String certData = certResult.getCert();
        byte[] p12Content = Pkcs12Utils.selfSignedCertificateToP12v2(keyPairCer, certData, alias, pin);

        String base64File = "";
        base64File = Base64.getEncoder().encodeToString(p12Content);
        P12ImportVM p12ImportVM = new P12ImportVM();
        p12ImportVM.setP12Base64(base64File);
        p12ImportVM.setOwnerId(ownerId);
        p12ImportVM.setPin(pin);

        log.info("importP12File: {}", p12ImportVM);
        ImportP12FileDTO serviceInput = MappingHelper.map(p12ImportVM, ImportP12FileDTO.class);
        CertificateDTO p12Cert = p12ImportService.insertP12(serviceInput);
        P12CertificateRegisterResult importedCert = new P12CertificateRegisterResult();
        importedCert.setSerial(p12Cert.getSerial());
        importedCert.setPin(pin);
        importedCert.setIdentification(certResult.getIdentification());
        importedCert.setTaxCode(certResult.getTaxCode());
        importedCert.setStatus(RESULT_OK);
        return importedCert;
    }

    @Transactional
    public CertificateDTO saveAndInstallCert(String certValue,
                                             String alias,
                                             String ownerId,
                                             CryptoToken cryptoToken) throws ApplicationException {
        try {
            X509Certificate x509Certificate = null;
            x509Certificate = CertUtils.decodeBase64X509(certValue);
            if (x509Certificate == null)
                throw new ApplicationException("Cannot init X509Certificate from cert - alias: " + alias);
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

            // Create random password for hsm certificate
            String rawPin = CommonUtils.genRandomHsmCertPin();
            certificateDTO.setRawPin(rawPin);
            certificateDTO.setEncryptedPin(symmetricService.encrypt(rawPin));

            TokenInfo tokenInfo = new TokenInfo()
                .setName(hsmConfig.getName());
            if (hsmConfig.getSlot() != null && !hsmConfig.getSlot().isEmpty())
                tokenInfo.setSlot(Long.parseLong(hsmConfig.getSlot()));
            tokenInfo.setPassword(hsmConfig.getModulePin());
            tokenInfo.setLibrary(hsmConfig.getLibrary());
            if (hsmConfig.getAttributes() != null)
                tokenInfo.setP11Attrs(hsmConfig.getAttributes());
            certificateDTO.setTokenInfo(tokenInfo);

            vn.easyca.signserver.webapp.domain.Certificate entity = mapper.map(certificateDTO);
            certificateRepository.save(entity);
            cryptoToken.installCert(alias, x509Certificate);
            return certificateDTO;
        } catch (Exception e) {
            throw new ApplicationException(ApplicationException.APPLICATION_ERROR_CODE, String.format("Install cert into HSM has error - alias: %s", alias), e);
        }
    }

    private CertificateGenerateResult.User createUser(CertificateGenerateDTO dto) throws ApplicationException {
        String username = dto.getOwnerId();
        String password = dto.getPassword();
        if (password == null || password.isEmpty())
            password = username; // TODO: change to random password
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
        String csr = new CSRGenerator().genCsr(
            dto.getSubjectDN().toString(),
            cryptoToken.getProviderName(),
            keyPair.getPrivate(),
            keyPair.getPublic(),
            null,
            false,
            false);
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
        String csr = new CSRGenerator().genCsr(
            dto.getSubjectDN().toString(),
            cryptoToken.getProviderName(),
            keyPair.getPrivate(),
            keyPair.getPublic(),
            null,
            false,
            false);
        return csr;
    }

    public String createCSR(CryptoToken cryptoToken, String alias, String subjectDN, int keyLength) throws CryptoTokenException, CSRGenerator.CSRGeneratorException {
//        String alias = CertUtils.genRandomAlias();
        KeyPair keyPair = cryptoToken.genKeyPair(alias, keyLength);
        String csr = new CSRGenerator().genCsr(
            subjectDN,
            cryptoToken.getProviderName(),
            keyPair.getPrivate(),
            keyPair.getPublic(),
            "SHA256withRSA",
            false,
            false);
        return csr;
    }

    /**
     * Tạo cert từ CSR
     *
     * @param dto
     * @return
     * @throws CryptoTokenException
     */
    private CertificateGenerateResult.Cert createCertFromCSR(CertificateGenerateDTO dto) throws
        CryptoTokenException, CryptoTokenProxyException, ApplicationException {
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
            CertificateGenerateDTO certificateGenerateDTO = new CertificateGenerateDTO(user.getOrganizationUnit(), null,
                user.getLocalityName(), user.getOrganizationName(), user.getStateName(), user.getCountry(), user.getCommonName(), user.getLogin(), dto.getKeyLen());
            result.setCsr(createCSR(certificateGenerateDTO));
//            result.setUser(CertificateGenerateResult.User(user.getLogin(), null, UserCreator.RESULT_EXIST));
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
                    CertificateGenerateDTO certificateGenerateDTO = new CertificateGenerateDTO(user.getOrganizationUnit(), null,
                        user.getLocalityName(), user.getOrganizationName(), user.getStateName(), user.getCountry(), user.getCommonName(), user.getLogin(), keyLength);
                    crs = createCSR(cryptoToken, certificateGenerateDTO);
                    certDto = new CertDTO(userId, user.getLogin(), crs, null);
                    //TODO: update csr status of user here
                } catch (Exception ex) {
                    certDto = new CertDTO(userId, user.getLogin(), ex.getMessage());
                }
            } else
                certDto = new CertDTO(userId, null, "Tài khoản không tồn tại");
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
     *
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
}
