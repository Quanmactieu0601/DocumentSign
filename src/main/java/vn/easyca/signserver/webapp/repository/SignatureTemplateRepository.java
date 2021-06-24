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

    // TODO: lay ban moi nhat hien tai, tuy nhien se sua lai tim theo trang thai active, moi user chi duoc active 1 mau chu ky tai 1 thoi diem
    Optional<SignatureTemplate> findFirstByUserIdOrderByCreatedDateDesc(Long userId);
    Optional<SignatureTemplate[]> findAllByUserId(Long userId);
}
