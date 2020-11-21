package vn.easyca.signserver.webapp.repository;

import vn.easyca.signserver.webapp.domain.SignatureTemplate;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the SignatureTemplate entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SignatureTemplateRepository extends JpaRepository<SignatureTemplate, Long> {
}
