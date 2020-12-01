package vn.easyca.signserver.webapp.repository;

import vn.easyca.signserver.webapp.domain.SignatureImage;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the SignatureImage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SignatureImageRepository extends JpaRepository<SignatureImage, Long> {
}
