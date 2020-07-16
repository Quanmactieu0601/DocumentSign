package vn.easyca.signserver.webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.easyca.signserver.webapp.domain.Certificate;
/**
 * Spring Data  repository for the Certificate entity.
 */
@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    @Query(nativeQuery = true,value = "select top(1) c from Certificate c where lower(c.serial) = lower(:serial)")
    Certificate getCertificateBySerial(@Param("serial") String serial);
}
