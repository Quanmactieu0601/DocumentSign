package study.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import study.domain.CertPackage;
import study.service.dto.CertPackageDTO;

/**
 * Service Interface for managing {@link study.domain.CertPackage}.
 */
public interface CertPackageService {
    /**
     * Save a certPackage.
     *
     * @param certPackageDTO the entity to save.
     * @return the persisted entity.
     */
    CertPackageDTO save(CertPackageDTO certPackageDTO);

    /**
     * Get all the certPackages.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CertPackageDTO> findAll(Pageable pageable);

    /**
     * Get the "id" certPackage.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CertPackageDTO> findOne(Long id);

    /**
     * Delete the "id" certPackage.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    Optional<CertPackage> findByPackageCode(String packageCode);
}
