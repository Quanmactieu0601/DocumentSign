package vn.easyca.signserver.webapp.web.rest;

import vn.easyca.signserver.webapp.domain.SystemConfigCategory;
import vn.easyca.signserver.webapp.repository.SystemConfigCategoryRepository;
import vn.easyca.signserver.webapp.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link vn.easyca.signserver.webapp.domain.SystemConfigCategory}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class SystemConfigCategoryResource {

    private final Logger log = LoggerFactory.getLogger(SystemConfigCategoryResource.class);

    private static final String ENTITY_NAME = "systemConfigCategory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SystemConfigCategoryRepository systemConfigCategoryRepository;

    public SystemConfigCategoryResource(SystemConfigCategoryRepository systemConfigCategoryRepository) {
        this.systemConfigCategoryRepository = systemConfigCategoryRepository;
    }

    /**
     * {@code POST  /system-config-categories} : Create a new systemConfigCategory.
     *
     * @param systemConfigCategory the systemConfigCategory to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new systemConfigCategory, or with status {@code 400 (Bad Request)} if the systemConfigCategory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/system-config-categories")
    public ResponseEntity<SystemConfigCategory> createSystemConfigCategory(@RequestBody SystemConfigCategory systemConfigCategory) throws URISyntaxException {
        log.debug("REST request to save SystemConfigCategory : {}", systemConfigCategory);
        if (systemConfigCategory.getId() != null) {
            throw new BadRequestAlertException("A new systemConfigCategory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SystemConfigCategory result = systemConfigCategoryRepository.save(systemConfigCategory);
        return ResponseEntity.created(new URI("/api/system-config-categories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /system-config-categories} : Updates an existing systemConfigCategory.
     *
     * @param systemConfigCategory the systemConfigCategory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated systemConfigCategory,
     * or with status {@code 400 (Bad Request)} if the systemConfigCategory is not valid,
     * or with status {@code 500 (Internal Server Error)} if the systemConfigCategory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/system-config-categories")
    public ResponseEntity<SystemConfigCategory> updateSystemConfigCategory(@RequestBody SystemConfigCategory systemConfigCategory) throws URISyntaxException {
        log.debug("REST request to update SystemConfigCategory : {}", systemConfigCategory);
        if (systemConfigCategory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        SystemConfigCategory result = systemConfigCategoryRepository.save(systemConfigCategory);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, systemConfigCategory.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /system-config-categories} : get all the systemConfigCategories.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of systemConfigCategories in body.
     */
    @GetMapping("/system-config-categories")
    public List<SystemConfigCategory> getAllSystemConfigCategories() {
        log.debug("REST request to get all SystemConfigCategories");
        return systemConfigCategoryRepository.findAll();
    }

    /**
     * {@code GET  /system-config-categories/:id} : get the "id" systemConfigCategory.
     *
     * @param id the id of the systemConfigCategory to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the systemConfigCategory, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/system-config-categories/{id}")
    public ResponseEntity<SystemConfigCategory> getSystemConfigCategory(@PathVariable Long id) {
        log.debug("REST request to get SystemConfigCategory : {}", id);
        Optional<SystemConfigCategory> systemConfigCategory = systemConfigCategoryRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(systemConfigCategory);
    }

    /**
     * {@code DELETE  /system-config-categories/:id} : delete the "id" systemConfigCategory.
     *
     * @param id the id of the systemConfigCategory to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/system-config-categories/{id}")
    public ResponseEntity<Void> deleteSystemConfigCategory(@PathVariable Long id) {
        log.debug("REST request to delete SystemConfigCategory : {}", id);
        systemConfigCategoryRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
