package vn.easyca.signserver.webapp.service;

import vn.easyca.signserver.webapp.domain.CoreParser;
import vn.easyca.signserver.webapp.service.dto.CoreParserDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link vn.easyca.signserver.webapp.domain.CoreParser}.
 */
public interface CoreParserService {

    /**
     * Save a coreParser.
     *
     * @param coreParserDTO the entity to save.
     * @return the persisted entity.
     */
    CoreParserDTO save(CoreParserDTO coreParserDTO);

    /**
     * Get all the coreParsers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CoreParserDTO> findAll(Pageable pageable);


    /**
     * Get the "id" coreParser.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CoreParserDTO> findOne(Long id);

    /**
     * Delete the "id" coreParser.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    List<CoreParser> getAllCoreParsers();
}
