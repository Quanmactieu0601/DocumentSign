package vn.easyca.signserver.webapp.web.rest;

import vn.easyca.signserver.webapp.domain.CoreParser;
import vn.easyca.signserver.webapp.service.CoreParserService;
import vn.easyca.signserver.webapp.service.dto.CoreParserDTO;

import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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



    public CoreParserResource(CoreParserService coreParserService) {
        this.coreParserService = coreParserService;
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
