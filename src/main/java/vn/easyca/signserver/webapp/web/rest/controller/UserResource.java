package vn.easyca.signserver.webapp.web.rest.controller;

import vn.easyca.signserver.webapp.config.Constants;
import vn.easyca.signserver.infrastructure.database.jpa.entity.UserEntity;
import vn.easyca.signserver.infrastructure.database.jpa.repository.UserRepository;
import vn.easyca.signserver.webapp.enm.TransactionType;
import vn.easyca.signserver.webapp.security.AuthoritiesConstants;
import vn.easyca.signserver.webapp.service.MailService;
import vn.easyca.signserver.webapp.service.TransactionService;
import vn.easyca.signserver.webapp.service.UserApplicationService;
import vn.easyca.signserver.webapp.service.dto.TransactionDTO;
import vn.easyca.signserver.webapp.service.dto.UserDTO;
import vn.easyca.signserver.webapp.web.rest.errors.BadRequestAlertException;
import vn.easyca.signserver.webapp.web.rest.errors.EmailAlreadyUsedException;
import vn.easyca.signserver.webapp.web.rest.errors.LoginAlreadyUsedException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * REST controller for managing users.
 * <p>
 * This class accesses the {@link UserEntity} entity, and needs to fetch its collection of authorities.
 * <p>
 * For a normal use-case, it would be better to have an eager relationship between User and Authority,
 * and send everything to the client side: there would be no View Model and DTO, a lot less code, and an outer-join
 * which would be good for performance.
 * <p>
 * We use a View Model and a DTO for 3 reasons:
 * <ul>
 * <li>We want to keep a lazy association between the user and the authorities, because people will
 * quite often do relationships with the user, and we don't want them to get the authorities all
 * the time for nothing (for performance reasons). This is the #1 goal: we should not impact our users'
 * application because of this use-case.</li>
 * <li> Not having an outer join causes n+1 requests to the database. This is not a real issue as
 * we have by default a second-level cache. This means on the first HTTP call we do the n+1 requests,
 * but then all authorities come from the cache, so in fact it's much better than doing an outer join
 * (which will get lots of data from the database, for each HTTP call).</li>
 * <li> As this manages users, for security reasons, we'd rather have a DTO layer.</li>
 * </ul>
 * <p>
 * Another option would be to have a specific JPA entity graph to handle this case.
 */
@RestController
@RequestMapping("/api")
public class UserResource {
    String code = null;
    String message = null;

