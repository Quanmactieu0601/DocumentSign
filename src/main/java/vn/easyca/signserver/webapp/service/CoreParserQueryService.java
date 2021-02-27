package vn.easyca.signserver.webapp.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import vn.easyca.signserver.webapp.domain.CoreParser;
import vn.easyca.signserver.webapp.domain.*; // for static metamodels
import vn.easyca.signserver.webapp.repository.CoreParserRepository;
import vn.easyca.signserver.webapp.service.dto.CoreParserCriteria;
import vn.easyca.signserver.webapp.service.dto.CoreParserDTO;
import vn.easyca.signserver.webapp.service.mapper.CoreParserMapper;

/**
 * Service for executing complex queries for {@link CoreParser} entities in the database.
 * The main input is a {@link CoreParserCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link CoreParserDTO} or a {@link Page} of {@link CoreParserDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CoreParserQueryService extends QueryService<CoreParser> {

    private final Logger log = LoggerFactory.getLogger(CoreParserQueryService.class);

    private final CoreParserRepository coreParserRepository;

    private final CoreParserMapper coreParserMapper;

    public CoreParserQueryService(CoreParserRepository coreParserRepository, CoreParserMapper coreParserMapper) {
        this.coreParserRepository = coreParserRepository;
        this.coreParserMapper = coreParserMapper;
    }

    /**
     * Return a {@link List} of {@link CoreParserDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<CoreParserDTO> findByCriteria(CoreParserCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<CoreParser> specification = createSpecification(criteria);
        return coreParserMapper.toDto(coreParserRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link CoreParserDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CoreParserDTO> findByCriteria(CoreParserCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<CoreParser> specification = createSpecification(criteria);
        return coreParserRepository.findAll(specification, page)
            .map(coreParserMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CoreParserCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<CoreParser> specification = createSpecification(criteria);
        return coreParserRepository.count(specification);
    }

    /**
     * Function to convert {@link CoreParserCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<CoreParser> createSpecification(CoreParserCriteria criteria) {
        Specification<CoreParser> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), CoreParser_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), CoreParser_.name));
            }
        }
        return specification;
    }
}
