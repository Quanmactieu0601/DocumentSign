package vn.easyca.signserver.webapp.service;

import com.google.common.base.Strings;

import vn.easyca.signserver.webapp.config.Constants;
import vn.easyca.signserver.webapp.domain.Authority;
import vn.easyca.signserver.webapp.domain.UserEntity;
import vn.easyca.signserver.webapp.repository.AuthorityRepository;
import vn.easyca.signserver.webapp.repository.UserRepository;
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
import vn.easyca.signserver.webapp.service.error.*;

import java.time.LocalDateTime;
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
            .filter(user -> user.getResetDate().isAfter(LocalDateTime.now().minusSeconds(86400)))
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
                user.setResetDate(LocalDateTime.now());
                this.clearUserCaches(user);
                return user;
            });
    }

    /*public BaseResponseVM registerListUser(List<UserDTO> userDTOList, String password) {
        StringBuilder errorMes = new StringBuilder();
        int row = 1;
        for (UserDTO userDTO : userDTOList) {
            if (userDTO.getLogin().length() == 0) {
                errorMes.append("Check required field (*) as 'login' in row " + row + "!");
                errorMes.append("<br />");
            }
            userRepository.findOneByLogin(userDTO.getLogin().toLowerCase()).ifPresent(existingUser -> {
                boolean removed = removeNonActivatedUser(existingUser);
                if (!removed) {
                    errorMes.append("User name " + existingUser.getLogin() + "already exist !");
                    errorMes.append("<br />");
                }
            });
            if (userDTO.getEmail().length() != 0) {
                userRepository.findOneByEmailIgnoreCase(userDTO.getEmail()).ifPresent(existingUser -> {
                    boolean removed = removeNonActivatedUser(existingUser);
                    if (!removed) {
                        errorMes.append("Email " + existingUser.getEmail() + " already exist !");
                        errorMes.append("<br />");
                    }
                });
            }
            if (userDTO.getCountry().length() == 0 && userDTO.getCommonName().length() == 0 && userDTO.getOrganizationName().length() == 0 && userDTO.getOrganizationUnit().length() == 0 && userDTO.getLocalityName().length() == 0 && userDTO.getStateName().length() == 0) {
                errorMes.append(userDTO.getLogin() + " you must fill at least one value from Common Name to Country in excel table in row " + row + " !");
                errorMes.append("<br />");
            } else if (userDTO.getCountry().length() != 0 && (userDTO.getCountry().length() != 2)) {
                errorMes.append(userDTO.getLogin() + " length of column Country must be 2 character in row " + row + " !");
                errorMes.append("<br />");
            }
            if (errorMes.length() == 0) {
                UserEntity newUserEntity = new UserEntity();
                String encryptedPassword = passwordEncoder.encode(password);
                newUserEntity.setLogin(userDTO.getLogin().toLowerCase());
                newUserEntity.setPassword(encryptedPassword);
                newUserEntity.setFirstName(userDTO.getFirstName());
                newUserEntity.setLastName(userDTO.getLastName());
                if (userDTO.getEmail().length() != 0) {
                    newUserEntity.setEmail(userDTO.getEmail().toLowerCase());
                }
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
            }

            row++;
        }

        if (errorMes.length() == 0) {
            return new BaseResponseVM(200, null, "Created List User!");
        } else {
            System.out.println(errorMes.toString());
            return new BaseResponseVM(400, null, errorMes.toString());
        }
    }*/


    public UserEntity registerUser(UserDTO userDTO, String password) {
        userRepository.findOneByLogin(userDTO.getLogin().toLowerCase()).ifPresent(existingUser -> {
            throw new UsernameAlreadyUsedException();
        });
        if (!Strings.isNullOrEmpty(userDTO.getEmail())) {
            userRepository.findOneByEmailIgnoreCase(userDTO.getEmail()).ifPresent(existingUser -> {
                throw new EmailAlreadyUsedException();
            });
        }
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
        newUserEntity.setActivated(true);
        //test_branch, develope_branch error when create user by excel because not set RemindChangePassword yet
        newUserEntity.setRemindChangePassword(true);
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

    public boolean createUser(String username, String password, String fullName) {
        Optional<UserEntity> userEntity = this.getUserWithAuthoritiesByLogin(username);
        if (userEntity.isPresent())
            return false;
        UserDTO userDTO = new UserDTO();
        userDTO.setLogin(username);
        userDTO.setFirstName(fullName);
        userDTO.setPassword(password);
        Set<String> authorities = new HashSet<>();
        authorities.add(AuthoritiesConstants.USER);
        userDTO.setAuthorities(authorities);
        this.createUser(userDTO);
        return true;
    }

    public UserEntity createUser(UserDTO userDTO) {
        userRepository.findOneByLogin(userDTO.getLogin().toLowerCase()).ifPresent(existingUser -> {
            boolean removed = removeNonActivatedUser(existingUser);
            if (!removed) {
                throw new UsernameAlreadyUsedException();
            }
        });

        if (userDTO.getEmail() != null) {
            userRepository.findOneByEmailIgnoreCase(userDTO.getEmail()).ifPresent(existingUser -> {
                boolean removed = removeNonActivatedUser(existingUser);
                if (!removed) {
                    throw new EmailAlreadyUsedException();
                }
            });
        }

        UserEntity newUserEntity = new UserEntity();
        String encryptedPassword = passwordEncoder.encode(userDTO.getPassword());
        newUserEntity.setLogin(userDTO.getLogin().toLowerCase());
        // new user gets initially a generated password
        newUserEntity.setPassword(encryptedPassword);
        newUserEntity.setFirstName(userDTO.getFirstName());
        newUserEntity.setLastName(userDTO.getLastName());
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
        newUserEntity.setRemindChangePassword(true);

        Set<Authority> authorities = userDTO
            .getAuthorities()
            .stream()
            .map(authorityRepository::findById)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toSet());
        newUserEntity.setAuthorities(authorities);
        if( userDTO.getAuthorities().size() == 0){
            authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        }

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
                if (!Strings.isNullOrEmpty(userDTO.getPassword())) {
                    user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
                }
                user.setLogin(userDTO.getLogin().toLowerCase());
                user.setFirstName(userDTO.getFirstName());
                user.setLastName(userDTO.getLastName());
                try{
                    if (userDTO.getEmail() != null) {
                        user.setEmail(userDTO.getEmail().toLowerCase());
                    }
                    if (userDTO.getEmail().length() == 0) {
                        user.setEmail(null);
                    }
                }
                catch (NullPointerException ex) {
                    System.out.println("Exception in NPE1()" + ex);
                }
                user.setCommonName(userDTO.getCommonName());
                user.setOrganizationName(userDTO.getOrganizationName());
                user.setOrganizationUnit(userDTO.getOrganizationUnit());
                user.setLocalityName(userDTO.getLocalityName());
                user.setStateName(userDTO.getStateName());
                user.setCountry(userDTO.getCountry());
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
                user.setRemindChangePassword(false);
                log.debug("Changed password for User: {}", user);
            });
    }

    public Boolean remindChangePassword(String login) {
        return userRepository.findOneByLogin(login).get().getRemindChangePassword();
    }

    public void setDefaultOfRemindChangePassword(String login) {
        userRepository.setDefaultOfRemindChangePassword(login);
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getAllManagedUsers(Pageable pageable) {
        return userRepository.findAllByLoginNot(pageable, Constants.ANONYMOUS_USER).map(UserDTO::new);
    }



    @Transactional(readOnly = true)
    public Page<UserDTO> getByFilter(Pageable pageable, String account, String name, String email, String ownerId, String commonName, String country, String phone, boolean activated) {
        return userRepository.findByFilter(pageable, Constants.ANONYMOUS_USER, account, name, email, ownerId, commonName, country, phone, activated).map(UserDTO::new);
    }

    @Transactional(readOnly = true)
    public Optional<UserEntity> getUserWithAuthoritiesByLogin(String login) {
        return userRepository.findOneWithAuthoritiesByLogin(login);
    }

    @Transactional(readOnly = true)
    public Optional<UserEntity> getUserById(Long id) {
        return userRepository.findById(id);
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
            .findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(LocalDateTime.now().minus(3, ChronoUnit.DAYS))
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
        return authorityRepository.getAuthoritiesName();
    }


    private void clearUserCaches(UserEntity userEntity) {
        Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE)).evict(userEntity.getLogin());
        if (userEntity.getEmail() != null) {
            Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE)).evict(userEntity.getEmail());
        }
    }

    @Transactional(readOnly = true)
    public Optional<UserEntity> getUserEntity() {
        return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin);
    }
}
