package vn.easyca.signserver.webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vn.easyca.signserver.webapp.domain.Certificate;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long>, CertificateRepositoryCustom {
    List<Certificate> findByOwnerId(String ownerId);
    Optional<Certificate> findOneBySerial(String serial);
    Optional<Certificate> findOneBySerialAndActiveStatus(String serial, Integer activeStatus);

    @Transactional
    @Modifying
    @Query(value = "update Certificate " +
        "set signatureImageId = :signatureImageId " +
        "where id = :certId ")
    void updateSignatureImageInCert(@Param("signatureImageId") Long signatureImageId, @Param("certId") Long certId);

}
