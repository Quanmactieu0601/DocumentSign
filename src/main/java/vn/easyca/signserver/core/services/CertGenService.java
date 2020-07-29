package vn.easyca.signserver.core.services;


import io.github.jhipster.security.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.easyca.signserver.sign.core.cryptotoken.Config;
import vn.easyca.signserver.sign.core.cryptotoken.CryptoToken;
import vn.easyca.signserver.sign.core.cryptotoken.utils.CertRequestUtils;
import vn.easyca.signserver.webapp.service.UserService;
import vn.easyca.signserver.core.domain.*;
import vn.easyca.signserver.core.utils.CommonUtils;
import vn.easyca.signserver.webapp.domain.User;
import vn.easyca.signserver.webapp.security.AuthoritiesConstants;
import vn.easyca.signserver.core.dto.*;
import vn.easyca.signserver.webapp.service.dto.UserDTO;

import java.security.KeyPair;
import java.util.HashSet;
import java.util.Set;

@Service
public class CertGenService {

    private final int CERT_TYPE = 2;

    private final String CERT_METHOD = "SOFT_TOKEN";

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private UserService userService;

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
        KeyPair keyPair = genKeyPair(cryptoToken,alias,dto.getKeyLen());
        String csr = genCsr(cryptoToken,alias,dto.getSubjectDN());
        RawCertificate rawCertificate = certificateRequester.request(csr,dto.getCertPackage(CERT_METHOD,CERT_TYPE), dto.getSubjectDN(),dto.getOwnerInfo());
        cryptoToken.installCert(alias, CommonUtils.decodeBase64X509(rawCertificate.getCert()));

        Certificate certificate = new Certificate();
        certificate.setRawData(rawCertificate.getCert());
        certificate.setSerial(rawCertificate.getSerial());
        certificate.setTokenType(Certificate.PKCS_11);
        Config cfg = cryptoToken.getConfig();
        TokenInfo tokenInfo = new TokenInfo().setName(cfg.getName())
            .setSlot(Integer.parseInt(cfg.getSlot()))
            .setPassword(cfg.getModulePin())
            .setLibrary(cfg.getLibrary())
            .setP11Attrs(cfg.getPkcs11Config());
        certificate.setTokenInfo(tokenInfo);
        certificateService.save(certificate);

        String password = RandomUtil.generatePassword();
        boolean createdUseResult =userCreator.CreateUser(dto.getOwnerId(),password,dto.getOwnerName());
        if (createdUseResult)
        {
            result.setUserName(dto.getOwnerId());
            result.setUserPassword(password);
        }
        return result;
    }

    private KeyPair genKeyPair(CryptoToken cryptoToken, String alias, int keyLength) throws Exception {
        return cryptoToken.genKeyPair(alias, keyLength);
    }

    private String genCsr(CryptoToken token,String alias, SubjectDN subjectDN) throws Exception {
        CertRequestUtils certRequestUtils = new CertRequestUtils();
        return certRequestUtils.genCsr(subjectDN.toString(),
            token.getProviderName(),
            token.getPrivateKey(alias),
            token.getPublicKey(alias),
            null,
            false,
            false);
    }

    private User createNewAccount(String login, String password) {
        UserDTO userDTO = new UserDTO();
        userDTO.setLogin(login);
        userDTO.setLangKey("vi");
        userDTO.setActivated(true);
        userDTO.setCreatedBy("system");

        //Mac dinh tao role CUSTOMER cho tai khoan moi
        Set<String> authorities = new HashSet<>();
        authorities.add(AuthoritiesConstants.CUSTOMER);
        userDTO.setAuthorities(authorities);
        return userService.createUser(userDTO, password);
    }

    public interface CryptoTokenGetter {

        public CryptoToken getToken() throws Exception;
    }

    public interface CertificateRequester {

        RawCertificate request(String csr, CertPackage certPackage, SubjectDN subjectDN, OwnerInfo ownerInfo) throws Exception;
    }

    public interface UserCreator{

        public boolean CreateUser(String username,String password,String fullName);
    }


}
