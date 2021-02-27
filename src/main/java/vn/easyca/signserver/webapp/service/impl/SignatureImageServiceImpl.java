package vn.easyca.signserver.webapp.service.impl;

import org.springframework.web.multipart.MultipartFile;
import vn.easyca.signserver.webapp.service.SignatureImageService;
import vn.easyca.signserver.webapp.domain.SignatureImage;
import vn.easyca.signserver.webapp.repository.SignatureImageRepository;
import vn.easyca.signserver.webapp.service.dto.SignatureImageDTO;
import vn.easyca.signserver.webapp.service.mapper.SignatureImageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

/**
 * Service Implementation for managing {@link SignatureImage}.
 */
@Service
@Transactional
public class SignatureImageServiceImpl implements SignatureImageService {

    private final Logger log = LoggerFactory.getLogger(SignatureImageServiceImpl.class);

    private final SignatureImageRepository signatureImageRepository;

    private final SignatureImageMapper signatureImageMapper;

    public SignatureImageServiceImpl(SignatureImageRepository signatureImageRepository, SignatureImageMapper signatureImageMapper) {
        this.signatureImageRepository = signatureImageRepository;
        this.signatureImageMapper = signatureImageMapper;
    }

    /**
     * Save a signatureImage.
     *
     * @param signatureImageDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public SignatureImageDTO save(SignatureImageDTO signatureImageDTO) {
        log.debug("Request to save SignatureImage : {}", signatureImageDTO);
        SignatureImage signatureImage = signatureImageMapper.toEntity(signatureImageDTO);
        signatureImage = signatureImageRepository.save(signatureImage);
        return signatureImageMapper.toDto(signatureImage);
    }

    /**
     * Get all the signatureImages.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<SignatureImageDTO> findAll(Pageable pageable) {
        log.debug("Request to get all SignatureImages");
        return signatureImageRepository.findAll(pageable)
            .map(signatureImageMapper::toDto);
    }


    /**
     * Get one signatureImage by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<SignatureImageDTO> findOne(Long id) {
        log.debug("Request to get SignatureImage : {}", id);
        return signatureImageRepository.findById(id)
            .map(signatureImageMapper::toDto);
    }

    /**
     * Delete the signatureImage by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete SignatureImage : {}", id);
        signatureImageRepository.deleteById(id);
    }

    @Override
    public String getBase64Image(Long id) {
        return signatureImageRepository.getSignatureImageById(id).get().getImgData();
    }

}
