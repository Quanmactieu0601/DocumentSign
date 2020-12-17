package vn.easyca.signserver.webapp.web.rest;

import org.springframework.web.bind.annotation.*;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.webapp.utils.SymmetricEncryptors;

import javax.validation.Valid;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/util")
public class UtilityResource {
    private final SymmetricEncryptors symmetricService;
    public UtilityResource(SymmetricEncryptors symmetricService) {
        this.symmetricService = symmetricService;
    }

    @PostMapping("/encryptKey")
    public String encryptKey(String secretKey, @Valid @RequestParam String rawKey) throws ApplicationException {
        if (!"easysign-ca-!@#$%".equals(secretKey))
            return "";
        return symmetricService.encrypt(rawKey);
    }

    @PostMapping("/decryptKey")
    public String decryptKey(String secretKey, @Valid @RequestParam String decryptedKey) throws ApplicationException {
        if (!"easysign-ca-!@#$%".equals(secretKey))
            return "";
        return symmetricService.decrypt(decryptedKey);
    }
}
