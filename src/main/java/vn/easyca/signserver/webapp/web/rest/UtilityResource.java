package vn.easyca.signserver.webapp.web.rest;

import io.github.jhipster.web.util.ResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.easyca.signserver.core.domain.CertificateDTO;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.core.factory.CryptoTokenProxy;
import vn.easyca.signserver.core.factory.CryptoTokenProxyFactory;
import vn.easyca.signserver.webapp.domain.Certificate;
import vn.easyca.signserver.webapp.repository.CertificateRepository;
import vn.easyca.signserver.webapp.security.AuthoritiesConstants;
import vn.easyca.signserver.webapp.service.SystemConfigCachingService;
import vn.easyca.signserver.webapp.service.dto.CaptchaDTO;
import vn.easyca.signserver.webapp.service.mapper.CertificateMapper;
import vn.easyca.signserver.webapp.utils.CaptchaUtils;
import vn.easyca.signserver.webapp.utils.SymmetricEncryptors;

import javax.validation.Valid;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@RestController
@RequestMapping("/util")
public class UtilityResource {
    private final SymmetricEncryptors symmetricService;
    private final CertificateRepository certificateRepository;
    private final CertificateMapper mapper;
    private final CryptoTokenProxyFactory cryptoTokenProxyFactory;
    private final SystemConfigCachingService systemConfigCachingService;
    private final CaptchaUtils captchaUtils;


    public UtilityResource(SymmetricEncryptors symmetricService, CertificateRepository certificateRepository, CertificateMapper mapper, CryptoTokenProxyFactory cryptoTokenProxyFactory, SystemConfigCachingService systemConfigCachingService, CaptchaUtils captchaUtils) {
        this.symmetricService = symmetricService;
        this.certificateRepository = certificateRepository;
        this.mapper = mapper;
        this.cryptoTokenProxyFactory = cryptoTokenProxyFactory;
        this.systemConfigCachingService = systemConfigCachingService;
        this.captchaUtils = captchaUtils;
    }

    private boolean matchSecretKey(String secretKey) {
        return "easysign-ca-!$%@#$1$$".equals(secretKey);
    }

    @PostMapping("/encryptKey")
    public String encryptKey(String secretKey, @Valid @RequestParam String rawKey) throws ApplicationException {
        if (!matchSecretKey(secretKey))
            return "-- Secret key is not correct --";
        return symmetricService.encrypt(rawKey);
    }

    @PostMapping("/decryptKey")
    public String decryptKey(String secretKey, @Valid @RequestParam String decryptedKey) throws ApplicationException {
        if (!matchSecretKey(secretKey))
            return "-- Secret key is not correct --";
        return symmetricService.decrypt(decryptedKey);
    }

    @PostMapping("/checkAuthenCert")
    public String checkAuthenCert(String secretKey, String pin, String serial) {
        if (!matchSecretKey(secretKey))
            return "-- Secret key is not correct --";
        try {
            Optional<Certificate> certificateOptional = certificateRepository.findOneBySerial(serial);
            if (!certificateOptional.isPresent())
                return "-- Certificate is not found --";
            if (certificateOptional.get().getActiveStatus() == 0) {
                return "-- Certificate is in-active";
            }
            CertificateDTO certificateDTO = mapper.map(certificateOptional.get());
            CryptoTokenProxy cryptoTokenProxy = cryptoTokenProxyFactory.resolveCryptoTokenProxy(certificateDTO, pin);
            cryptoTokenProxy.getCryptoToken().checkInitialized();
            return "** Authen OK **";
        } catch (Exception ex) {
            return String.format("-- Co loi xay ra: %s --", ex.getMessage());
        }
    }

    @PostMapping("/resetSystemConfigCache")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.SUPER_ADMIN + "\")")
    public String resetSystemConfigCache(String secretKey) {
        if (!matchSecretKey(secretKey))
            return "-- Secret key is not correct --";
        try {
            systemConfigCachingService.clearCache();
            return "** Reset system config cache successfully **";
        } catch (Exception ex) {
            return String.format("-- Co loi xay ra: %s --", ex.getMessage());
        }
    }

    @GetMapping("/captcha")
    public ResponseEntity<CaptchaDTO> getCaptcha() throws IOException, NoSuchAlgorithmException {
        Optional<CaptchaDTO> captchaDTO = Optional.ofNullable(captchaUtils.generateCaptcha());
        return ResponseUtil.wrapOrNotFound(captchaDTO);
    }
}
