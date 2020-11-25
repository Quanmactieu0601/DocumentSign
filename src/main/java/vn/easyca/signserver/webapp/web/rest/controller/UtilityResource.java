package vn.easyca.signserver.webapp.web.rest.controller;

import io.github.jhipster.web.util.HeaderUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.core.services.SymmetricService;
import vn.easyca.signserver.webapp.domain.UserEntity;
import vn.easyca.signserver.webapp.enm.Method;
import vn.easyca.signserver.webapp.enm.TransactionType;
import vn.easyca.signserver.webapp.security.AuthoritiesConstants;
import vn.easyca.signserver.webapp.service.dto.UserDTO;
import vn.easyca.signserver.webapp.utils.AccountUtils;
import vn.easyca.signserver.webapp.web.rest.errors.BadRequestAlertException;
import vn.easyca.signserver.webapp.web.rest.errors.EmailAlreadyUsedException;
import vn.easyca.signserver.webapp.web.rest.errors.LoginAlreadyUsedException;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/util")
public class UtilityResource {
    private final SymmetricService symmetricService;
    public UtilityResource(SymmetricService symmetricService) {
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
