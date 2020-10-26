package vn.easyca.signserver.infrastructure.database.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.easyca.signserver.infrastructure.database.jpa.entity.CertificateEntity;

import java.util.Optional;

/**
 * Spring Data  repository for the Certificate entity.
 */
@Repository
public interface CertificateJpaRepository extends JpaRepository<CertificateEntity, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM certificate WHERE serial = ?1")
    Optional<CertificateEntity> getCertificateBySerial(String serial);

}
