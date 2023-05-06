package study.service;

import java.io.IOException;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import study.domain.SignatureTemplate;
import study.service.dto.SignatureExampleDTO;
import study.service.dto.SignatureTemplateDTO;
import vn.easyca.signserver.core.exception.ApplicationException;

/**
 * Service Interface for managing {@link study.domain.SignatureTemplate}.
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

    Optional<SignatureTemplate[]> findAllTemplatesByUserLoggedIn() throws ApplicationException;

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
