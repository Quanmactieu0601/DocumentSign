package vn.easyca.signserver.business.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.easyca.signserver.pki.cryptotoken.Config;
import vn.easyca.signserver.pki.cryptotoken.CryptoToken;
import vn.easyca.signserver.pki.cryptotoken.utils.CertRequestUtils;
import vn.easyca.signserver.business.domain.*;
import vn.easyca.signserver.business.services.dto.*;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

@Service
public class CertGenService {
    private final Logger log = LoggerFactory.getLogger(CertGenService.class);

    private final int CERT_TYPE = 2;

    private final String CERT_METHOD = "SOFT_TOKEN";

    @Autowired
    private CertificateService certificateService;

    @Autowired
    CryptoTokenGetter cryptoTokenGetter;

    @Autowired
    CertificateRequester certificateRequester;

    @Autowired
    UserCreator userCreator;


    public CertificateGeneratedResult genCertificate(CertificateGeneratorDto dto) throws Exception {
        CertificateGeneratedResult result = new CertificateGeneratedResult();
        try {
            result.setCert(createCert(dto));
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error(ex.getMessage());
            throw ex;
        }
        result.setUser(createUser(dto));
        return result;
    }

    private CertificateGeneratedResult.User createUser(CertificateGeneratorDto dto) {
        String username = dto.getOwnerId();
        String password = dto.getPassword() == null || dto.getPassword().isEmpty() ? dto.getOwnerId() : dto.getPassword();
        int createdUserResult = 0;
        try {
            createdUserResult = userCreator.CreateUser(username, password, dto.getOwnerName());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return createdUserResult == UserCreator.RESULT_CREATED ?
            new CertificateGeneratedResult.User(username, password, createdUserResult) :
            new CertificateGeneratedResult.User(username, null, createdUserResult);
    }

    private CertificateGeneratedResult.Cert createCert(CertificateGeneratorDto dto) throws Exception {
        String alias = dto.getOwnerId();
        CryptoToken cryptoToken = cryptoTokenGetter.getToken();
        KeyPair keyPair = genKeyPair(cryptoToken, alias, dto.getKeyLen());
        String csr = genCsr(cryptoToken.getProviderName(), keyPair.getPrivate(), keyPair.getPublic(), dto.getSubjectDN());
        RawCertificate rawCertificate = certificateRequester.request(csr, dto.getCertPackage(CERT_METHOD, CERT_TYPE), dto.getSubjectDN(), dto.getOwnerInfo());
//        cryptoToken.installCert(alias, CommonUtils.decodeBase64X509(rawCertificate.getCert()));
        Certificate certificate = saveNewCertificate(rawCertificate, cryptoToken);
        return new CertificateGeneratedResult.Cert(certificate.getSerial(), certificate.getRawData());
    }

    private KeyPair genKeyPair(CryptoToken cryptoToken, String alias, int keyLength) throws Exception {
        return cryptoToken.genKeyPair(alias, keyLength);
    }

    private String genCsr(String providerName, PrivateKey privateKey, PublicKey publicKey, SubjectDN subjectDN) throws Exception {
        CertRequestUtils certRequestUtils = new CertRequestUtils();
        return certRequestUtils.genCsr(subjectDN.toString(),
            providerName,
            privateKey,
            publicKey,
            null,
            false,
            false);
    }

    private Certificate saveNewCertificate(RawCertificate rawCertificate, CryptoToken cryptoToken) throws Exception {
        Certificate certificate = new Certificate();
        certificate.setRawData(rawCertificate.getCert());
        certificate.setSerial(rawCertificate.getSerial());
        certificate.setTokenType(Certificate.PKCS_11);
        Config cfg = cryptoToken.getConfig();
        TokenInfo tokenInfo = new TokenInfo()
            .setName(cfg.getName())
            .setSlot(Long.parseLong(cfg.getSlot()))
            .setPassword(cfg.getModulePin())
            .setLibrary(cfg.getLibrary())
            .setP11Attrs(cfg.getPkcs11Config());
        certificate.setTokenInfo(tokenInfo);
        certificateService.save(certificate);
        return certificate;
    }

    public interface CryptoTokenGetter {
        CryptoToken getToken() throws Exception;
    }

    public interface CertificateRequester {
        RawCertificate request(String csr, CertPackage certPackage, SubjectDN subjectDN, OwnerInfo ownerInfo) throws Exception;
    }

    public interface UserCreator {
        int RESULT_CREATED = 1;
        int RESULT_EXIST = 2;

        int CreateUser(String username, String password, String fullName) throws Exception;
    }


}
