package vn.easyca.signserver.webapp.repository;

import vn.easyca.signserver.webapp.domain.SystemConfig;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import vn.easyca.signserver.webapp.enm.SystemConfigKey;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the SystemConfig entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfig, Long> {
    List<SystemConfig> findAllByActivatedIsTrueAndComId(Long comId);
    List<SystemConfig> findAllByActivatedIsTrue();
    Optional<SystemConfig> findByComIdAndKey(Long comId, SystemConfigKey key);
}
