package study.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import study.service.dto.CertImportSuccessDTO;
import study.service.dto.SignatureImageDTO;
import vn.easyca.signserver.core.exception.ApplicationException;

/**
 * Service Interface for managing {@link study.domain.SignatureImage}.
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

    SignatureImageDTO saveSignatureImageByCert(String base64Image, Long certId) throws ApplicationException;

    List<CertImportSuccessDTO> saveSignatureImageByPersonalID(MultipartFile[] imageFiles) throws ApplicationException;
}
