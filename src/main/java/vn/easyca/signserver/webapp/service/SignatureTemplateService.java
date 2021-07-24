package vn.easyca.signserver.webapp.service;

import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.webapp.domain.SignatureTemplate;
import vn.easyca.signserver.webapp.service.dto.SignatureExampleDTO;
import vn.easyca.signserver.webapp.service.dto.SignatureTemplateDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.Optional;

/**
 * Service Interface for managing {@link vn.easyca.signserver.webapp.domain.SignatureTemplate}.
 */
public interface SignatureTemplateService {

    /**
     * Save a signatureTemplate.
     *
     * @param signatureTemplateDTO the entity to save.
     * @return the persisted entity.
     */
    SignatureTemplateDTO save(SignatureTemplateDTO signatureTemplateDTO) throws ApplicationException;

    /**
     * Get all the signatureTemplates.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SignatureTemplateDTO> findAll(Pageable pageable);


    /**
     * Get the "id" signatureTemplate.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SignatureTemplateDTO> findOne(Long id);

    /**
     * Delete the "id" signatureTemplate.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    Page<SignatureTemplateDTO> findAllWithUserId(Pageable pageable, Long userId) throws ApplicationException, IOException;

    String getSignatureExample(SignatureExampleDTO signatureExampleDTO) throws ApplicationException;
}
