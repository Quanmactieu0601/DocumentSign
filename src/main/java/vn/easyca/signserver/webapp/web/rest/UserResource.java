package vn.easyca.signserver.webapp.web.rest;

import com.google.common.base.Strings;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import vn.easyca.signserver.webapp.config.Constants;
import vn.easyca.signserver.webapp.domain.UserEntity;
import vn.easyca.signserver.webapp.enm.*;
import vn.easyca.signserver.webapp.repository.UserRepository;

import vn.easyca.signserver.webapp.security.AuthoritiesConstants;
import vn.easyca.signserver.webapp.service.MailService;
import vn.easyca.signserver.webapp.service.UserApplicationService;
import vn.easyca.signserver.webapp.service.dto.UserDTO;
import vn.easyca.signserver.webapp.service.error.InfoFromCNToCountryNotFoundException;
import vn.easyca.signserver.webapp.service.error.InvalidCountryColumnLength;
import vn.easyca.signserver.webapp.service.error.RequiredColumnNotFoundException;
import vn.easyca.signserver.webapp.service.error.UsernameAlreadyUsedException;
import vn.easyca.signserver.webapp.utils.ExcelUtils;
import vn.easyca.signserver.webapp.service.AsyncTransactionService;
import vn.easyca.signserver.webapp.utils.AccountUtils;
import vn.easyca.signserver.webapp.web.rest.errors.BadRequestAlertException;
import vn.easyca.signserver.webapp.web.rest.errors.CurrentPasswordNotMatchException;
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
import vn.easyca.signserver.webapp.web.rest.vm.response.BaseResponseVM;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
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
public class UserResource extends BaseResource {
    private final Logger log = LoggerFactory.getLogger(UserResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserApplicationService userApplicationService;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final AsyncTransactionService asyncTransactionService;

    public UserResource(UserApplicationService userApplicationService, UserRepository userRepository, PasswordEncoder passwordEncoder, MailService mailService, AsyncTransactionService asyncTransactionService) {
        this.userApplicationService = userApplicationService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.asyncTransactionService = asyncTransactionService;
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
        log.debug("REST request to save User : {}", userDTO);
        if (userDTO.getId() != null) {
            asyncTransactionService.newThread("/api/users", TransactionType.SYSTEM, Action.CREATE, Extension.NONE, Method.POST,
                TransactionStatus.FAIL, "A New User Cannot Already Have An ID", AccountUtils.getLoggedAccount());
            throw new BadRequestAlertException("A new user cannot already have an ID", "userManagement", "idexists");
            // Lowercase the user login before comparing with database
        } else if (userRepository.findOneByLogin(userDTO.getLogin().toLowerCase()).isPresent()) {
            asyncTransactionService.newThread("/api/users", TransactionType.SYSTEM, Action.CREATE, Extension.NONE, Method.POST,
                TransactionStatus.FAIL, "Login Already Used", AccountUtils.getLoggedAccount());
            throw new LoginAlreadyUsedException();
        } else if (userRepository.findOneByEmailIgnoreCase(userDTO.getEmail()).isPresent()) {
            asyncTransactionService.newThread("/api/users", TransactionType.SYSTEM, Action.CREATE, Extension.NONE, Method.POST,
                TransactionStatus.FAIL, "Email Already Used", AccountUtils.getLoggedAccount());
            throw new EmailAlreadyUsedException();
        } else {
            UserEntity newUserEntity = userApplicationService.createUser(userDTO);
            mailService.sendCreationEmail(newUserEntity);
            asyncTransactionService.newThread("/api/users", TransactionType.SYSTEM, Action.CREATE, Extension.NONE, Method.POST,
                TransactionStatus.SUCCESS, null, AccountUtils.getLoggedAccount());
            return ResponseEntity.created(new URI("/api/users/" + newUserEntity.getLogin()))
                .headers(HeaderUtil.createAlert(applicationName, "userManagement.created", newUserEntity.getLogin()))
                .body(newUserEntity);
        }
    }

    @PostMapping("users/uploadUser")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<BaseResponseVM> uploadUser(@RequestParam("file") MultipartFile file) {
        try {
            List<UserDTO> userDTOList = ExcelUtils.convertExcelToUserDTO(file.getInputStream());
            for (UserDTO userDTO : userDTOList) {
                // TODO: use other method instead of use registerUser
                userApplicationService.registerUser(userDTO, userDTO.getLogin());
            }
            status = TransactionStatus.SUCCESS;
            return ResponseEntity.ok(new BaseResponseVM(HttpStatus.OK.value(), null, null));
        } catch (IOException e) {
            message = e.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(HttpStatus.EXPECTATION_FAILED.value(), null, e.getMessage()));
        } catch (UsernameAlreadyUsedException usernameAlreadyUsedException) {
            message = usernameAlreadyUsedException.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(HttpStatus.BAD_REQUEST.value(), null, usernameAlreadyUsedException.getMessage()));
        } catch (vn.easyca.signserver.webapp.service.error.EmailAlreadyUsedException emailAlreadyUsedException) {
            message = emailAlreadyUsedException.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(HttpStatus.BAD_REQUEST.value(), null, emailAlreadyUsedException.getMessage()));
        } catch (RequiredColumnNotFoundException requiredColumnNotFoundException) {
            message = requiredColumnNotFoundException.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(HttpStatus.BAD_REQUEST.value(), null, requiredColumnNotFoundException.getMessage()));
        } catch (InvalidCountryColumnLength invalidCountryColumnLength) {
            message = invalidCountryColumnLength.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(HttpStatus.BAD_REQUEST.value(), null, invalidCountryColumnLength.getMessage()));
        } catch (InfoFromCNToCountryNotFoundException infoFromCNToCountryNotFoundException) {
            message = infoFromCNToCountryNotFoundException.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(HttpStatus.BAD_REQUEST.value(), null, infoFromCNToCountryNotFoundException.getMessage()));
        } finally {
            asyncTransactionService.newThread("/api/users/uploadUser", TransactionType.SYSTEM, Action.CREATE, Extension.NONE, Method.POST,
                status, message, AccountUtils.getLoggedAccount());
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
        log.debug("REST request to update User : {}", userDTO);
        Optional<UserEntity> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(userDTO.getId()))) {
            asyncTransactionService.newThread("/api/users", TransactionType.SYSTEM, Action.CREATE, Extension.NONE, Method.PUT,
                TransactionStatus.FAIL, "Email Already Used", AccountUtils.getLoggedAccount());
            throw new EmailAlreadyUsedException();
        }

        existingUser = userRepository.findOneByLogin(userDTO.getLogin().toLowerCase());
        if (existingUser.isPresent() && (!existingUser.get().getId().equals(userDTO.getId()))) {
            asyncTransactionService.newThread("/api/users", TransactionType.SYSTEM, Action.CREATE, Extension.NONE, Method.PUT,
                TransactionStatus.FAIL, "Login Already Used", AccountUtils.getLoggedAccount());
            throw new LoginAlreadyUsedException();
        }

        if (!Strings.isNullOrEmpty(userDTO.getCurrentPassword())) {
            if (!passwordEncoder.matches(userDTO.getCurrentPassword(), existingUser.get().getPassword())) {
                asyncTransactionService.newThread("/api/users", TransactionType.SYSTEM, Action.CREATE, Extension.NONE, Method.PUT,
                    TransactionStatus.FAIL, "Current Password does not match", AccountUtils.getLoggedAccount());
                throw new CurrentPasswordNotMatchException();
            }
        }

        Optional<UserDTO> updatedUser = userApplicationService.updateUser(userDTO);
        asyncTransactionService.newThread("/api/users", TransactionType.SYSTEM, Action.CREATE, Extension.NONE, Method.PUT,
            TransactionStatus.SUCCESS, null, AccountUtils.getLoggedAccount());
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

    @GetMapping("users/templateFile")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<byte[]> getTemplateFileUpload(HttpServletResponse response) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = new ClassPathResource("templates/upload/UserUploadTemplate.xlsx").getInputStream();
            byte[] isr = new byte[inputStream.available()];
            inputStream.read(isr);
            response.setContentType("application/vnd.ms-excel");

            HttpHeaders respHeaders = new HttpHeaders();
            respHeaders.setContentLength(isr.length);
            respHeaders.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(isr, respHeaders, HttpStatus.OK);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    @GetMapping("/users/search")
    public ResponseEntity<List<UserDTO>> getAllUsersByFilter(Pageable pageable, @RequestParam(required = false) String account, @RequestParam(required = false) String name, @RequestParam(required = false) String email, @RequestParam(required = false) String ownerId, @RequestParam(required = false) String commonName, @RequestParam(required = false) String country, @RequestParam(required = false) String phone) {
        final Page<UserDTO> page = userApplicationService.getByFilter(pageable, account, name, email, ownerId, commonName, country, phone);
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
        log.debug("REST request to delete User: {}", login);
        userApplicationService.deleteUser(login);
        asyncTransactionService.newThread("/api/users/login", TransactionType.SYSTEM, Action.DELETE, Extension.NONE, Method.DELETE,
            TransactionStatus.SUCCESS, null, AccountUtils.getLoggedAccount());
        return ResponseEntity.noContent().headers(HeaderUtil.createAlert(applicationName, "userManagement.deleted", login)).build();
    }
}
