package vn.easyca.signserver.webapp.service.impl;

import vn.easyca.signserver.webapp.service.CertPackageService;
import vn.easyca.signserver.webapp.domain.CertPackage;
import vn.easyca.signserver.webapp.repository.CertPackageRepository;
import vn.easyca.signserver.webapp.service.dto.CertPackageDTO;
import vn.easyca.signserver.webapp.service.mapper.CertPackageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link CertPackage}.
 */
@Service
@Transactional
public class CertPackageServiceImpl implements CertPackageService {

    private final Logger log = LoggerFactory.getLogger(CertPackageServiceImpl.class);

    private final CertPackageRepository certPackageRepository;

    private final CertPackageMapper certPackageMapper;

    public CertPackageServiceImpl(CertPackageRepository certPackageRepository, CertPackageMapper certPackageMapper) {
        this.certPackageRepository = certPackageRepository;
        this.certPackageMapper = certPackageMapper;
    }

    /**
     * Save a certPackage.
     *
     * @param certPackageDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public CertPackageDTO save(CertPackageDTO certPackageDTO) {
        log.debug("Request to save CertPackage : {}", certPackageDTO);
        CertPackage certPackage = certPackageMapper.toEntity(certPackageDTO);
        certPackage = certPackageRepository.save(certPackage);
        return certPackageMapper.toDto(certPackage);
    }

    /**
     * Get all the certPackages.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<CertPackageDTO> findAll(Pageable pageable) {
        log.debug("Request to get all CertPackages");
        return certPackageRepository.findAll(pageable)
            .map(certPackageMapper::toDto);
    }


    /**
     * Get one certPackage by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<CertPackageDTO> findOne(Long id) {
        log.debug("Request to get CertPackage : {}", id);
        return certPackageRepository.findById(id)
            .map(certPackageMapper::toDto);
    }

    /**
     * Delete the certPackage by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete CertPackage : {}", id);
        certPackageRepository.deleteById(id);
    }
}
