package vn.easyca.signserver.webapp.web.rest;

import vn.easyca.signserver.webapp.service.CertPackageService;
import vn.easyca.signserver.webapp.web.rest.errors.BadRequestAlertException;
import vn.easyca.signserver.webapp.service.dto.CertPackageDTO;

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
 * REST controller for managing {@link vn.easyca.signserver.webapp.domain.CertPackage}.
 */
@RestController
@RequestMapping("/api")
public class CertPackageResource {

    private final Logger log = LoggerFactory.getLogger(CertPackageResource.class);

    private static final String ENTITY_NAME = "certPackage";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CertPackageService certPackageService;

    public CertPackageResource(CertPackageService certPackageService) {
        this.certPackageService = certPackageService;
    }

    /**
     * {@code POST  /cert-packages} : Create a new certPackage.
     *
     * @param certPackageDTO the certPackageDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new certPackageDTO, or with status {@code 400 (Bad Request)} if the certPackage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/cert-packages")
    public ResponseEntity<CertPackageDTO> createCertPackage(@RequestBody CertPackageDTO certPackageDTO) throws URISyntaxException {
        log.debug("REST request to save CertPackage : {}", certPackageDTO);
        if (certPackageDTO.getId() != null) {
            throw new BadRequestAlertException("A new certPackage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CertPackageDTO result = certPackageService.save(certPackageDTO);
        return ResponseEntity.created(new URI("/api/cert-packages/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /cert-packages} : Updates an existing certPackage.
     *
     * @param certPackageDTO the certPackageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated certPackageDTO,
     * or with status {@code 400 (Bad Request)} if the certPackageDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the certPackageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/cert-packages")
    public ResponseEntity<CertPackageDTO> updateCertPackage(@RequestBody CertPackageDTO certPackageDTO) throws URISyntaxException {
        log.debug("REST request to update CertPackage : {}", certPackageDTO);
        if (certPackageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        CertPackageDTO result = certPackageService.save(certPackageDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, certPackageDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /cert-packages} : get all the certPackages.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of certPackages in body.
     */
    @GetMapping("/cert-packages")
    public ResponseEntity<List<CertPackageDTO>> getAllCertPackages(Pageable pageable) {
        log.debug("REST request to get a page of CertPackages");
        Page<CertPackageDTO> page = certPackageService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /cert-packages/:id} : get the "id" certPackage.
     *
     * @param id the id of the certPackageDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the certPackageDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/cert-packages/{id}")
    public ResponseEntity<CertPackageDTO> getCertPackage(@PathVariable Long id) {
        log.debug("REST request to get CertPackage : {}", id);
        Optional<CertPackageDTO> certPackageDTO = certPackageService.findOne(id);
        return ResponseUtil.wrapOrNotFound(certPackageDTO);
    }

    /**
     * {@code DELETE  /cert-packages/:id} : delete the "id" certPackage.
     *
     * @param id the id of the certPackageDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/cert-packages/{id}")
    public ResponseEntity<Void> deleteCertPackage(@PathVariable Long id) {
        log.debug("REST request to delete CertPackage : {}", id);
        certPackageService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
