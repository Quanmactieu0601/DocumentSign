package vn.easyca.signserver.webapp.service;


import io.github.jhipster.security.RandomUtil;
import vn.easyca.signserver.ca.service.api.CAFacadeApi;
import vn.easyca.signserver.core.cryptotoken.Config;
import vn.easyca.signserver.core.cryptotoken.CryptoToken;
import vn.easyca.signserver.webapp.domain.Certificate;
import vn.easyca.signserver.webapp.domain.TokenInfo;
import vn.easyca.signserver.webapp.domain.User;
import vn.easyca.signserver.webapp.service.certificate.CertificateService;
import vn.easyca.signserver.webapp.service.dto.*;
import vn.easyca.signserver.webapp.service.model.generator.CertGenerator;
import vn.easyca.signserver.webapp.service.model.generator.CertGeneratorInput;
import vn.easyca.signserver.webapp.service.model.generator.CertGeneratorOutput;

// complex service
public class CertificateGeneratorService {

    private CryptoToken cryptoToken;

    private CertificateService certificateService;

    private UserService userService;

    private static int CERT_TYPE = 2; // For Company

    public CertificateGeneratorService(CryptoToken cryptoToken, CertificateService certificateService, UserService userService) {
        this.cryptoToken = cryptoToken;
        this.certificateService = certificateService;
        this.userService = userService;
    }

    public CertificateGeneratedResult genCertificate(CertificateGeneratorDto dto) throws Exception {

        Certificate certificate = genCert(dto);
        CertificateGeneratedResult result = new CertificateGeneratedResult(certificate);
        NewAccount newAccount = createNewAccount(dto);
        if (newAccount != null) {
            result.setUserInfo(newAccount.getUser(), newAccount.getPassword());
        }
        return result;
    }

    private Certificate genCert(CertificateGeneratorDto dto) throws Exception {
        String alias = dto.getOwnerId();
        CertGenerator certGenerator = new CertGenerator(cryptoToken, CAFacadeApi.getInstance().createRegisterCertificateApi());
        CertGeneratorInput.CertGeneratorInputBuilder inputBuilder = new CertGeneratorInput.CertGeneratorInputBuilder();
        inputBuilder.setAlias(alias);
        inputBuilder.setAttrs(dto.getCn(), dto.getOu(), dto.getOu(), dto.getL(), dto.getS(), dto.getC())
            .setKeyLength(dto.getKeyLen())
            .setOwner(dto.getOwnerId(), dto.getOwnerPhone(), dto.getOwnerEmail())
            .setCertService(dto.getCertProfile());
        CertGeneratorInput certGeneratorInput = inputBuilder.build();
        CertGeneratorOutput certGeneratorOutput = certGenerator.genCert(certGeneratorInput);
        Certificate certificate = new Certificate();
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
        certificateService.saveWithEncryption(certificate);
        return certificate;
    }

    private NewAccount createNewAccount(CertificateGeneratorDto dto) {
        try {
            String password = RandomUtil.generatePassword();
            UserDTO userDTO = new UserDTO();
            userDTO.setLogin(dto.getOwnerId());
            userDTO.setLangKey("vi");
            userDTO.setActivated(true);
            userDTO.setCreatedBy("system");
            User user = userService.createUser(userDTO, password);
            return new NewAccount(user, password);
        } catch (Exception ex) {
            return null;
        }
    }

    public class NewAccount {

        private final User user;

        private final String password;

        public NewAccount(User user, String password) {
            this.user = user;
            this.password = password;
        }

        public User getUser() {
            return user;
        }

        public String getPassword() {
            return password;
        }
    }


}
