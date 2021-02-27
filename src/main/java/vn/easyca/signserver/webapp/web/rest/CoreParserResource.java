package vn.easyca.signserver.webapp.web.rest;

import vn.easyca.signserver.webapp.domain.CoreParser;
import vn.easyca.signserver.webapp.service.CoreParserService;
import vn.easyca.signserver.webapp.service.dto.UserDropdownDTO;
import vn.easyca.signserver.webapp.web.rest.errors.BadRequestAlertException;
import vn.easyca.signserver.webapp.service.dto.CoreParserDTO;
import vn.easyca.signserver.webapp.service.dto.CoreParserCriteria;
import vn.easyca.signserver.webapp.service.CoreParserQueryService;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link vn.easyca.signserver.webapp.domain.CoreParser}.
 */
@RestController
@RequestMapping("/api")
public class CoreParserResource {

    private final Logger log = LoggerFactory.getLogger(CoreParserResource.class);

    private final CoreParserService coreParserService;

    private final CoreParserQueryService coreParserQueryService;

    public CoreParserResource(CoreParserService coreParserService, CoreParserQueryService coreParserQueryService) {
        this.coreParserService = coreParserService;
        this.coreParserQueryService = coreParserQueryService;
    }

    /**
     * {@code GET  /core-parsers} : get all the coreParsers.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of coreParsers in body.
     */
    @GetMapping("/core-parsers")
    public ResponseEntity<List<CoreParserDTO>> getAllCoreParsers(CoreParserCriteria criteria, Pageable pageable) {
        log.debug("REST request to get CoreParsers by criteria: {}", criteria);
        Page<CoreParserDTO> page = coreParserQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /core-parsers/count} : count all the coreParsers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/core-parsers/count")
    public ResponseEntity<Long> countCoreParsers(CoreParserCriteria criteria) {
        log.debug("REST request to count CoreParsers by criteria: {}", criteria);
        return ResponseEntity.ok().body(coreParserQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /core-parsers/:id} : get the "id" coreParser.
     *
     * @param id the id of the coreParserDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the coreParserDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/core-parsers/{id}")
    public ResponseEntity<CoreParserDTO> getCoreParser(@PathVariable Long id) {
        log.debug("REST request to get CoreParser : {}", id);
        Optional<CoreParserDTO> coreParserDTO = coreParserService.findOne(id);
        return ResponseUtil.wrapOrNotFound(coreParserDTO);
    }

    @GetMapping("/core-parsers/getAll")
    public List<CoreParser> getAllUsers() { return  coreParserService.getAllCoreParsers(); }
}
