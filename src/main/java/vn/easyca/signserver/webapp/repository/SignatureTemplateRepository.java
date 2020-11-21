package vn.easyca.signserver.webapp.repository;

import vn.easyca.signserver.infrastructure.database.jpa.entity.UserEntity;
import vn.easyca.signserver.webapp.domain.SignatureTemplate;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import vn.easyca.signserver.webapp.service.dto.SignatureTemplateDTO;

import java.util.Optional;

/**
 * Spring Data  repository for the SignatureTemplate entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SignatureTemplateRepository extends JpaRepository<SignatureTemplate, Long> {
    Optional<SignatureTemplate> findOneByUserId(Long userId);
}
