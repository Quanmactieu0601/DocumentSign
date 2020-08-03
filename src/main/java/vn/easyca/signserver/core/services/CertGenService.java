package vn.easyca.signserver.core.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.easyca.signserver.sign.core.cryptotoken.Config;
import vn.easyca.signserver.sign.core.cryptotoken.CryptoToken;
import vn.easyca.signserver.sign.core.cryptotoken.utils.CertRequestUtils;
import vn.easyca.signserver.core.domain.*;
import vn.easyca.signserver.core.utils.CommonUtils;
import vn.easyca.signserver.core.services.dto.*;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

@Service
public class CertGenService {

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
        String alias = dto.getOwnerId();
        CryptoToken cryptoToken = cryptoTokenGetter.getToken();
        KeyPair keyPair = genKeyPair(cryptoToken, alias, dto.getKeyLen());
        String csr = genCsr(cryptoToken.getProviderName(), keyPair.getPrivate(), keyPair.getPublic(), dto.getSubjectDN());
        RawCertificate rawCertificate = certificateRequester.request(csr, dto.getCertPackage(CERT_METHOD, CERT_TYPE), dto.getSubjectDN(), dto.getOwnerInfo());
        cryptoToken.installCert(alias, CommonUtils.decodeBase64X509(rawCertificate.getCert()));
        Certificate certificate = saveNewCertificate(rawCertificate, cryptoToken);
        result.setCert(new CertificateGeneratedResult.Cert(certificate.getSerial(), certificate.getRawData()));
        int createdUserResult = userCreator.CreateUser(dto.getOwnerId(), dto.getPassword(), dto.getOwnerName());
        result.setUser(new CertificateGeneratedResult.User(dto.getOwnerId(), dto.getPassword(), createdUserResult));
        return result;
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
            .setSlot(Integer.parseInt(cfg.getSlot()))
            .setPassword(cfg.getModulePin())
            .setLibrary(cfg.getLibrary())
            .setP11Attrs(cfg.getPkcs11Config());
        certificate.setTokenInfo(tokenInfo);
        certificateService.save(certificate);
        return certificate;
    }

    public interface CryptoTokenGetter {

        public CryptoToken getToken() throws Exception;
    }

    public interface CertificateRequester {

        RawCertificate request(String csr, CertPackage certPackage, SubjectDN subjectDN, OwnerInfo ownerInfo) throws Exception;
    }

    public interface UserCreator {

        public final int RESULT_CREATED = 1;
        public final int RESULT_EXIST = 2;

        public int CreateUser(String username, String password, String fullName) throws Exception;
    }


}
