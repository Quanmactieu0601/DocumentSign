package vn.easyca.signserver.core.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import vn.easyca.signserver.core.interfaces.CertificateRequester;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.core.factory.CryptoTokenProxyException;
import vn.easyca.signserver.core.factory.CryptoTokenProxyFactory;
import vn.easyca.signserver.core.utils.CommonUtils;
import vn.easyca.signserver.webapp.repository.CertificateRepository;
import vn.easyca.signserver.webapp.domain.UserEntity;
import vn.easyca.signserver.webapp.repository.UserRepository;
import vn.easyca.signserver.pki.cryptotoken.HsmConfig;
import vn.easyca.signserver.pki.cryptotoken.error.*;
import vn.easyca.signserver.pki.cryptotoken.CryptoToken;
import vn.easyca.signserver.pki.cryptotoken.utils.CSRGenerator;
import vn.easyca.signserver.core.domain.*;
import vn.easyca.signserver.core.dto.*;
import vn.easyca.signserver.webapp.service.UserApplicationService;
import vn.easyca.signserver.webapp.service.mapper.CertificateMapper;
import vn.easyca.signserver.webapp.utils.CertificateEncryptionHelper;
import vn.easyca.signserver.webapp.utils.DateTimeUtils;
import vn.easyca.signserver.webapp.web.rest.vm.request.CsrGeneratorVM;
import vn.easyca.signserver.webapp.web.rest.vm.request.sign.CsrsGeneratorVM;

import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CertificateGenerateService {
    private final Logger log = LoggerFactory.getLogger(CertificateGenerateService.class);

    private static final int CERT_TYPE = 2;

    private static final String CERT_METHOD = "SOFT_TOKEN";

    final CertificateRequester certificateRequester;
    final UserApplicationService userApplicationService;
    final CertificateRepository certificateRepository;
    private final UserRepository userRepository;
    private final CryptoTokenProxyFactory cryptoTokenProxyFactory;
    private final HsmConfig hsmConfig;
    private final CertificateMapper mapper;

    public CertificateGenerateService(CertificateRequester certificateRequester,
                                      UserApplicationService userApplicationService,
                                      CertificateRepository certificateRepository,
                                      UserRepository userRepository,
                                      CryptoTokenProxyFactory cryptoTokenProxyFactory, HsmConfig hsmConfig,
                                      CertificateMapper mapper) {
        this.certificateRequester = certificateRequester;
        this.userApplicationService = userApplicationService;
        this.certificateRepository = certificateRepository;
        this.userRepository = userRepository;
        this.cryptoTokenProxyFactory = cryptoTokenProxyFactory;
        this.hsmConfig = hsmConfig;
        this.mapper = mapper;
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

    private CertificateGenerateResult.Cert createCert(CertificateGenerateDTO dto) throws
        CertificateRequester.CertificateRequesterException,
        CryptoTokenException,
        CSRGenerator.CSRGeneratorException, CryptoTokenProxyException, ApplicationException {
//        String alias = CommonUtils.genRandomAlias();
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
        CertificateDTO certificateDTO = saveNewCertificate(rawCertificate, alias, dto.getSubjectDN().toString(), cryptoToken);
        return new CertificateGenerateResult.Cert(certificateDTO.getSerial(), certificateDTO.getRawData());
    }

    private CertificateDTO saveNewCertificate(RawCertificate rawCertificate,
                                              String alias,
                                              String subjectInfo,
                                              CryptoToken cryptoToken) throws ApplicationException {

        String certB64 = rawCertificate.getCert();
        String serial = rawCertificate.getSerial();
        X509Certificate x509Certificate = null;
        x509Certificate = CommonUtils.decodeBase64X509(certB64);
        if (x509Certificate == null)
            throw new ApplicationException("Cannot init X509Certificate from cert with serial: " + serial);
        CertificateDTO certificateDTO = new CertificateDTO();
        certificateDTO.setRawData(certB64);
        certificateDTO.setSerial(serial);
        certificateDTO.setSubjectInfo(subjectInfo);
        certificateDTO.setTokenType(CertificateDTO.PKCS_11);
        certificateDTO.setAlias(alias);
        certificateDTO.setOwnerId(alias);
        certificateDTO.setModifiedDate(new Date());
        certificateDTO.setValidDate(DateTimeUtils.convertToLocalDateTime(x509Certificate.getNotBefore()));
        certificateDTO.setExpiredDate(DateTimeUtils.convertToLocalDateTime(x509Certificate.getNotAfter()));
        certificateDTO.setActiveStatus(1);
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
        entity = certificateRepository.save(entity);
        return certificateDTO;
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
//        String alias = CommonUtils.genRandomAlias();
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
//        String alias = CommonUtils.genRandomAlias();
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

    /**
     * Tạo cert từ CSR
     *
     * @param dto
     * @return
     * @throws CryptoTokenException
     */
    private CertificateGenerateResult.Cert createCertFromCSR(CertificateGenerateDTO dto) throws
        CryptoTokenException, CryptoTokenProxyException, ApplicationException {
//        String alias = CommonUtils.genRandomAlias();
        String alias = dto.getOwnerId();
        CryptoToken cryptoToken = cryptoTokenProxyFactory.resolveP11Token(null);
        RawCertificate rawCertificate = dto.getRawCertificate();
        CertificateDTO certificateDTO = saveNewCertificate(rawCertificate, alias, dto.getSubjectDN().toString(), cryptoToken);
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
            CertificateGenerateDTO certificateGenerateDTO = new CertificateGenerateDTO(user.getOrganizationUnit(),
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
                    CertificateGenerateDTO certificateGenerateDTO = new CertificateGenerateDTO(user.getOrganizationUnit(),
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
                // TODO: viet lai ham luu cert
                saveNewCertificate(new RawCertificate(dto.getSerial(), dto.getCert()), dto.getOwnerId(), new SubjectDN(user.getCommonName(), user.getOrganizationUnit(),
                    user.getOrganizationName(), user.getLocalityName(), user.getStateName(), user.getCountry()).toString(), cryptoToken);
                //TODO: update csr status of user here
            }
        }
    }
}
