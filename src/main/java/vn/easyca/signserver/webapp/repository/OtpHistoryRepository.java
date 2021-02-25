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
public interface OtpHistoryRepository extends JpaRepository<OtpHistory, Long>, OtpHistoryRepositoryCustom {
}
