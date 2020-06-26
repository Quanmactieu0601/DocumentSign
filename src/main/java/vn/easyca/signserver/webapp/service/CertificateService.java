package vn.easyca.signserver.webapp.service;

import vn.easyca.signserver.webapp.domain.Certificate;
import vn.easyca.signserver.webapp.repository.CertificateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Certificate}.
 */
@Service
@Transactional
public class CertificateService {

    private final Logger log = LoggerFactory.getLogger(CertificateService.class);

    private final CertificateRepository certificateRepository;

    public CertificateService(CertificateRepository certificateRepository) {
        this.certificateRepository = certificateRepository;
    }

    /**
     * Save a certificate.
     *
     * @param certificate the entity to save.
     * @return the persisted entity.
     */
    public Certificate save(Certificate certificate) {
        log.debug("Request to save Certificate : {}", certificate);
        return certificateRepository.save(certificate);
    }

    /**
     * Get all the certificates.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Certificate> findAll(Pageable pageable) {
        log.debug("Request to get all Certificates");
        return certificateRepository.findAll(pageable);
    }


    /**
     * Get one certificate by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Certificate> findOne(Long id) {
        log.debug("Request to get Certificate : {}", id);
        return certificateRepository.findById(id);
    }

    /**
     * Delete the certificate by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Certificate : {}", id);
        certificateRepository.deleteById(id);
    }
}
