package vn.easyca.signserver.webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.easyca.signserver.webapp.domain.Certificate;
/**
 * Spring Data  repository for the Certificate entity.
 */
@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    Certificate getCertificateBySerial(String serial);
}
