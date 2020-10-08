package vn.easyca.signserver.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.easyca.signserver.application.cryptotoken.ConnectionException;
import vn.easyca.signserver.application.cryptotoken.NotConfigException;
import vn.easyca.signserver.application.dependency.CertificateRequester;
import vn.easyca.signserver.application.cryptotoken.CryptoTokenConnector;
import vn.easyca.signserver.application.dependency.UserCreator;
import vn.easyca.signserver.application.exception.GenCSRException;
import vn.easyca.signserver.application.exception.GenKeyPairException;
import vn.easyca.signserver.application.repository.CertificateRepository;
import vn.easyca.signserver.pki.cryptotoken.Config;
import vn.easyca.signserver.pki.cryptotoken.CryptoToken;
import vn.easyca.signserver.pki.cryptotoken.utils.CertRequestUtils;
import vn.easyca.signserver.application.domain.*;
import vn.easyca.signserver.application.dto.*;

import java.net.ConnectException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
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


    public CertificateGenerateResult genCertificate(CertificateGenerateDTO dto) throws Exception {
        CertificateGenerateResult result = new CertificateGenerateResult();
        result.setCert(createCert(dto));
        CertificateGenerateResult.User newUser = createUser(dto);
        if (newUser != null)
            result.setUser(createUser(dto));
        return result;
    }

    private CertificateGenerateResult.User createUser(CertificateGenerateDTO dto) {
        String username = dto.getOwnerId();
        String password = dto.getPassword();
        if (password == null || password.isEmpty())
            password = username;
        try {
            int createdUserResult = userCreator.CreateUser(username, password, dto.getOwnerName());
            return createdUserResult == UserCreator.RESULT_CREATED ?
                new CertificateGenerateResult.User(username, password, createdUserResult) :
                new CertificateGenerateResult.User(username, null, createdUserResult);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private CertificateGenerateResult.Cert createCert(CertificateGenerateDTO dto) throws Exception {
        String alias = dto.getOwnerId();
        CryptoToken cryptoToken = cryptoTokenConnector.getToken();
        KeyPair keyPair = genKeyPair(cryptoToken, alias, dto.getKeyLen());
        String csr = genCsr(cryptoToken.getProviderName(), keyPair.getPrivate(), keyPair.getPublic(), dto.getSubjectDN());
        RawCertificate rawCertificate = certificateRequester.request(csr, dto.getCertPackage(CERT_METHOD, CERT_TYPE), dto.getSubjectDN(), dto.getOwnerInfo());
//        cryptoToken.installCert(alias, CommonUtils.decodeBase64X509(rawCertificate.getCert()));
        Certificate certificate = saveNewCertificate(rawCertificate, alias, dto.getSubjectDN().toString(), cryptoToken);
        return new CertificateGenerateResult.Cert(certificate.getSerial(), certificate.getRawData());
    }

    private KeyPair genKeyPair(CryptoToken cryptoToken, String alias, int keyLength) throws GenKeyPairException {
        try {
            return cryptoToken.genKeyPair(alias, keyLength);
        } catch (Exception ex) {
            throw new GenKeyPairException();
        }
    }

    private String genCsr(String providerName, PrivateKey privateKey, PublicKey publicKey, SubjectDN subjectDN) throws GenCSRException {
        CertRequestUtils certRequestUtils = new CertRequestUtils();
        try {
            return certRequestUtils.genCsr(subjectDN.toString(),
                providerName,
                privateKey,
                publicKey,
                null,
                false,
                false);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new GenCSRException();
        }
    }

    private Certificate saveNewCertificate(RawCertificate rawCertificate,
                                           String alias,
                                           String subjectInfo,
                                           CryptoToken cryptoToken) throws Exception {
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
        if (cfg.getSlot() != null) {
            try {
                tokenInfo.setSlot(Long.parseLong(cfg.getSlot()));
            } catch (Exception ignored) {
            }
        }
        tokenInfo.setPassword(cfg.getModulePin());
        tokenInfo.setLibrary(cfg.getLibrary());
        if (cfg.getAttributes() != null) {
            try {
                tokenInfo.setP11Attrs(cfg.getAttributes());
            } catch (Exception ignored) {
            }
        }
        certificateRepository.save(certificate);
        return certificate;
    }

}
