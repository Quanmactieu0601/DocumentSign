package vn.easyca.signserver.webapp.repository;

import vn.easyca.signserver.webapp.domain.SignatureTemplate;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data  repository for the SignatureTemplate entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SignatureTemplateRepository extends JpaRepository<SignatureTemplate, Long>, SignatureTemplateRepositoryCustom {
    Optional<SignatureTemplate> findOneByUserId(Long userId);
}
