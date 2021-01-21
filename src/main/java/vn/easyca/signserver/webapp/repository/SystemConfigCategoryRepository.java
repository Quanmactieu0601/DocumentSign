package vn.easyca.signserver.webapp.repository;

import vn.easyca.signserver.webapp.domain.SystemConfigCategory;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the SystemConfigCategory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SystemConfigCategoryRepository extends JpaRepository<SystemConfigCategory, Long> {
}
