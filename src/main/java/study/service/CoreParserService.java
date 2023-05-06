package study.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import study.domain.CoreParser;
import study.service.dto.CoreParserDTO;

/**
 * Service Interface for managing {@link study.domain.CoreParser}.
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
     * Get the "name" coreParser.
     *
     * @param name the id of the entity.
     * @return the entity.
     */
    Optional<CoreParserDTO> findByName(String name);

    /**
     * Delete the "id" coreParser.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    List<CoreParser> getAllCoreParsers();
}
