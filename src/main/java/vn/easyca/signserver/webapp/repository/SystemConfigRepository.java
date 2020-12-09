package vn.easyca.signserver.webapp.repository;

import vn.easyca.signserver.webapp.domain.SystemConfig;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the SystemConfig entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfig, Long> {
    List<SystemConfig> findAllByActivatedIsTrueAndComId(Long comId);
    List<SystemConfig> findAllByActivatedIsTrue();
}
