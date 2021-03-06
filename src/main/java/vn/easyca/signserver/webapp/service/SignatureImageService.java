package vn.easyca.signserver.webapp.service;

import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.webapp.domain.SignatureImage;
import vn.easyca.signserver.webapp.service.dto.SignatureImageDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link vn.easyca.signserver.webapp.domain.SignatureImage}.
 */
public interface SignatureImageService {

    /**
     * Save a signatureImage.
     *
     * @param signatureImageDTO the entity to save.
     * @return the persisted entity.
     */
    SignatureImageDTO save(SignatureImageDTO signatureImageDTO);

    /**
     * Get all the signatureImages.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SignatureImageDTO> findAll(Pageable pageable);


    /**
     * Get the "id" signatureImage.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SignatureImageDTO> findOne(Long id);

    /**
     * Delete the "id" signatureImage.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    String getBase64Image(Long id) throws ApplicationException;

    SignatureImage saveSignatureImageByCert(String base64Image, Long certId) throws ApplicationException;

}
