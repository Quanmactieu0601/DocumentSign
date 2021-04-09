package vn.easyca.signserver.webapp.web.rest;


import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.webapp.domain.SignatureTemplate;
import vn.easyca.signserver.webapp.enm.*;
import vn.easyca.signserver.webapp.service.AsyncTransactionService;
import vn.easyca.signserver.webapp.service.FileResourceService;
import vn.easyca.signserver.webapp.service.SignatureTemplateService;
import vn.easyca.signserver.webapp.service.dto.SignatureExampleDTO;
import vn.easyca.signserver.webapp.utils.AccountUtils;
import vn.easyca.signserver.webapp.utils.DateTimeUtils;
import vn.easyca.signserver.webapp.utils.ParserUtils;
import vn.easyca.signserver.webapp.web.rest.errors.BadRequestAlertException;
import vn.easyca.signserver.webapp.service.dto.SignatureTemplateDTO;

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
import vn.easyca.signserver.webapp.web.rest.vm.response.BaseResponseVM;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link vn.easyca.signserver.webapp.domain.SignatureTemplate}.
 */
@RestController
@RequestMapping("/api")
public class SignatureTemplateResource extends BaseResource {
    private final Logger log = LoggerFactory.getLogger(SignatureTemplateResource.class);

    private final AsyncTransactionService asyncTransactionService;
    private final FileResourceService fileResourceService;

    private static final String ENTITY_NAME = "signatureTemplate";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SignatureTemplateService signatureTemplateService;

    public SignatureTemplateResource(AsyncTransactionService asyncTransactionService, FileResourceService fileResourceService, SignatureTemplateService signatureTemplateService) {
        this.asyncTransactionService = asyncTransactionService;
        this.fileResourceService = fileResourceService;
        this.signatureTemplateService = signatureTemplateService;
    }

