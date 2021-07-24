package vn.easyca.signserver.webapp.service.impl;

import vn.easyca.signserver.webapp.service.CoreParserService;
import vn.easyca.signserver.webapp.domain.CoreParser;
import vn.easyca.signserver.webapp.repository.CoreParserRepository;
import vn.easyca.signserver.webapp.service.dto.CoreParserDTO;
import vn.easyca.signserver.webapp.service.mapper.CoreParserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link CoreParser}.
 */
@Service
@Transactional
public class CoreParserServiceImpl implements CoreParserService {

    private final Logger log = LoggerFactory.getLogger(CoreParserServiceImpl.class);

    private final CoreParserRepository coreParserRepository;

    private final CoreParserMapper coreParserMapper;

    public CoreParserServiceImpl(CoreParserRepository coreParserRepository, CoreParserMapper coreParserMapper) {
        this.coreParserRepository = coreParserRepository;
        this.coreParserMapper = coreParserMapper;
    }

    /**
     * Save a coreParser.
     *
     * @param coreParserDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public CoreParserDTO save(CoreParserDTO coreParserDTO) {
        log.debug("Request to save CoreParser : {}", coreParserDTO);
        CoreParser coreParser = coreParserMapper.toEntity(coreParserDTO);
        coreParser = coreParserRepository.save(coreParser);
        return coreParserMapper.toDto(coreParser);
    }

    /**
     * Get all the coreParsers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<CoreParserDTO> findAll(Pageable pageable) {
        log.debug("Request to get all CoreParsers");
        return coreParserRepository.findAll(pageable)
            .map(coreParserMapper::toDto);
    }


    /**
     * Get one coreParser by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<CoreParserDTO> findOne(Long id) {
        log.debug("Request to get CoreParser : {}", id);
        return coreParserRepository.findById(id)
            .map(coreParserMapper::toDto);
    }

    @Override
    public Optional<CoreParserDTO> findByName(String s) {
        log.debug("Request to get CoreParser : {}", s);
        return coreParserRepository.findByName(s)
            .map(coreParserMapper::toDto);
    }

    /**
     * Delete the coreParser by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete CoreParser : {}", id);
        coreParserRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CoreParser> getAllCoreParsers(){
       return coreParserRepository.findAll();
    }
}
