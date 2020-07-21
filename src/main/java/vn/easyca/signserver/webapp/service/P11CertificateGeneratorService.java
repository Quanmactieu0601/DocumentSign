package vn.easyca.signserver.webapp.service;


import io.github.jhipster.security.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.easyca.signserver.ca.service.api.CAFacadeApi;
import vn.easyca.signserver.core.cryptotoken.Config;
import vn.easyca.signserver.core.cryptotoken.CryptoToken;
import vn.easyca.signserver.core.cryptotoken.P11CryptoToken;
import vn.easyca.signserver.webapp.config.Configs;
import vn.easyca.signserver.webapp.domain.Certificate;
import vn.easyca.signserver.webapp.domain.TokenInfo;
import vn.easyca.signserver.webapp.domain.User;
import vn.easyca.signserver.webapp.security.AuthoritiesConstants;
import vn.easyca.signserver.webapp.service.certificate.CertificateService;
import vn.easyca.signserver.webapp.service.dto.*;
import vn.easyca.signserver.webapp.service.cert_generator.CertGenerator;
import vn.easyca.signserver.webapp.service.cert_generator.CertGeneratorInput;
import vn.easyca.signserver.webapp.service.cert_generator.CertGeneratorOutput;

import java.util.HashSet;
import java.util.Set;

@Service
public class P11CertificateGeneratorService {
    //Chi dang ho tro cert doanh nghiep
    private final int CERT_TYPE = 2;
    private final String CERT_METHOD = "SOFT_TOKEN";

    private final String TOKEN_NAME = "EasyCA Token";
    private final String TOKEN_LIB = "C:\\Windows\\System32\\easyca_csp11_v1.dll";
    private final String TOKEN_PIN = "";

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private UserService userService;

    @Transactional
    public CertificateGeneratedResult genCertificate(CertificateGeneratorDto dto) throws Exception {
        //Xu ly dau vao
        String alias = dto.getOwnerId();
        CertGeneratorInput.CertGeneratorInputBuilder inputBuilder = new CertGeneratorInput.CertGeneratorInputBuilder();
        inputBuilder.setAlias(alias)
            .setOwner(dto.getOwnerId(), dto.getOwnerPhone(), dto.getOwnerEmail())
            .setAttrs(dto.getCn(), dto.getOu(), dto.getOu(), dto.getL(), dto.getS(), dto.getC())
            .setKeyLength(dto.getKeyLen())
            .setCertService(dto.getCertProfile(), CERT_TYPE, CERT_METHOD);
        CertGeneratorInput certGeneratorInput = inputBuilder.build();

        //Gen key trong token/hsm va tao cert request
        CertGeneratorOutput certGeneratorOutput;
        P11CryptoToken cryptoToken = new P11CryptoToken();
        try {
            cryptoToken.init(initP11Config());
            CertGenerator certGenerator = new CertGenerator(cryptoToken, CAFacadeApi.getInstance().createRegisterCertificateApi());
            certGeneratorOutput = certGenerator.genCert(certGeneratorInput);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Cannot generate key & request");
        }

        //Luu them ban ghi certificate trong DB
        Certificate certificate = new Certificate();
        try {
            certificate.rawData(certGeneratorOutput.getCertificate());
            certificate.setAlias(alias);
            certificate.setOwnerId(dto.getOwnerId());
            certificate.setSerial(certGeneratorOutput.getSerial());
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

        //Tao moi nguoi dung tuong ung voi chung thu so, password sinh ngau nhien
        String password = RandomUtil.generatePassword();
        User newUser;
        try {
            newUser = createNewAccount(dto.getOwnerId(), password);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Cannot create new account");
        }

        //Tra ve ket qua
        CertificateGeneratedResult result = new CertificateGeneratedResult();
        result.setCertSerial(certificate.getSerial());
        result.setCertData(certificate.getRawData());
        result.setUser(newUser.getLogin());
        result.setUserPassword(password);
        return result;
    }

    private Config initP11Config() {
        return Config.build().initPkcs11(TOKEN_NAME, TOKEN_LIB, TOKEN_PIN);
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
