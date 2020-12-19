package vn.easyca.signserver.webapp.web.rest;

import vn.easyca.signserver.webapp.enm.*;
import vn.easyca.signserver.webapp.service.AsyncTransactionService;
import vn.easyca.signserver.webapp.service.SignatureImageService;
import vn.easyca.signserver.webapp.utils.AccountUtils;
import vn.easyca.signserver.webapp.web.rest.errors.BadRequestAlertException;
import vn.easyca.signserver.webapp.service.dto.SignatureImageDTO;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link vn.easyca.signserver.webapp.domain.SignatureImage}.
 */
@RestController
@RequestMapping("/api")
public class SignatureImageResource {

    private final AsyncTransactionService asyncTransactionService;

    private final Logger log = LoggerFactory.getLogger(SignatureImageResource.class);

    private static final String ENTITY_NAME = "signatureImage";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SignatureImageService signatureImageService;

    public SignatureImageResource(AsyncTransactionService asyncTransactionService, SignatureImageService signatureImageService) {
        this.asyncTransactionService = asyncTransactionService;
        this.signatureImageService = signatureImageService;
    }

    /**
     * {@code POST  /signature-images} : Create a new signatureImage.
     *
     * @param signatureImageDTO the signatureImageDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new signatureImageDTO, or with status {@code 400 (Bad Request)} if the signatureImage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/signature-images")
    public ResponseEntity<SignatureImageDTO> createSignatureImage(@RequestBody SignatureImageDTO signatureImageDTO) throws URISyntaxException {
        log.debug("REST request to save SignatureImage : {}", signatureImageDTO);
        if (signatureImageDTO.getId() != null) {
            asyncTransactionService.newThread("/api/signature-images", TransactionType.BUSINESS, Action.CREATE, Extension.NONE, Method.POST,
                TransactionStatus.FAIL, "A new signatureImage cannot already have an ID", AccountUtils.getLoggedAccount());
            throw new BadRequestAlertException("A new signatureImage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SignatureImageDTO result = signatureImageService.save(signatureImageDTO);
        asyncTransactionService.newThread("/api/signature-images", TransactionType.BUSINESS, Action.CREATE, Extension.NONE, Method.POST,
            TransactionStatus.SUCCESS, null, AccountUtils.getLoggedAccount());
        return ResponseEntity.created(new URI("/api/signature-images/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /signature-images} : Updates an existing signatureImage.
     *
     * @param signatureImageDTO the signatureImageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated signatureImageDTO,
     * or with status {@code 400 (Bad Request)} if the signatureImageDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the signatureImageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/signature-images")
    public ResponseEntity<SignatureImageDTO> updateSignatureImage(@RequestBody SignatureImageDTO signatureImageDTO) throws URISyntaxException {
        log.debug("REST request to update SignatureImage : {}", signatureImageDTO);
        if (signatureImageDTO.getId() == null) {
            asyncTransactionService.newThread("/api/signature-images", TransactionType.BUSINESS, Action.MODIFY, Extension.NONE, Method.PUT,
                TransactionStatus.FAIL, "Invalid id", AccountUtils.getLoggedAccount());
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        SignatureImageDTO result = signatureImageService.save(signatureImageDTO);
        asyncTransactionService.newThread("/api/signature-images", TransactionType.BUSINESS, Action.CREATE, Extension.NONE, Method.PUT,
            TransactionStatus.SUCCESS, null, AccountUtils.getLoggedAccount());
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, signatureImageDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /signature-images} : get all the signatureImages.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of signatureImages in body.
     */
    @GetMapping("/signature-images")
    public ResponseEntity<List<SignatureImageDTO>> getAllSignatureImages(Pageable pageable) {
        log.debug("REST request to get a page of SignatureImages");
        Page<SignatureImageDTO> page = signatureImageService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        asyncTransactionService.newThread("/api/signature-images", TransactionType.BUSINESS, Action.GET_INFO, Extension.NONE, Method.GET,
            TransactionStatus.SUCCESS, null, AccountUtils.getLoggedAccount());
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /signature-images/:id} : get the "id" signatureImage.
     *
     * @param id the id of the signatureImageDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the signatureImageDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/signature-images/{id}")
    public ResponseEntity<SignatureImageDTO> getSignatureImage(@PathVariable Long id) {
        log.debug("REST request to get SignatureImage : {}", id);
        Optional<SignatureImageDTO> signatureImageDTO = signatureImageService.findOne(id);
        asyncTransactionService.newThread("/api/signature-images/{id}", TransactionType.BUSINESS, Action.GET_INFO, Extension.NONE, Method.GET,
            TransactionStatus.SUCCESS, null, AccountUtils.getLoggedAccount());
        return ResponseUtil.wrapOrNotFound(signatureImageDTO);
    }

    /**
     * {@code DELETE  /signature-images/:id} : delete the "id" signatureImage.
     *
     * @param id the id of the signatureImageDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/signature-images/{id}")
    public ResponseEntity<Void> deleteSignatureImage(@PathVariable Long id) {
        log.debug("REST request to delete SignatureImage : {}", id);
        signatureImageService.delete(id);
        asyncTransactionService.newThread("/api/signature-images/{id}", TransactionType.BUSINESS, Action.DELETE, Extension.NONE, Method.DELETE,
            TransactionStatus.SUCCESS, null, AccountUtils.getLoggedAccount());
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
