package vn.easyca.signserver.webapp.jparepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.easyca.signserver.webapp.domain.CertificateEntity;

import java.util.Optional;

/**
 * Spring Data  repository for the Certificate entity.
 */
@Repository
public interface CertificateRepository extends JpaRepository<CertificateEntity, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM certificate WHERE serial = ?1 limit 1")
    Optional<CertificateEntity> getCertificateBySerial(String serial);

}
