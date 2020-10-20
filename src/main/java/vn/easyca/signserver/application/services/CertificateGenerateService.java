package vn.easyca.signserver.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import vn.easyca.signserver.application.dependency.CertificateRequester;
import vn.easyca.signserver.application.dependency.CryptoTokenConnector;
import vn.easyca.signserver.application.dependency.UserCreator;
import vn.easyca.signserver.application.exception.ApplicationException;
import vn.easyca.signserver.application.repository.CertificateRepository;
import vn.easyca.signserver.pki.cryptotoken.Config;
import vn.easyca.signserver.pki.cryptotoken.error.*;
import vn.easyca.signserver.pki.cryptotoken.CryptoToken;
import vn.easyca.signserver.pki.cryptotoken.utils.CSRGenerator;
import vn.easyca.signserver.application.domain.*;
import vn.easyca.signserver.application.dto.*;


import java.security.KeyPair;
import java.util.Date;

@Service
public class CertificateGenerateService {
    private final Logger log = LoggerFactory.getLogger(CertificateGenerateService.class);

    private final int CERT_TYPE = 2;

    private final String CERT_METHOD = "SOFT_TOKEN";

    final CryptoTokenConnector cryptoTokenConnector;
    final CertificateRequester certificateRequester;
    final UserCreator userCreator;
    final CertificateRepository certificateRepository;

    public CertificateGenerateService(CryptoTokenConnector cryptoTokenConnector, CertificateRequester certificateRequester, UserCreator userCreator, CertificateRepository certificateRepository) {
        this.cryptoTokenConnector = cryptoTokenConnector;
        this.certificateRequester = certificateRequester;
        this.userCreator = userCreator;
        this.certificateRepository = certificateRepository;
    }


    public CertificateGenerateResult genCertificate(CertificateGenerateDTO dto) throws ApplicationException {
        CertificateGenerateResult result = new CertificateGenerateResult();
        try {
            result.setCert(createCert(dto));
        } catch (CryptoTokenConnector.CryptoTokenConnectorException | CertificateRequester.CertificateRequesterException e) {
            throw ApplicationException.throwServerInternalError("can not create new certificate. check log for know detail reason", e);
        } catch (CryptoTokenException e) {
            throw ApplicationException.throwCryptoTokenError(e);
        } catch (CSRGenerator.CSRGeneratorException e) {
            throw ApplicationException.throwGenCSRError(e);
        }

        try {
            CertificateGenerateResult.User newUser = createUser(dto);
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
        KeyPair keyPair = null;
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
        if (cfg.getSlot() != null)
            tokenInfo.setSlot(Long.parseLong(cfg.getSlot()));
        tokenInfo.setPassword(cfg.getModulePin());
        tokenInfo.setLibrary(cfg.getLibrary());
        if (cfg.getAttributes() != null)
            tokenInfo.setP11Attrs(cfg.getAttributes());
        certificateRepository.save(certificate);
        return certificate;
    }

    private CertificateGenerateResult.User createUser(CertificateGenerateDTO dto) throws UserCreator.UserCreatorException {
        String username = dto.getOwnerId();
        String password = dto.getPassword();
        if (password == null || password.isEmpty())
            password = username;
        int createdUserResult = userCreator.CreateUser(username, password, dto.getOwnerName());
        return createdUserResult == UserCreator.RESULT_CREATED ?
            new CertificateGenerateResult.User(username, password, createdUserResult) :
            new CertificateGenerateResult.User(username, null, createdUserResult);
    }

}