    /**
     * {@code POST  /signature-templates} : Create a new signatureTemplate.
     *
     * @param signatureTemplateDTO the signatureTemplateDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new signatureTemplateDTO, or with status {@code 400 (Bad Request)} if the signatureTemplate has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/signature-templates")
    public ResponseEntity<SignatureTemplateDTO> createSignatureTemplate(@RequestBody SignatureTemplateDTO signatureTemplateDTO) throws URISyntaxException {
        log.debug("REST request to save SignatureTemplate : {}", signatureTemplateDTO);
        if (signatureTemplateDTO.getId() != null) {
            asyncTransactionService.newThread("/api/signature-templates", TransactionType.BUSINESS, Action.CREATE, Extension.NONE, Method.POST,
                TransactionStatus.FAIL, "A new signatureTemplate cannot already have an ID", AccountUtils.getLoggedAccount());
            throw new BadRequestAlertException("A new signatureTemplate cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SignatureTemplateDTO result = signatureTemplateService.save(signatureTemplateDTO);
        asyncTransactionService.newThread("/api/signature-templates", TransactionType.BUSINESS, Action.CREATE, Extension.NONE, Method.POST,
            TransactionStatus.SUCCESS, null, AccountUtils.getLoggedAccount());
        return ResponseEntity.created(new URI("/api/signature-templates/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /signature-templates} : Updates an existing signatureTemplate.
     *
     * @param signatureTemplateDTO the signatureTemplateDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated signatureTemplateDTO,
     * or with status {@code 400 (Bad Request)} if the signatureTemplateDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the signatureTemplateDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/signature-templates")
    public ResponseEntity<SignatureTemplateDTO> updateSignatureTemplate(@RequestBody SignatureTemplateDTO signatureTemplateDTO) throws URISyntaxException {
        log.debug("REST request to update SignatureTemplate : {}", signatureTemplateDTO);
        if (signatureTemplateDTO.getId() == null) {
            asyncTransactionService.newThread("/api/signature-templates", TransactionType.BUSINESS, Action.MODIFY, Extension.NONE, Method.PUT,
                TransactionStatus.FAIL, "Invalid id", AccountUtils.getLoggedAccount());
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        SignatureTemplateDTO result = signatureTemplateService.save(signatureTemplateDTO);
        asyncTransactionService.newThread("/api/signature-templates", TransactionType.BUSINESS, Action.MODIFY, Extension.NONE, Method.PUT,
            TransactionStatus.SUCCESS, null, AccountUtils.getLoggedAccount());
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, signatureTemplateDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /signature-templates} : get all the signatureTemplates.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of signatureTemplates in body.
     */
    @GetMapping("/signature-templates")
    public ResponseEntity<List<SignatureTemplateDTO>> getAllSignatureTemplates(Pageable pageable) {
        log.debug("REST request to get a page of SignatureTemplates");
        Page<SignatureTemplateDTO> page = signatureTemplateService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        asyncTransactionService.newThread("/api/signature-templates", TransactionType.BUSINESS, Action.GET_INFO, Extension.NONE, Method.GET,
            TransactionStatus.SUCCESS, null, AccountUtils.getLoggedAccount());
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/signature-templates/getByUserId")
    public ResponseEntity<List<SignatureTemplateDTO>> getAllSignatureTemplatesByUserId(Pageable pageable, @RequestParam(required = true) Long userId) {
        log.debug("REST request to get a page of SignatureTemplates by UserId");
//        Page<SignatureTemplateDTO> page = signatureTemplateService.findAll(pageable);
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
//        asyncTransactionService.newThread("/api/signature-templates", TransactionType.BUSINESS, Action.GET_INFO, Extension.NONE, Method.GET,
//            TransactionStatus.SUCCESS, null, AccountUtils.getLoggedAccount());
//        return ResponseEntity.ok().headers(headers).body(page.getContent());

         Page<SignatureTemplateDTO> page = signatureTemplateService.findAllWithUserId(pageable, userId);
         HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
         asyncTransactionService.newThread("/api/signature-templates/{id}", TransactionType.BUSINESS, Action.GET_INFO, Extension.NONE, Method.GET,
            TransactionStatus.SUCCESS, null, AccountUtils.getLoggedAccount());
         return ResponseEntity.ok().headers(headers).body(page.getContent());
    }


    /**
     * {@code GET  /signature-templates/:id} : get the "id" signatureTemplate.
     *
     * @param id the id of the signatureTemplateDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the signatureTemplateDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/signature-templates/{id}")
    public ResponseEntity<SignatureTemplateDTO> getSignatureTemplate(@PathVariable Long id) {
        log.debug("REST request to get SignatureTemplate : {}", id);
        Optional<SignatureTemplateDTO> signatureTemplateDTO = signatureTemplateService.findOne(id);
        asyncTransactionService.newThread("/api/signature-templates/{id}", TransactionType.BUSINESS, Action.GET_INFO, Extension.NONE, Method.GET,
            TransactionStatus.SUCCESS, null, AccountUtils.getLoggedAccount());
        return ResponseUtil.wrapOrNotFound(signatureTemplateDTO);
    }

    /**
     * {@code DELETE  /signature-templates/:id} : delete the "id" signatureTemplate.
     *
     * @param id the id of the signatureTemplateDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/signature-templates/{id}")
    public ResponseEntity<Void> deleteSignatureTemplate(@PathVariable Long id) {
        log.debug("REST request to delete SignatureTemplate : {}", id);
        signatureTemplateService.delete(id);
        asyncTransactionService.newThread("/api/signature-templates/{id}", TransactionType.BUSINESS, Action.DELETE, Extension.NONE, Method.DELETE,
            TransactionStatus.SUCCESS, null, AccountUtils.getLoggedAccount());
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    @PostMapping("/signature-templates/signExample")
    public ResponseEntity<BaseResponseVM> getSignExample(@RequestBody SignatureExampleDTO signatureExampleDTO) {
        try {
            String htmlTemplate = signatureExampleDTO.getHtmlTemplate(fileResourceService);
            String signer = signatureExampleDTO.getSigner();
            String signingImageB64 = signatureExampleDTO.getSigningImage(fileResourceService);
            int width = signatureExampleDTO.getWidth();
            int height = signatureExampleDTO.getHeight();
            boolean transparency = signatureExampleDTO.isTransparency();

            String htmlContent = htmlTemplate
                .replaceFirst("signer", signer)
                .replaceFirst("position", "TPDV. ")
                .replaceFirst("address", "Hà Nội")
                .replaceFirst("signatureImage", signingImageB64)
                .replaceFirst("timeSign", DateTimeUtils.getCurrentTimeStampWithFormat(DateTimeUtils.HHmmss_ddMMyyyy));

            String imageBase64 = ParserUtils.convertHtmlContentToBase64Resize(htmlContent, width, height, transparency);
            return ResponseEntity.ok(BaseResponseVM.createNewSuccessResponse(imageBase64));
        } catch (ApplicationException applicationException) {
            log.error(applicationException.getMessage(), applicationException);
            message = applicationException.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(applicationException.getCode(), null, applicationException.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage());
            message = e.getMessage();
            return ResponseEntity.ok(new BaseResponseVM(-1, null, e.getMessage()));
        } finally {
            asyncTransactionService.newThread("/api/signature-templates/signExample", TransactionType.BUSINESS, Action.CREATE, Extension.CERT, Method.POST,
                status, message, AccountUtils.getLoggedAccount());
        }
    }
}
