package vn.easyca.signserver.webapp.repository;

import vn.easyca.signserver.webapp.domain.CertPackage;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data  repository for the CertPackage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CertPackageRepository extends JpaRepository<CertPackage, Long> {
    Optional<CertPackage> findOneByPackageCode(String packageCode);
}
