package vn.easyca.signserver.webapp.web.rest;

import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.webapp.service.SystemConfigCachingService;
import vn.easyca.signserver.webapp.service.SystemConfigService;
import vn.easyca.signserver.webapp.web.rest.errors.BadRequestAlertException;
import vn.easyca.signserver.webapp.service.dto.SystemConfigDTO;

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
import vn.easyca.signserver.webapp.web.rest.vm.response.BaseResponseVM;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link vn.easyca.signserver.webapp.domain.SystemConfig}.
 */
@RestController
@RequestMapping("/api")
public class SystemConfigResource {

    private final Logger log = LoggerFactory.getLogger(SystemConfigResource.class);

    private static final String ENTITY_NAME = "systemConfig";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SystemConfigService systemConfigService;

    private final SystemConfigCachingService systemConfigCachingService;

    public SystemConfigResource(SystemConfigService systemConfigService, SystemConfigCachingService systemConfigCachingService) {
        this.systemConfigService = systemConfigService;
        this.systemConfigCachingService = systemConfigCachingService;
    }

    /**
     * {@code POST  /system-configs} : Create a new systemConfig.
     *
     * @param systemConfigDTO the systemConfigDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new systemConfigDTO, or with status {@code 400 (Bad Request)} if the systemConfig has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/system-configs")
    public ResponseEntity<SystemConfigDTO> createSystemConfig(@RequestBody SystemConfigDTO systemConfigDTO) throws Exception {
        log.debug("REST request to save SystemConfig : {}", systemConfigDTO);
        try{
            if (systemConfigDTO.getId() != null) {
                throw new BadRequestAlertException("A new systemConfig cannot already have an ID", ENTITY_NAME, "idexists");
            }
            SystemConfigDTO result = systemConfigService.save(systemConfigDTO);
            return ResponseEntity.created(new URI("/api/system-configs/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                .body(result);
        }catch (ApplicationException applicationException){
            throw new BadRequestAlertException("A new systemConfig cannot already have comId and key that exist", "systemConfig", "duplicateComIdAndKey");
        }
    }

    /**
     * {@code PUT  /system-configs} : Updates an existing systemConfig.
     *
     * @param systemConfigDTO the systemConfigDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated systemConfigDTO,
     * or with status {@code 400 (Bad Request)} if the systemConfigDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the systemConfigDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/system-configs")
    public ResponseEntity<SystemConfigDTO> updateSystemConfig(@RequestBody SystemConfigDTO systemConfigDTO) throws ApplicationException {
        log.debug("REST request to update SystemConfig : {}", systemConfigDTO);
        if (systemConfigDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        SystemConfigDTO result = systemConfigService.save(systemConfigDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, systemConfigDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /system-configs} : get all the systemConfigs.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of systemConfigs in body.
     */
    @GetMapping("/system-configs")
    public ResponseEntity<List<SystemConfigDTO>> getAllSystemConfigs(Pageable pageable) {
        log.debug("REST request to get a page of SystemConfigs");
        Page<SystemConfigDTO> page = systemConfigService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /system-configs/:id} : get the "id" systemConfig.
     *
     * @param id the id of the systemConfigDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the systemConfigDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/system-configs/{id}")
    public ResponseEntity<SystemConfigDTO> getSystemConfig(@PathVariable Long id) {
        log.debug("REST request to get SystemConfig : {}", id);
        Optional<SystemConfigDTO> systemConfigDTO = systemConfigService.findOne(id);
        return ResponseUtil.wrapOrNotFound(systemConfigDTO);
    }

    /**
     * {@code DELETE  /system-configs/:id} : delete the "id" systemConfig.
     *
     * @param id the id of the systemConfigDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/system-configs/{id}")
    public ResponseEntity<Void> deleteSystemConfig(@PathVariable Long id) {
        log.debug("REST request to delete SystemConfig : {}", id);
        systemConfigService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    @GetMapping("/system-configs/isAuthenOTP")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BaseResponseVM> isAuthenOTP() {
        try {
            Boolean isUseOTP = systemConfigCachingService.getConfig().getUseOTP();
            return ResponseEntity.ok(BaseResponseVM.createNewSuccessResponse(isUseOTP));
        } catch (ApplicationException e) {
            throw new BadRequestAlertException("Has error when get OTP configuration", "systemConfig", "otp");
        }
    }
}
