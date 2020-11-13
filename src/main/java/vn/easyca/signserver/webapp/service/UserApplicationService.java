package vn.easyca.signserver.webapp.service;

import vn.easyca.signserver.core.dto.CertificateGenerateResult;
import vn.easyca.signserver.webapp.config.Constants;
import vn.easyca.signserver.infrastructure.database.jpa.entity.Authority;
import vn.easyca.signserver.infrastructure.database.jpa.entity.UserEntity;
import vn.easyca.signserver.infrastructure.database.jpa.repository.AuthorityRepository;
import vn.easyca.signserver.infrastructure.database.jpa.repository.UserRepository;
import vn.easyca.signserver.webapp.security.AuthoritiesConstants;
import vn.easyca.signserver.webapp.security.SecurityUtils;
import vn.easyca.signserver.webapp.service.dto.UserDTO;

import io.github.jhipster.security.RandomUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.easyca.signserver.webapp.service.error.EmailAlreadyUsedException;
import vn.easyca.signserver.webapp.service.error.InvalidPasswordException;
import vn.easyca.signserver.webapp.service.error.UsernameAlreadyUsedException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserApplicationService {

    private final Logger log = LoggerFactory.getLogger(UserApplicationService.class);

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthorityRepository authorityRepository;

    private final CacheManager cacheManager;

    public UserApplicationService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthorityRepository authorityRepository, CacheManager cacheManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
        this.cacheManager = cacheManager;
    }

    public Optional<UserEntity> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        return userRepository.findOneByActivationKey(key)
            .map(user -> {
                // activate given user for the registration key.
                user.setActivated(true);
                user.setActivationKey(null);
                this.clearUserCaches(user);
                log.debug("Activated user: {}", user);
                return user;
            });
    }

    public Optional<UserEntity> completePasswordReset(String newPassword, String key) {
        log.debug("Reset user password for reset key {}", key);
        return userRepository.findOneByResetKey(key)
            .filter(user -> user.getResetDate().isAfter(Instant.now().minusSeconds(86400)))
            .map(user -> {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setResetKey(null);
                user.setResetDate(null);
                this.clearUserCaches(user);
                return user;
            });
    }

    public Optional<UserEntity> requestPasswordReset(String mail) {
        return userRepository.findOneByEmailIgnoreCase(mail)
            .filter(UserEntity::getActivated)
            .map(user -> {
                user.setResetKey(RandomUtil.generateResetKey());
                user.setResetDate(Instant.now());
                this.clearUserCaches(user);
                return user;
            });
    }

    public UserEntity registerUser(UserDTO userDTO, String password) {
        userRepository.findOneByLogin(userDTO.getLogin().toLowerCase()).ifPresent(existingUser -> {
            boolean removed = removeNonActivatedUser(existingUser);
            if (!removed) {
                throw new UsernameAlreadyUsedException();
            }
        });

        userRepository.findOneByEmailIgnoreCase(userDTO.getEmail()).ifPresent(existingUser -> {
            boolean removed = removeNonActivatedUser(existingUser);
            if (!removed) {
                throw new EmailAlreadyUsedException();
            }
        });

        UserEntity newUserEntity = new UserEntity();
        String encryptedPassword = passwordEncoder.encode(password);
        newUserEntity.setLogin(userDTO.getLogin().toLowerCase());
        // new user gets initially a generated password
        newUserEntity.setPassword(encryptedPassword);
        newUserEntity.setFirstName(userDTO.getFirstName());
        newUserEntity.setLastName(userDTO.getLastName());
        if (userDTO.getEmail() != null) {
            newUserEntity.setEmail(userDTO.getEmail().toLowerCase());
        }
        newUserEntity.setOwnerId(userDTO.getOwnerId());
        newUserEntity.setPhone(userDTO.getPhone());
        newUserEntity.setCommonName(userDTO.getCommonName());
        newUserEntity.setOrganizationName(userDTO.getOrganizationName());
        newUserEntity.setOrganizationUnit(userDTO.getOrganizationUnit());
        newUserEntity.setLocalityName(userDTO.getLocalityName());
        newUserEntity.setStateName(userDTO.getStateName());
        newUserEntity.setCountry(userDTO.getCountry());
        newUserEntity.setImageUrl(userDTO.getImageUrl());
        newUserEntity.setLangKey(userDTO.getLangKey());
        // new user is not active
        newUserEntity.setActivated(false);
        // new user gets registration key
        newUserEntity.setActivationKey(RandomUtil.generateActivationKey());
        Set<Authority> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        newUserEntity.setAuthorities(authorities);
        userRepository.save(newUserEntity);
        this.clearUserCaches(newUserEntity);
        log.debug("Created Information for User: {}", newUserEntity);
        return newUserEntity;
    }

    private boolean removeNonActivatedUser(UserEntity existingUserEntity) {
        if (existingUserEntity.getActivated()) {
            return false;
        }
        userRepository.delete(existingUserEntity);
        userRepository.flush();
        this.clearUserCaches(existingUserEntity);
        return true;
    }


    public UserEntity createUser(UserDTO userDTO, String password) {

        userRepository.findOneByLogin(userDTO.getLogin().toLowerCase()).ifPresent(existingUser -> {
            boolean removed = removeNonActivatedUser(existingUser);
            if (!removed) {
                throw new UsernameAlreadyUsedException();
            }
        });

        if(userDTO.getEmail() != null) {
            userRepository.findOneByEmailIgnoreCase(userDTO.getEmail()).ifPresent(existingUser -> {
                boolean removed = removeNonActivatedUser(existingUser);
                if (!removed) {
                    throw new EmailAlreadyUsedException();
                }
            });
        }

        UserEntity newUserEntity = new UserEntity();
        String encryptedPassword = passwordEncoder.encode(password);
        newUserEntity.setLogin(userDTO.getLogin().toLowerCase());
        // new user gets initially a generated password
        newUserEntity.setPassword(encryptedPassword);
        newUserEntity.setFirstName(userDTO.getFirstName());
        newUserEntity.setLastName(userDTO.getLastName());
        newUserEntity.setOwnerId(userDTO.getOwnerId());
        newUserEntity.setPhone(userDTO.getPhone());
        newUserEntity.setCommonName(userDTO.getCommonName());
        newUserEntity.setOrganizationName(userDTO.getOrganizationName());
        newUserEntity.setOrganizationUnit(userDTO.getOrganizationUnit());
        newUserEntity.setLocalityName(userDTO.getLocalityName());
        newUserEntity.setStateName(userDTO.getStateName());
        newUserEntity.setCountry(userDTO.getCountry());
        if (userDTO.getEmail() != null) {
            newUserEntity.setEmail(userDTO.getEmail().toLowerCase());
        }
        newUserEntity.setImageUrl(userDTO.getImageUrl());
        newUserEntity.setLangKey(userDTO.getLangKey());
        // new user is not active
        newUserEntity.setActivated(true);
        // new user gets registration key
        newUserEntity.setActivationKey(RandomUtil.generateActivationKey());
        Set<Authority> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        newUserEntity.setAuthorities(authorities);
        userRepository.save(newUserEntity);
        this.clearUserCaches(newUserEntity);
        log.debug("Created Information for User: {}", newUserEntity);
        return newUserEntity;
    }

    /**
     * Update all information for a specific user, and return the modified user.
     *
     * @param userDTO user to update.
     * @return updated user.
     */
    public Optional<UserDTO> updateUser(UserDTO userDTO) {
        return Optional.of(userRepository
            .findById(userDTO.getId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(user -> {
                this.clearUserCaches(user);
                user.setLogin(userDTO.getLogin().toLowerCase());
                user.setFirstName(userDTO.getFirstName());
                user.setLastName(userDTO.getLastName());
                if (userDTO.getEmail() != null) {
                    user.setEmail(userDTO.getEmail().toLowerCase());
                }
                user.setCommonName(userDTO.getCommonName());
                user.setOrganizationName(userDTO.getOrganizationName());
                user.setOrganizationUnit(userDTO.getOrganizationUnit());
                user.setLocalityName(userDTO.getLocalityName());
                user.setStateName(userDTO.getStateName());
                user.setCountry(userDTO.getCountry());
                user.setOwnerId(userDTO.getOwnerId());
                user.setPhone(userDTO.getPhone());
                user.setImageUrl(userDTO.getImageUrl());
                user.setActivated(userDTO.isActivated());
                user.setLangKey(userDTO.getLangKey());
                Set<Authority> managedAuthorities = user.getAuthorities();
                managedAuthorities.clear();
                userDTO.getAuthorities().stream()
                    .map(authorityRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(managedAuthorities::add);
                this.clearUserCaches(user);
                log.debug("Changed Information for User: {}", user);
                return user;
            })
            .map(UserDTO::new);
    }

    public void deleteUser(String login) {
        userRepository.findOneByLogin(login).ifPresent(user -> {
            userRepository.delete(user);
            this.clearUserCaches(user);
            log.debug("Deleted User: {}", user);
        });
    }

    /**
     * Update basic information (first name, last name, email, language) for the current user.
     *
     * @param firstName first name of user.
     * @param lastName  last name of user.
     * @param email     email id of user.
     * @param langKey   language key.
     * @param imageUrl  image URL of user.
     */
    public void updateUser(String firstName, String lastName, String email, String langKey, String imageUrl) {
        SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .ifPresent(user -> {
                user.setFirstName(firstName);
                user.setLastName(lastName);
                if (email != null) {
                    user.setEmail(email.toLowerCase());
                }
                user.setLangKey(langKey);
                user.setImageUrl(imageUrl);
                this.clearUserCaches(user);
                log.debug("Changed Information for User: {}", user);
            });
    }


    @Transactional
    public void changePassword(String currentClearTextPassword, String newPassword) {
        SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .ifPresent(user -> {
                String currentEncryptedPassword = user.getPassword();
                if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {
                    throw new InvalidPasswordException();
                }
                String encryptedPassword = passwordEncoder.encode(newPassword);
                user.setPassword(encryptedPassword);
                this.clearUserCaches(user);
                log.debug("Changed password for User: {}", user);
            });
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getAllManagedUsers(Pageable pageable) {
        return userRepository.findAllByLoginNot(pageable, Constants.ANONYMOUS_USER).map(UserDTO::new);
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getByFilter(Pageable pageable, String account, String name, String email, String ownerId, String commonName, String country, String phone ) {
        return userRepository.findByFilter(pageable, Constants.ANONYMOUS_USER, account, name, email, ownerId, commonName, country, phone).map(UserDTO::new);
    }

    @Transactional(readOnly = true)
    public Optional<UserEntity> getUserWithAuthoritiesByLogin(String login) {
        return userRepository.findOneWithAuthoritiesByLogin(login);
    }

    @Transactional(readOnly = true)
    public Optional<UserEntity> getUserWithAuthorities() {
        return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneWithAuthoritiesByLogin);
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        userRepository
            .findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant.now().minus(3, ChronoUnit.DAYS))
            .forEach(user -> {
                log.debug("Deleting not activated user {}", user.getLogin());
                userRepository.delete(user);
                this.clearUserCaches(user);
            });
    }

    /**
     * Gets a list of all the authorities.
     *
     * @return a list of all the authorities.
     */
    @Transactional(readOnly = true)
    public List<String> getAuthorities() {
        return authorityRepository.findAll().stream().map(Authority::getName).collect(Collectors.toList());
    }


    private void clearUserCaches(UserEntity userEntity) {
        Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE)).evict(userEntity.getLogin());
        if (userEntity.getEmail() != null) {
            Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE)).evict(userEntity.getEmail());
        }
    }
}
