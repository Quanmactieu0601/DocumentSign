package vn.easyca.signserver.core.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import vn.easyca.signserver.core.interfaces.CertificateRequester;
import vn.easyca.signserver.core.interfaces.CryptoTokenConnector;
import vn.easyca.signserver.core.interfaces.UserCreator;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.core.repository.CertificateRepository;
import vn.easyca.signserver.infrastructure.database.jpa.entity.UserEntity;
import vn.easyca.signserver.infrastructure.database.jpa.repository.UserRepository;
import vn.easyca.signserver.pki.cryptotoken.Config;
import vn.easyca.signserver.pki.cryptotoken.error.*;
import vn.easyca.signserver.pki.cryptotoken.CryptoToken;
import vn.easyca.signserver.pki.cryptotoken.utils.CSRGenerator;
import vn.easyca.signserver.core.domain.*;
import vn.easyca.signserver.core.dto.*;
import vn.easyca.signserver.webapp.web.rest.vm.request.CsrGeneratorVM;
import vn.easyca.signserver.webapp.web.rest.vm.request.sign.CsrsGeneratorVM;


import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CertificateGenerateService {
    private final Logger log = LoggerFactory.getLogger(CertificateGenerateService.class);

    private static final int CERT_TYPE = 2;

    private static final String CERT_METHOD = "SOFT_TOKEN";

    final CryptoTokenConnector cryptoTokenConnector;
    final CertificateRequester certificateRequester;
    final UserCreator userCreator;
    final CertificateRepository certificateRepository;
    private final UserRepository userRepository;

    public CertificateGenerateService(CryptoTokenConnector cryptoTokenConnector,
                                      CertificateRequester certificateRequester,
                                      UserCreator userCreator,
                                      CertificateRepository certificateRepository,
                                      UserRepository userRepository
    ) {
        this.cryptoTokenConnector = cryptoTokenConnector;
        this.certificateRequester = certificateRequester;
        this.userCreator = userCreator;
        this.certificateRepository = certificateRepository;
        this.userRepository = userRepository;
    }


    public CertificateGenerateResult genCertificate(CertificateGenerateDTO dto) throws ApplicationException {
        CertificateGenerateResult result = new CertificateGenerateResult();
        // create new cert.
        try {
            result.setCert(createCert(dto));
        } catch (CryptoTokenConnector.CryptoTokenConnectorException | CertificateRequester.CertificateRequesterException e) {
            throw ApplicationException.throwServerInternalError("can not create new certificate. check log for know detail reason", e);
        } catch (CryptoTokenException e) {
            throw ApplicationException.throwCryptoTokenError(e);
        } catch (CSRGenerator.CSRGeneratorException e) {
            throw ApplicationException.throwGenCSRError(e);
        }
        // create new user
        try {
            result.setUser(createUser(dto));
        } catch (UserCreator.UserCreatorException e) {
            log.error("Can not create user: cn is" + dto.getCn(), e);
        }
        return result;
    }

    private CertificateGenerateResult.Cert createCert(CertificateGenerateDTO dto) throws
        CertificateRequester.CertificateRequesterException,
        CryptoTokenException,
        CSRGenerator.CSRGeneratorException,
        CryptoTokenConnector.CryptoTokenConnectorException {
        String alias = dto.getOwnerId();
        CryptoToken cryptoToken = cryptoTokenConnector.getToken();
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
        Certificate certificate = saveNewCertificate(rawCertificate, alias, dto.getSubjectDN().toString(), cryptoToken);
        return new CertificateGenerateResult.Cert(certificate.getSerial(), certificate.getRawData());
    }

    private Certificate saveNewCertificate(RawCertificate rawCertificate,
                                           String alias,
                                           String subjectInfo,
                                           CryptoToken cryptoToken) throws CryptoTokenException {
        Certificate certificate = new Certificate();
        certificate.setRawData(rawCertificate.getCert());
        certificate.setSerial(rawCertificate.getSerial());
        certificate.setSubjectInfo(subjectInfo);
        certificate.setTokenType(Certificate.PKCS_11);
        certificate.setAlias(alias);
        certificate.setOwnerId(alias);
        certificate.setModifiedDate(new Date());
        Config cfg = cryptoToken.getConfig();
        TokenInfo tokenInfo = new TokenInfo()
            .setName(cfg.getName());
        if (cfg.getSlot() != null && !cfg.getSlot().isEmpty())
            tokenInfo.setSlot(Long.parseLong(cfg.getSlot()));
        tokenInfo.setPassword(cfg.getModulePin());
        tokenInfo.setLibrary(cfg.getLibrary());
        if (cfg.getAttributes() != null)
            tokenInfo.setP11Attrs(cfg.getAttributes());
        certificate.setTokenInfo(tokenInfo);
        certificateRepository.save(certificate);
        return certificate;
    }

    private CertificateGenerateResult.User createUser(CertificateGenerateDTO dto) throws UserCreator.UserCreatorException {
        String username = dto.getOwnerId();
        String password = dto.getPassword();
        if (password == null || password.isEmpty())
            password = username; // TODO: change to random password
        int createdUserResult = userCreator.CreateUser(username, password, dto.getOwnerName());
        return createdUserResult == UserCreator.RESULT_CREATED ?
            new CertificateGenerateResult.User(username, password, createdUserResult) :
            new CertificateGenerateResult.User(username, null, createdUserResult);
    }

    /**
     * Tạo CSR từ thông tin KH
     * Keypair được lưu vào HSM khi sinh CSR
     *
     * @param dtoUserRepository
     * @return
     * @throws Exception
     */
    public String createCSR(CertificateGenerateDTO dto) throws Exception {
        String alias = dto.getOwnerId();
        CryptoToken cryptoToken = cryptoTokenConnector.getToken();
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
     * @throws CryptoTokenConnector.CryptoTokenConnectorException
     */
    private CertificateGenerateResult.Cert createCertFromCSR(CertificateGenerateDTO dto) throws
        CryptoTokenException,
        CryptoTokenConnector.CryptoTokenConnectorException {
        String alias = dto.getOwnerId();
        CryptoToken cryptoToken = cryptoTokenConnector.getToken();
        RawCertificate rawCertificate = dto.getRawCertificate();
        Certificate certificate = saveNewCertificate(rawCertificate, alias, dto.getSubjectDN().toString(), cryptoToken);
        return new CertificateGenerateResult.Cert(certificate.getSerial(), certificate.getRawData());
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
                user.getLocalityName(), user.getOrganizationName(), user.getStateName(), user.getCountry(), user.getCommonName(), user.getOwnerId(), dto.getKeyLen());
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
        CryptoToken cryptoToken = cryptoTokenConnector.getToken();
        CertDTO certDto = null;
        String crs = null;
        for (Long userId : dto.getUserIds()) {
            Optional<UserEntity> userEntityOptional = userRepository.findById(userId);
            if (userEntityOptional.isPresent()) {
                UserEntity user = userEntityOptional.get();
                try {
                    CertificateGenerateDTO certificateGenerateDTO = new CertificateGenerateDTO(user.getOrganizationUnit(),
                        user.getLocalityName(), user.getOrganizationName(), user.getStateName(), user.getCountry(), user.getCommonName(), user.getOwnerId(), keyLength);
                    crs = createCSR(cryptoToken, certificateGenerateDTO);
                    certDto = new CertDTO(userId, user.getOwnerId(), crs, null);
                    //TODO: update csr status of user here
                } catch (Exception ex) {
                    certDto = new CertDTO(userId, user.getOwnerId(), ex.getMessage());
                }
            } else
                certDto = new CertDTO(userId, null, "Tài khoản không tồn tại");
            result.add(certDto);
        }
        return result;
    }


    public void saveCerts(List<CertDTO> dtos) throws
        CryptoTokenConnector.CryptoTokenConnectorException, CryptoTokenException {
        CryptoToken cryptoToken = cryptoTokenConnector.getToken();
        for (CertDTO dto : dtos) {
            Optional<UserEntity> userEntityOptional = userRepository.findOneByOwnerId(dto.getOwnerId());
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
