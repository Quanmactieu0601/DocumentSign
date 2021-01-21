package vn.easyca.signserver.webapp.repository;

import org.springframework.data.repository.query.Param;
import vn.easyca.signserver.webapp.domain.OtpHistory;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Spring Data  repository for the OtpHistory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OtpHistoryRepository extends JpaRepository<OtpHistory, Long> {

    // TODO: change this query string when change DB system
    @Query(value = "select * from otp_history a where a.user_id = :userId and a.secret_key = :secretKey and a.otp = :otp and a.action_time <= :authenTime and a.expire_time >= :authenTime ORDER BY a.action_time desc LIMIT 1", nativeQuery = true)
    Optional<OtpHistory> findTop1By(@Param("userId") Long userId, @Param("secretKey") String secretKey, @Param("otp") String otp,
                                   @Param("authenTime") LocalDateTime authenTime);
}
