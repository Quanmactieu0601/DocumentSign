package vn.easyca.signserver.webapp.service;


import io.github.jhipster.security.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.easyca.signserver.core.cryptotoken.Config;
import vn.easyca.signserver.core.cryptotoken.CryptoToken;
import vn.easyca.signserver.webapp.domain.Certificate;
import vn.easyca.signserver.webapp.domain.TokenInfo;
import vn.easyca.signserver.webapp.domain.User;
import vn.easyca.signserver.webapp.security.AuthoritiesConstants;
import vn.easyca.signserver.webapp.service.cert_generator.*;
import vn.easyca.signserver.webapp.service.certificate.CertificateService;
import vn.easyca.signserver.webapp.service.domain.RawCertificate;
import vn.easyca.signserver.webapp.service.dto.*;
import vn.easyca.signserver.webapp.service.port.CertificateRequester;
import vn.easyca.signserver.webapp.service.port.CryptoTokenGetter;

import java.util.HashSet;
import java.util.Set;

@Service
public class CertificateGeneratorService {


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


    public CertificateGeneratedResult genCertificate(CertificateGeneratorDto dto) throws Exception {
        String alias = dto.getOwnerId();
        CryptoToken cryptoToken = cryptoTokenGetter.getToken();
        RawCertificate rawCertificate = null;
        try {
            CertGenerator certGenerator = new CertGenerator(cryptoToken, certificateRequester)
                .setAlias(alias)
                .setKeyLength(dto.getKeyLen())
                .setOwnerInfo(new OwnerInfo(dto.getOwnerId(), dto.getOwnerEmail(), dto.getOwnerPhone()))
                .setCertPackage(new CertPackage(CERT_METHOD, dto.getCertProfile(), CERT_TYPE));
            rawCertificate = certGenerator.genCert();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Cannot generate key & request");
        }

        Certificate certificate = new Certificate();
        try {
            certificate.rawData(rawCertificate.getCert());
            certificate.setAlias(alias);
            certificate.setOwnerId(dto.getOwnerId());
            certificate.setSerial(rawCertificate.getSerial());
            certificate.setTokenType(Certificate.PKCS_11);
            Config cfg = cryptoToken.getConfig();
            TokenInfo tokenInfo = new TokenInfo().setName(cfg.getName())
                .setSlot(Integer.parseInt(cfg.getSlot()))
                .setPassword(cfg.getModulePin())
                .setLibrary(cfg.getLibrary())
                .setP11Attrs(cfg.getPkcs11Config());
            certificate.setCertificateTokenInfo(tokenInfo);
            certificate = certificateService.save(certificate);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Cannot write certificate to database");
        }
        CertificateGeneratedResult result = new CertificateGeneratedResult();
        result.setCertSerial(certificate.getSerial());
        result.setCertData(certificate.getRawData());
        //Tao moi nguoi dung tuong ung voi chung thu so, password sinh ngau nhien
        String password = RandomUtil.generatePassword();
        User newUser;
        try {
            newUser = createNewAccount(dto.getOwnerId(), password);
            result.setUserPassword(password);
            result.setUserName(newUser.getLogin());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Tra ve ket qua
        return result;
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
}
