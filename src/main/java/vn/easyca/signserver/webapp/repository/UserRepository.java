package vn.easyca.signserver.webapp.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.easyca.signserver.webapp.domain.UserEntity;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;
import java.time.LocalDateTime;

/**
 * Spring Data JPA repository for the {@link UserEntity} entity.
 */

public interface UserRepository extends JpaRepository<UserEntity, Long>, UserRepositoryCustom {

    String USERS_BY_LOGIN_CACHE = "usersByLogin";

    String USERS_BY_EMAIL_CACHE = "usersByEmail";

    Optional<UserEntity> findOneByActivationKey(String activationKey);

    List<UserEntity> findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(LocalDateTime dateTime);

    Optional<UserEntity> findOneByResetKey(String resetKey);

    Optional<UserEntity> findOneByEmailIgnoreCase(String email);

    Optional<UserEntity> findOneByLogin(String login);

    @Query(value = "update jhi_user " +
        "set remind_change_password = false " +
        "WHERE login = :login ", nativeQuery = true)
    UserEntity setDefaultRemindChangePassword(@Param("login") String login);

    @EntityGraph(attributePaths = "authorities")
    @Cacheable(cacheNames = USERS_BY_LOGIN_CACHE)
    Optional<UserEntity> findOneWithAuthoritiesByLogin(String login);

    @EntityGraph(attributePaths = "authorities")
    @Cacheable(cacheNames = USERS_BY_EMAIL_CACHE)
    Optional<UserEntity> findOneWithAuthoritiesByEmailIgnoreCase(String email);

    Page<UserEntity> findAllByLoginNot(Pageable pageable, String login);
}
