package vn.easyca.signserver.infrastructure.database.jpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.easyca.signserver.infrastructure.database.jpa.entity.CertificateEntity;
import vn.easyca.signserver.infrastructure.database.jpa.entity.CertificateEntity_;
import vn.easyca.signserver.infrastructure.database.jpa.entity.UserEntity;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the Certificate entity.
 */
@Repository
public interface CertificateJpaRepository extends JpaRepository<CertificateEntity, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM certificate WHERE serial = ?1")
    Optional<CertificateEntity> getCertificateBySerial(String serial);

    List<CertificateEntity> findByOwnerId(String ownerId);

//    @Query(nativeQuery = true, value = "SELECT * FROM certificate WHERE owner_id = ?1")
//    List<CertificateEntity> getCertificateByOwnerId(String ownerId);
}
