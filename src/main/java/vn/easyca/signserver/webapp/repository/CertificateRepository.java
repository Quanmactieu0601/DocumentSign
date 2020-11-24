package vn.easyca.signserver.webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.easyca.signserver.webapp.domain.Certificate;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long>, CertificateRepositoryCustom {
    List<Certificate> findByOwnerId(String ownerId);
    Optional<Certificate> findOneBySerial(String serial);
}
