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
    public String encryptKey(@Valid @RequestParam String rawKey) throws URISyntaxException, ApplicationException {
        return symmetricService.encrypt(rawKey);
    }

    @PostMapping("/decryptKey")
    public String decryptKey(@Valid @RequestParam String decryptedKey) throws URISyntaxException, ApplicationException {
        return symmetricService.decrypt(decryptedKey);
    }
}
