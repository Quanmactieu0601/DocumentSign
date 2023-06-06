package vn.easyca.signserver.webapp.web.rest;

import org.springframework.http.ResponseEntity;
import vn.easyca.signserver.webapp.domain.UserEntity;
import vn.easyca.signserver.webapp.enm.*;
import vn.easyca.signserver.webapp.repository.UserRepository;

import vn.easyca.signserver.webapp.security.SecurityUtils;
import vn.easyca.signserver.webapp.service.*;
import vn.easyca.signserver.webapp.service.UserApplicationService;
import vn.easyca.signserver.webapp.service.dto.PasswordChangeDTO;
import vn.easyca.signserver.webapp.service.dto.UserDTO;
import vn.easyca.signserver.webapp.service.AsyncTransactionService;
import vn.easyca.signserver.webapp.utils.AccountUtils;
import vn.easyca.signserver.webapp.web.rest.errors.*;
import vn.easyca.signserver.webapp.web.rest.vm.KeyAndPasswordVM;
import vn.easyca.signserver.webapp.web.rest.vm.LoginVM;
import vn.easyca.signserver.webapp.web.rest.vm.ManagedUserVM;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class AccountResource {

    private static class AccountResourceException extends RuntimeException {
        private AccountResourceException(String message) {
            super(message);
        }
    }

    private final AsyncTransactionService asyncTransactionService;
    private final Logger log = LoggerFactory.getLogger(AccountResource.class);

    private final UserRepository userRepository;

    private final UserApplicationService userApplicationService;

    private final MailService mailService;

    public AccountResource(AsyncTransactionService asyncTransactionService, UserRepository userRepository, UserApplicationService userApplicationService, MailService mailService ) {
        this.asyncTransactionService = asyncTransactionService;
        this.userRepository = userRepository;
        this.userApplicationService = userApplicationService;
        this.mailService = mailService;
    }

    /**
     * {@code POST  /register} : register the user.
     *
     * @param managedUserVM the managed user View Model.
     * @throws InvalidPasswordException  {@code 400 (Bad Request)} if the password is incorrect.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws LoginAlreadyUsedException {@code 400 (Bad Request)} if the login is already used.
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerAccount(@Valid @RequestBody ManagedUserVM managedUserVM) {
        if (!checkPasswordLength(managedUserVM.getPassword())) {
            asyncTransactionService.newThread("/api/register", TransactionType.SYSTEM, Action.CREATE, Extension.NONE, Method.POST,
                TransactionStatus.FAIL, "Invalid Password", AccountUtils.getLoggedAccount());
            throw new InvalidPasswordException();
        } else {
            UserEntity userEntity = userApplicationService.registerUser(managedUserVM, managedUserVM.getPassword());
            mailService.sendActivationEmail(userEntity);
            asyncTransactionService.newThread("api/register", TransactionType.SYSTEM, Action.CREATE, Extension.NONE, Method.POST,
                TransactionStatus.SUCCESS, null, AccountUtils.getLoggedAccount());
        }
    }

    /**
     * {@code GET  /activate} : activate the registered user.
     *
     * @param key the activation key.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be activated.
     */
    @GetMapping("/activate")
    public void activateAccount(@RequestParam(value = "key") String key) {
        Optional<UserEntity> user = userApplicationService.activateRegistration(key);
        if (!user.isPresent()) {
            asyncTransactionService.newThread("/api/activate", TransactionType.SYSTEM, Action.MODIFY, Extension.NONE, Method.GET,
                TransactionStatus.FAIL, "No user was found for this activation key", AccountUtils.getLoggedAccount());
            throw new AccountResourceException("No user was found for this activation key");
        } else {
            asyncTransactionService.newThread("/api/activate", TransactionType.SYSTEM, Action.MODIFY, Extension.NONE, Method.GET,
                TransactionStatus.SUCCESS, null, AccountUtils.getLoggedAccount());
        }
    }

    /**
     * {@code GET  /authenticate} : check if the user is authenticated, and return its login.
     *
     * @param request the HTTP request.
     * @return the login if the user is authenticated.
     */
    @GetMapping("/authenticate")
    public String isAuthenticated(HttpServletRequest request) {
        log.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    /**
     * {@code GET  /account} : get the current user.
     *
     * @return the current user.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be returned.
     */
    @GetMapping("/account")
    public UserDTO getAccount() {
        return userApplicationService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new AccountResourceException("User could not be found"));
    }

    /**
     * {@code POST  /account} : update the current user information.
     *
     * @param userDTO the current user information.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws RuntimeException          {@code 500 (Internal Server Error)} if the user login wasn't found.
     */
    @PostMapping("/account")
    public void saveAccount(@Valid @RequestBody UserDTO userDTO) {
        String userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new AccountResourceException("Current user login not found"));
        Optional<UserEntity> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getLogin().equalsIgnoreCase(userLogin))) {
            asyncTransactionService.newThread("/api/account", TransactionType.SYSTEM, Action.CREATE, Extension.NONE, Method.POST,
                TransactionStatus.FAIL, "Email Already Used", AccountUtils.getLoggedAccount());
            throw new EmailAlreadyUsedException();
        }
        Optional<UserEntity> user = userRepository.findOneByLogin(userLogin);
        if (!user.isPresent()) {
            asyncTransactionService.newThread("/api/account", TransactionType.SYSTEM, Action.CREATE, Extension.NONE, Method.POST,
                TransactionStatus.FAIL, "User Could Not Be Found", AccountUtils.getLoggedAccount());
            throw new AccountResourceException("User could not be found");
        } else {
            userApplicationService.updateUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(),
                userDTO.getLangKey(), userDTO.getImageUrl());
            asyncTransactionService.newThread("/api/account", TransactionType.SYSTEM, Action.CREATE, Extension.NONE, Method.POST,
                TransactionStatus.SUCCESS, null, AccountUtils.getLoggedAccount());
        }
    }

    /**
     * {@code POST  /account/change-password} : changes the current user's password.
     *
     * @param passwordChangeDto current and new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the new password is incorrect.
     */
    @PostMapping(path = "/account/change-password")
    public void changePassword(@RequestBody PasswordChangeDTO passwordChangeDto) {
        if (!checkPasswordLength(passwordChangeDto.getNewPassword())) {
            asyncTransactionService.newThread("/api/account/change-password", TransactionType.SYSTEM, Action.MODIFY, Extension.NONE, Method.POST,
                TransactionStatus.FAIL, "Invalid Password", AccountUtils.getLoggedAccount());
            throw new InvalidPasswordException();
        } else {
            userApplicationService.changePassword(passwordChangeDto.getCurrentPassword(), passwordChangeDto.getNewPassword());
            asyncTransactionService.newThread("/api/account/change-password", TransactionType.SYSTEM, Action.MODIFY, Extension.NONE, Method.POST,
                TransactionStatus.SUCCESS, null, AccountUtils.getLoggedAccount());
        }
    }

    @PostMapping(path = "/account/first-login")
    public ResponseEntity remindChangePasswordInFirstLogin(@RequestBody LoginVM login) {
         Boolean isFirstLogin = userApplicationService.remindChangePassword(login.getUsername());
        return new ResponseEntity<>(isFirstLogin, HttpStatus.OK);
    }

    @PutMapping(path = "/account/default-remind")
    public void setDefaultOfRemindChangePassword() {
         userApplicationService.setDefaultOfRemindChangePassword(AccountUtils.getLoggedAccount());
    }

    /**PutMapping
     * {@code POST   /account/reset-password/init} : Send an email to reset the password of the user.
     *
     * @param mail the mail of the user.
     */
    @PostMapping(path = "/account/reset-password/init")
    public void requestPasswordReset(@RequestBody String mail) {
        Optional<UserEntity> user = userApplicationService.requestPasswordReset(mail);
        if (user.isPresent()) {
            mailService.sendPasswordResetMail(user.get());
            asyncTransactionService.newThread("/api/account/reset-password/init", TransactionType.SYSTEM, Action.MODIFY, Extension.NONE, Method.POST,
                TransactionStatus.SUCCESS, null, AccountUtils.getLoggedAccount());
        } else {
            // Pretend the request has been successful to prevent checking which emails really exist
            // but log that an invalid attempt has been made
            asyncTransactionService.newThread("/api/account/reset-password/init", TransactionType.SYSTEM, Action.MODIFY, Extension.NONE, Method.POST,
                TransactionStatus.FAIL, "Password Reset Requested For Non Existing Mail", AccountUtils.getLoggedAccount());
        }
    }

    /**
     * {@code POST   /account/reset-password/finish} : Finish to reset the password of the user.
     *
     * @param keyAndPassword the generated key and the new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws RuntimeException         {@code 500 (Internal Server Error)} if the password could not be reset.
     */
    @PostMapping(path = "/account/reset-password/finish")
    public void finishPasswordReset(@RequestBody KeyAndPasswordVM keyAndPassword) {
        if (!checkPasswordLength(keyAndPassword.getNewPassword())) {
            asyncTransactionService.newThread("/api/account/reset-password/finish", TransactionType.SYSTEM, Action.MODIFY, Extension.NONE, Method.POST,
                TransactionStatus.FAIL, "Invalid Password", AccountUtils.getLoggedAccount());
            throw new InvalidPasswordException();
        }
        Optional<UserEntity> user =
            userApplicationService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey());
        if (!user.isPresent()) {
            asyncTransactionService.newThread("/api/account/reset-password/init/finish", TransactionType.SYSTEM, Action.MODIFY, Extension.NONE, Method.POST,
               TransactionStatus.FAIL, "No User Was Found For This Reset Key", AccountUtils.getLoggedAccount());
            throw new AccountResourceException("No user was found for this reset key");
        } else {
            asyncTransactionService.newThread("/api/account/reset-password/init/finish", TransactionType.SYSTEM, Action.MODIFY, Extension.NONE, Method.POST,
                TransactionStatus.SUCCESS, null, AccountUtils.getLoggedAccount());
        }
    }

    private static boolean checkPasswordLength(String password) {
        return !StringUtils.isEmpty(password) &&
            password.length() >= ManagedUserVM.PASSWORD_MIN_LENGTH &&
            password.length() <= ManagedUserVM.PASSWORD_MAX_LENGTH;
    }

    @PostMapping(path = "/account/valid-email")
    public void SendValidEmail() {
        Boolean isValidEmail = userApplicationService.SendValidEmail();
    }

    @PostMapping(path = "/account/valid-email")
    public ResponseEntity ValidEmail(@RequestParam String token) {
         Boolean isValidEmail = userApplicationService.ValidEmail(token);
        return new ResponseEntity<>(isValidEmail, HttpStatus.OK);
    }

}
