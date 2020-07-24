package vn.easyca.signserver.webapp.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import vn.easyca.signserver.webapp.domain.Certificate;
import vn.easyca.signserver.webapp.service.P11CertificateGeneratorService;
import vn.easyca.signserver.webapp.service.certificate.CertificateService;
import vn.easyca.signserver.webapp.service.dto.CertificateGeneratedResult;
import vn.easyca.signserver.webapp.service.dto.CertificateGeneratorDto;
import vn.easyca.signserver.webapp.service.dto.ImportP12FileDTO;
import vn.easyca.signserver.webapp.web.rest.errors.BadRequestAlertException;
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
import vn.easyca.signserver.webapp.web.rest.vm.GenCertificateVM;
import vn.easyca.signserver.webapp.web.rest.vm.RegisterCertificateVM;
import vn.easyca.signserver.webapp.web.rest.vm.BaseResponseVM;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link Certificate}.
 */
@RestController
@RequestMapping("/api/certificate")
public class CertificateResource {

    private final Logger log = LoggerFactory.getLogger(CertificateResource.class);

    private static final String ENTITY_NAME = "certificate";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    @Autowired
    private P11CertificateGeneratorService p11GeneratorService;

    @Autowired
    private CertificateService certificateService;

    @PostMapping("/register/p12")
    public ResponseEntity<BaseResponseVM> registerP12PKCS(@RequestBody ImportP12FileDTO importP12FileDTO)  {
        try {
            certificateService.importP12Certificate(importP12FileDTO);
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse("OK"));
        } catch (Exception e) {
            return ResponseEntity.ok(BaseResponseVM.CreateNewErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/register/p11")
    public ResponseEntity<BaseResponseVM> genCertificate(@RequestBody GenCertificateVM genCertificateVM) {
        try {
            CertificateGeneratorDto dto = new CertificateGeneratorDto(genCertificateVM);
            CertificateGeneratedResult result = p11GeneratorService.genCertificate(dto);
            RegisterCertificateVM registerCertificateVM = new RegisterCertificateVM();
            registerCertificateVM.setCert(result.getCertificate().getSerial(), result.getCertificate().getRawData());
            if (result.getUser() != null)
                registerCertificateVM.setUser(result.getUser(), result.getUserPassword());
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(registerCertificateVM));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(e.getMessage()));
        }
    }

    /**
     * {@code POST  /certificates} : Create a new certificate.
     *
     * @param certificate the certificate to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new certificate, or with status {@code 400 (Bad Request)} if the certificate has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/create")
    public ResponseEntity<Certificate> createCertificate(@RequestBody Certificate certificate) throws URISyntaxException {
        log.debug("REST request to save Certificate : {}", certificate);
        if (certificate.getId() != null) {
            throw new BadRequestAlertException("A new certificate cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Certificate result = certificateService.save(certificate);
        return ResponseEntity.created(new URI("/api/certificates/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /certificates} : Updates an existing certificate.
     *
     * @param certificate the certificate to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated certificate,
     * or with status {@code 400 (Bad Request)} if the certificate is not valid,
     * or with status {@code 500 (Internal Server Error)} if the certificate couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/update")
    public ResponseEntity<Certificate> updateCertificate(@RequestBody Certificate certificate) throws URISyntaxException {
        log.debug("REST request to update Certificate : {}", certificate);
        if (certificate.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Certificate result = certificateService.save(certificate);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, certificate.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /certificates} : get all the certificates.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of certificates in body.
     */
    @GetMapping("/get-all")
    public ResponseEntity<List<Certificate>> getAllCertificates(Pageable pageable) {
        log.debug("REST request to get a page of Certificates");
        Page<Certificate> page = certificateService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /certificates/:id} : get the "id" certificate.
     *
     * @param id the id of the certificate to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the certificate, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Certificate> getCertificate(@PathVariable Long id) {
        log.debug("REST request to get Certificate : {}", id);
        Optional<Certificate> certificate = certificateService.findOne(id);
        return ResponseUtil.wrapOrNotFound(certificate);
    }

    /**
     * {@code DELETE  /certificates/:id} : delete the "id" certificate.
     *
     * @param id the id of the certificate to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCertificate(@PathVariable Long id) {
        log.debug("REST request to delete Certificate : {}", id);
        certificateService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    @GetMapping("/get-by-serial")
    public ResponseEntity<BaseResponseVM> getBase64Cert(@RequestParam String serial) {
        Optional<Certificate> certificate = certificateService.findBySerial(serial);
        if (certificate.isPresent())
            return ResponseEntity.ok(BaseResponseVM.CreateNewSuccessResponse(certificate.get().getRawData()));
        else
            return ResponseEntity.ok(BaseResponseVM.CreateNewErrorResponse("Cert is not found"));
    }
}
