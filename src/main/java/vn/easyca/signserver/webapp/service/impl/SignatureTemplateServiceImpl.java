package vn.easyca.signserver.webapp.service.impl;

import org.hibernate.loader.plan.spi.Return;
import vn.easyca.signserver.pki.sign.utils.StringUtils;
import vn.easyca.signserver.webapp.repository.UserRepository;
import vn.easyca.signserver.webapp.service.SignatureTemplateService;
import vn.easyca.signserver.webapp.domain.SignatureTemplate;
import vn.easyca.signserver.webapp.repository.SignatureTemplateRepository;
import vn.easyca.signserver.webapp.service.dto.SignatureTemplateDTO;
import vn.easyca.signserver.webapp.service.mapper.SignatureTemplateMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link SignatureTemplate}.
 */
@Service
@Transactional
public class SignatureTemplateServiceImpl implements SignatureTemplateService {

    private final Logger log = LoggerFactory.getLogger(SignatureTemplateServiceImpl.class);

    private final SignatureTemplateRepository signatureTemplateRepository;

    private final SignatureTemplateMapper signatureTemplateMapper;

    private final UserRepository userRepository;

    public SignatureTemplateServiceImpl(SignatureTemplateRepository signatureTemplateRepository, SignatureTemplateMapper signatureTemplateMapper, UserRepository userRepository) {
        this.signatureTemplateRepository = signatureTemplateRepository;
        this.signatureTemplateMapper = signatureTemplateMapper;
        this.userRepository = userRepository;
    }

    /**
     * Save a signatureTemplate.
     *
     * @param signatureTemplateDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public SignatureTemplateDTO save(SignatureTemplateDTO signatureTemplateDTO) {
        log.debug("Request to save SignatureTemplate : {}", signatureTemplateDTO);
        SignatureTemplate signatureTemplate = signatureTemplateMapper.toEntity(signatureTemplateDTO);
        signatureTemplate = signatureTemplateRepository.save(signatureTemplate);
        return signatureTemplateMapper.toDto(signatureTemplate);
    }

    /**
     * Get all the signatureTemplates.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<SignatureTemplateDTO> findAll(Pageable pageable) {
        log.debug("Request to get all SignatureTemplates");
        return signatureTemplateRepository.findAllSignatureTemplate(pageable);
    }



    /**
     * Get one signatureTemplate by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<SignatureTemplateDTO> findOne(Long id) {
        log.debug("Request to get SignatureTemplate : {}", id);
        return signatureTemplateRepository.findById(id)
            .map(signatureTemplateMapper::toDto);
    }

    /**
     * Delete the signatureTemplate by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete SignatureTemplate : {}", id);
        signatureTemplateRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SignatureTemplateDTO> findAllWithUserId(Pageable pageable, Long userId) {
        log.debug("Request to get SignatureImage with id : {}", userId);
        return signatureTemplateRepository.findAllSignatureTemplateByUserId(pageable, userId);
    }
}