    private final Logger log = LoggerFactory.getLogger(UserResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserApplicationService userApplicationService;

    private final UserRepository userRepository;

    private final MailService mailService;
    private final TransactionService transactionService;


    public UserResource(UserApplicationService userApplicationService, UserRepository userRepository, MailService mailService, TransactionService transactionService) {
        this.userApplicationService = userApplicationService;
        this.userRepository = userRepository;
        this.mailService = mailService;
        this.transactionService = transactionService;
    }

    /**
     * {@code POST  /users}  : Creates a new user.
     * <p>
     * Creates a new user if the login and email are not already used, and sends an
     * mail with an activation link.
     * The user needs to be activated on creation.
     *
     * @param userDTO the user to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new user, or with status {@code 400 (Bad Request)} if the login or email is already in use.
     * @throws URISyntaxException       if the Location URI syntax is incorrect.
     * @throws BadRequestAlertException {@code 400 (Bad Request)} if the login or email is already in use.
     */
    @PostMapping("/users")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<UserEntity> createUser(@Valid @RequestBody UserDTO userDTO) throws URISyntaxException {
        TransactionDTO transactionDTO = new TransactionDTO("/api/users", TransactionType.SYSTEM);
        log.debug("REST request to save User : {}", userDTO);
        if (userDTO.getId() != null) {
            transactionDTO.setCode("400");
            transactionDTO.setMessage("A new user cannot already have an ID");
            transactionService.save(transactionDTO);
            throw new BadRequestAlertException("A new user cannot already have an ID", "userManagement", "idexists");
            // Lowercase the user login before comparing with database
        } else if (userRepository.findOneByLogin(userDTO.getLogin().toLowerCase()).isPresent()) {
            transactionDTO.setCode("400");
            transactionDTO.setMessage("Login Already Used ");
            transactionService.save(transactionDTO);
            throw new LoginAlreadyUsedException();
        } else if (userRepository.findOneByEmailIgnoreCase(userDTO.getEmail()).isPresent()) {
            transactionDTO.setCode("400");
            transactionDTO.setMessage("Email Already Used ");
            transactionService.save(transactionDTO);
            throw new EmailAlreadyUsedException();
        } else {
            UserEntity newUserEntity = userApplicationService.createUser(userDTO, null);
            mailService.sendCreationEmail(newUserEntity);
            transactionDTO.setCode("200");
            transactionDTO.setMessage("Create User Successfully");
            transactionService.save(transactionDTO);
            return ResponseEntity.created(new URI("/api/users/" + newUserEntity.getLogin()))
                .headers(HeaderUtil.createAlert(applicationName, "userManagement.created", newUserEntity.getLogin()))
                .body(newUserEntity);
        }
    }


    /**
     * {@code PUT /users} : Updates an existing User.
     *
     * @param userDTO the user to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated user.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already in use.
     * @throws LoginAlreadyUsedException {@code 400 (Bad Request)} if the login is already in use.
     */
    @PutMapping("/users")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<UserDTO> updateUser(@Valid @RequestBody UserDTO userDTO) {
        TransactionDTO transactionDTO = new TransactionDTO("/api/users", TransactionType.SYSTEM);
        log.debug("REST request to update User : {}", userDTO);
        Optional<UserEntity> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(userDTO.getId()))) {
            transactionDTO.setCode("400");
            transactionDTO.setMessage("Email Already Used");
            transactionService.save(transactionDTO);
            throw new EmailAlreadyUsedException();
        }
        existingUser = userRepository.findOneByLogin(userDTO.getLogin().toLowerCase());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(userDTO.getId()))) {
            transactionDTO.setCode("400");
            transactionDTO.setMessage(" Login Already Used ");
            transactionService.save(transactionDTO);
            throw new LoginAlreadyUsedException();
        }
        Optional<UserDTO> updatedUser = userApplicationService.updateUser(userDTO);
        transactionDTO.setCode("200");
        transactionDTO.setMessage("Update User Successfully");
        transactionService.save(transactionDTO);

        return ResponseUtil.wrapOrNotFound(updatedUser,
            HeaderUtil.createAlert(applicationName, "userManagement.updated", userDTO.getLogin()));
    }

    /**
     * {@code GET /users} : get all users.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all users.
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers(Pageable pageable) {
        final Page<UserDTO> page = userApplicationService.getAllManagedUsers(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * Gets a list of all roles.
     *
     * @return a string list of all roles.
     */
    @GetMapping("/users/authorities")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public List<String> getAuthorities() {
        return userApplicationService.getAuthorities();
    }

    /**
     * {@code GET /users/:login} : get the "login" user.
     *
     * @param login the login of the user to find.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the "login" user, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/users/{login:" + Constants.LOGIN_REGEX + "}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String login) {
        log.debug("REST request to get User : {}", login);
        return ResponseUtil.wrapOrNotFound(
            userApplicationService.getUserWithAuthoritiesByLogin(login)
                .map(UserDTO::new));
    }

    /**
     * {@code DELETE /users/:login} : delete the "login" User.
     *
     * @param login the login of the user to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/users/{login:" + Constants.LOGIN_REGEX + "}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Void> deleteUser(@PathVariable String login) {
        TransactionDTO transactionDTO = new TransactionDTO("/api/users/{login:" + Constants.LOGIN_REGEX + "}", TransactionType.SYSTEM);
        log.debug("REST request to delete User: {}", login);
        userApplicationService.deleteUser(login);
        transactionDTO.setCode("200");
        transactionDTO.setMessage("Delete User Successfully");
        transactionService.save(transactionDTO);
        return ResponseEntity.noContent().headers(HeaderUtil.createAlert(applicationName, "userManagement.deleted", login)).build();
    }
}
