package vn.easyca.signserver.webapp.web.rest.controller;

import vn.easyca.signserver.infrastructure.database.jpa.entity.UserEntity;
import vn.easyca.signserver.infrastructure.database.jpa.repository.UserRepository;
import vn.easyca.signserver.webapp.enm.TransactionType;
import vn.easyca.signserver.webapp.security.SecurityUtils;
import vn.easyca.signserver.webapp.service.*;
import vn.easyca.signserver.webapp.service.UserApplicationService;
import vn.easyca.signserver.webapp.service.dto.PasswordChangeDTO;
import vn.easyca.signserver.webapp.service.dto.TransactionDTO;
import vn.easyca.signserver.webapp.service.dto.UserDTO;
import vn.easyca.signserver.webapp.web.rest.errors.*;
import vn.easyca.signserver.webapp.web.rest.vm.KeyAndPasswordVM;
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

    String code = null;
    String message = null;

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);

    private final UserRepository userRepository;

    private final UserApplicationService userApplicationService;

    private final MailService mailService;
    private final TransactionService transactionService;

    public AccountResource(UserRepository userRepository,
                           UserApplicationService userApplicationService,
                           MailService mailService, TransactionService transactionService) {

        this.userRepository = userRepository;
        this.userApplicationService = userApplicationService;
        this.mailService = mailService;
        this.transactionService = transactionService;
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
        TransactionDTO transactionDTO = new TransactionDTO("/api/register", TransactionType.SYSTEM);
        if (!checkPasswordLength(managedUserVM.getPassword())) {
            transactionDTO.setCode("400");
            transactionDTO.setMessage("Invalid Password");
            transactionService.save(transactionDTO);
            throw new InvalidPasswordException();
        } else {
            UserEntity userEntity = userApplicationService.registerUser(managedUserVM, managedUserVM.getPassword());
            mailService.sendActivationEmail(userEntity);
            transactionDTO.setCode("200");
            transactionDTO.setMessage("Register successful");
            transactionService.save(transactionDTO);
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
        TransactionDTO transactionDTO = new TransactionDTO("/api/activate", TransactionType.SYSTEM);
        Optional<UserEntity> user = userApplicationService.activateRegistration(key);
        if (!user.isPresent()) {
            transactionDTO.setCode("400");
            transactionDTO.setMessage("No user was found for this activation key");
            transactionService.save(transactionDTO);
            throw new AccountResourceException("No user was found for this activation key");
        } else {
            transactionDTO.setCode("200");
            transactionDTO.setMessage("Activate Successfully");
            transactionService.save(transactionDTO);
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
        TransactionDTO transactionDTO = new TransactionDTO("/api/account", TransactionType.SYSTEM);
        String userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new AccountResourceException("Current user login not found"));
        Optional<UserEntity> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getLogin().equalsIgnoreCase(userLogin))) {
            transactionDTO.setCode("200");
            transactionDTO.setMessage("Save Account Successfully");
            transactionService.save(transactionDTO);
            throw new EmailAlreadyUsedException();
        }
        Optional<UserEntity> user = userRepository.findOneByLogin(userLogin);
        if (!user.isPresent()) {
            transactionDTO.setCode("400");
            transactionDTO.setMessage("User could not be found");
            transactionService.save(transactionDTO);
            throw new AccountResourceException("User could not be found");
        } else {
            userApplicationService.updateUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(),
                userDTO.getLangKey(), userDTO.getImageUrl());
            transactionDTO.setCode("200");
            transactionDTO.setMessage("Update User Successfully");
            transactionService.save(transactionDTO);
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
        TransactionDTO transactionDTO = new TransactionDTO("/api/account/change-password", TransactionType.SYSTEM);
        if (!checkPasswordLength(passwordChangeDto.getNewPassword())) {
            transactionDTO.setMessage("400");
            transactionDTO.setCode("Invalid Password");
            transactionService.save(transactionDTO);
            throw new InvalidPasswordException();
        } else {
            transactionDTO.setMessage("200");
            transactionDTO.setCode("Change Password Successfully");
            transactionService.save(transactionDTO);
            userApplicationService.changePassword(passwordChangeDto.getCurrentPassword(), passwordChangeDto.getNewPassword());
        }
    }

    /**
     * {@code POST   /account/reset-password/init} : Send an email to reset the password of the user.
     *
     * @param mail the mail of the user.
     */
    @PostMapping(path = "/account/reset-password/init")
    public void requestPasswordReset(@RequestBody String mail) {
        TransactionDTO transactionDTO = new TransactionDTO("/api/account/reset-password/init", TransactionType.SYSTEM);
        Optional<UserEntity> user = userApplicationService.requestPasswordReset(mail);
        if (user.isPresent()) {
            mailService.sendPasswordResetMail(user.get());
            code = "200";
            message = "Reset Password Successfully";
        } else {
            // Pretend the request has been successful to prevent checking which emails really exist
            // but log that an invalid attempt has been made
            code = "400";
            message = "Password reset requested for non existing mail";
        }
        transactionDTO.setCode(code);
        transactionDTO.setMessage(message);
        transactionService.save(transactionDTO);
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
        TransactionDTO transactionDTO = new TransactionDTO("/api/account/reset-password/finish", TransactionType.SYSTEM);
        if (!checkPasswordLength(keyAndPassword.getNewPassword())) {
            transactionDTO.setCode("400");
            transactionDTO.setMessage("Invalid Password");
            transactionService.save(transactionDTO);
            throw new InvalidPasswordException();
        }
        Optional<UserEntity> user =
            userApplicationService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey());
        if (!user.isPresent()) {
            transactionDTO.setCode("400");
            transactionDTO.setMessage("No user was found for this reset key");
            transactionService.save(transactionDTO);
            throw new AccountResourceException("No user was found for this reset key");
        } else {
            transactionDTO.setCode("200");
            transactionDTO.setMessage("Reset password successfully");
            transactionService.save(transactionDTO);
        }
    }

    private static boolean checkPasswordLength(String password) {
        return !StringUtils.isEmpty(password) &&
            password.length() >= ManagedUserVM.PASSWORD_MIN_LENGTH &&
            password.length() <= ManagedUserVM.PASSWORD_MAX_LENGTH;
    }
}
