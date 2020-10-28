package vn.easyca.signserver.infrastructure.database.jpa.repository;

import ch.qos.logback.classic.db.SQLBuilder;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;

import vn.easyca.signserver.infrastructure.database.jpa.entity.UserEntity;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.easyca.signserver.webapp.utils.CommonUntil;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;
import java.time.Instant;

/**
 * Spring Data JPA repository for the {@link UserEntity} entity.
 */

public interface UserRepository extends JpaRepository<UserEntity, Long>, UserRepositoryCustom {

    String USERS_BY_LOGIN_CACHE = "usersByLogin";

    String USERS_BY_EMAIL_CACHE = "usersByEmail";

    Optional<UserEntity> findOneByActivationKey(String activationKey);

    List<UserEntity> findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant dateTime);

    Optional<UserEntity> findOneByResetKey(String resetKey);

    Optional<UserEntity> findOneByEmailIgnoreCase(String email);

    Optional<UserEntity> findOneByLogin(String login);

    @EntityGraph(attributePaths = "authorities")
    @Cacheable(cacheNames = USERS_BY_LOGIN_CACHE)
    Optional<UserEntity> findOneWithAuthoritiesByLogin(String login);

    @EntityGraph(attributePaths = "authorities")
    @Cacheable(cacheNames = USERS_BY_EMAIL_CACHE)
    Optional<UserEntity> findOneWithAuthoritiesByEmailIgnoreCase(String email);

    Page<UserEntity> findAllByLoginNot(Pageable pageable, String login);

}
