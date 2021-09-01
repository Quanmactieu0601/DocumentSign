package vn.easyca.signserver.webapp.service;

import vn.easyca.signserver.webapp.domain.CertPackage;
import vn.easyca.signserver.webapp.service.dto.CertPackageDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link vn.easyca.signserver.webapp.domain.CertPackage}.
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
