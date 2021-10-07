package vn.easyca.signserver.webapp.service.impl;

import org.apache.commons.io.IOUtils;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageImpl;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.webapp.domain.UserEntity;
import vn.easyca.signserver.webapp.enm.SignatureTemplateParserType;
import vn.easyca.signserver.webapp.repository.UserRepository;
import vn.easyca.signserver.webapp.service.CoreParserService;
import vn.easyca.signserver.webapp.service.FileResourceService;
import vn.easyca.signserver.webapp.service.SignatureTemplateService;
import vn.easyca.signserver.webapp.domain.SignatureTemplate;
import vn.easyca.signserver.webapp.repository.SignatureTemplateRepository;
import vn.easyca.signserver.webapp.service.dto.CoreParserDTO;
import vn.easyca.signserver.webapp.service.dto.SignatureExampleDTO;
import vn.easyca.signserver.webapp.service.dto.SignatureTemplateDTO;
import vn.easyca.signserver.webapp.service.mapper.SignatureTemplateMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.easyca.signserver.webapp.service.parser.SignatureTemplateParseService;
import vn.easyca.signserver.webapp.service.parser.SignatureTemplateParserFactory;
import vn.easyca.signserver.webapp.utils.AccountUtils;
import vn.easyca.signserver.webapp.utils.ParserUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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

    private final SignatureTemplateParserFactory signatureTemplateParserFactory;

    private final FileResourceService fileResourceService;

    private final CoreParserService coreParserService;

    private final Environment env;

    public SignatureTemplateServiceImpl(SignatureTemplateRepository signatureTemplateRepository, SignatureTemplateMapper signatureTemplateMapper, UserRepository userRepository, SignatureTemplateParserFactory signatureTemplateParserFactory, FileResourceService fileResourceService, CoreParserService coreParserService, Environment env) {
        this.signatureTemplateRepository = signatureTemplateRepository;
        this.signatureTemplateMapper = signatureTemplateMapper;
        this.userRepository = userRepository;
        this.signatureTemplateParserFactory = signatureTemplateParserFactory;
        this.fileResourceService = fileResourceService;
        this.coreParserService = coreParserService;
        this.env = env;
    }

    /**
     * Save a signatureTemplate.
     *
     * @param signatureTemplateDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public SignatureTemplateDTO save(SignatureTemplateDTO signatureTemplateDTO) throws ApplicationException {
        log.debug("Request to save SignatureTemplate : {}", signatureTemplateDTO);
        String thumnailImage = createThumbnail(signatureTemplateDTO);
        signatureTemplateDTO.setThumbnail(thumnailImage);
        SignatureTemplate signatureTemplate = signatureTemplateMapper.toEntity(signatureTemplateDTO);
        signatureTemplate = signatureTemplateRepository.save(signatureTemplate);
        return signatureTemplateMapper.toDto(signatureTemplate);
    }

    private String createThumbnail(SignatureTemplateDTO signatureTemplateDTO) throws ApplicationException {
        SignatureExampleDTO exampleDTO = new SignatureExampleDTO();
        exampleDTO.setHeight(signatureTemplateDTO.getHeight());
        exampleDTO.setWidth(signatureTemplateDTO.getWidth());
        exampleDTO.setHtmlTemplate(signatureTemplateDTO.getHtmlTemplate());
        exampleDTO.setTransparency(false);
        String thumbnailImage = this.getSignatureExample(exampleDTO);
        return thumbnailImage;
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
    public Optional<SignatureTemplate[]> findAllTemplatesByUserLoggedIn() throws ApplicationException {
        Optional<UserEntity> userEntity = userRepository.findOneWithAuthoritiesByLogin(AccountUtils.getLoggedAccount());
        Optional<SignatureTemplate[]> signatureTemplates = signatureTemplateRepository.findAllByUserId(userEntity.get().getId());
        return signatureTemplates;
    }


    @Override
    @Transactional(readOnly = true)
    public Page<SignatureTemplateDTO> findAllWithUserId(Pageable pageable, Long userId) throws ApplicationException {
        log.debug("Request to get SignatureImage with id : {}", userId);
        Page<SignatureTemplateDTO> signatureTemplateDTOPage = signatureTemplateRepository.findAllSignatureTemplateByUserId(pageable, userId);
        Page<SignatureTemplateDTO> page;

        SignatureTemplateDTO templateDTO = new SignatureTemplateDTO();
        templateDTO.setWidth(355);
        templateDTO.setHeight(150);
        templateDTO.setThumbnail(this.createThumbnail(templateDTO));
        List<SignatureTemplateDTO> listSignatureTempDto = new ArrayList<>(signatureTemplateDTOPage.toList());
        listSignatureTempDto.add(templateDTO);

        page = new PageImpl<>(listSignatureTempDto);
        return page;
    }


    @Override
    public String getSignatureExample(SignatureExampleDTO signatureExampleDTO) throws ApplicationException {
        String htmlContent = "";
        Optional<CoreParserDTO> coreParserDTO;
        String htmlTemplate;
        String signingImageB64;
        try {
            htmlTemplate = signatureExampleDTO.getHtmlTemplate(fileResourceService);
            signingImageB64 = signatureExampleDTO.getSigningImage(fileResourceService);
        } catch (IOException ioException) {
            throw new ApplicationException(ioException.getMessage());
        }
        SignatureTemplateParserType coreParser = signatureExampleDTO.getCoreParser();
        int width = signatureExampleDTO.getWidth();
        int height = signatureExampleDTO.getHeight();
        boolean transparency = signatureExampleDTO.isTransparency();

        if (coreParser == null) {
            coreParser = SignatureTemplateParserType.DEFAULT;
        }

        SignatureTemplateParseService signatureTemplateParseService = signatureTemplateParserFactory.resolve(coreParser);
        htmlContent = signatureTemplateParseService.previewSignatureTemplate(htmlTemplate, signingImageB64);
        return ParserUtils.convertHtmlContentToImageByProversion(htmlContent, width, height, transparency, env);
    }
}
