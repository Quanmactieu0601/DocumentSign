package vn.easyca.signserver.webapp.repository;

import org.springframework.data.repository.query.Param;
import vn.easyca.signserver.webapp.domain.SignatureImage;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data  repository for the SignatureImage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SignatureImageRepository extends JpaRepository<SignatureImage, Long> {
    Optional<SignatureImage> findOneByUserId(Long userId);

    Optional<SignatureImage> getSignatureImageById(Long id);
}
