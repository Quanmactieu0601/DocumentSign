package vn.easyca.signserver.webapp.web.rest;

import vn.easyca.signserver.webapp.domain.Certificate;
import vn.easyca.signserver.webapp.domain.CertificateType;
import vn.easyca.signserver.webapp.service.certificate.CertificateService;
import vn.easyca.signserver.webapp.service.dto.CertificateGeneratorDto;
import vn.easyca.signserver.webapp.service.dto.ImportCertificateDto;
import vn.easyca.signserver.webapp.service.error.CreateCertificateException;
import vn.easyca.signserver.webapp.service.certificate.CertificateServiceFactory;
import vn.easyca.signserver.webapp.service.error.GenCertificateInputException;
import vn.easyca.signserver.webapp.utils.DateTimeUtils;
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
import vn.easyca.signserver.webapp.web.rest.vm.request.P12RegisterVM;
import vn.easyca.signserver.webapp.web.rest.vm.response.BaseResponseVM;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link Certificate}.
 */
@RestController
@RequestMapping("/api")
public class CertificateResource {

    private final Logger log = LoggerFactory.getLogger(CertificateResource.class);

    private static final String ENTITY_NAME = "certificate";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CertificateServiceFactory certificateServiceFactory;

    public CertificateResource(CertificateServiceFactory certificateServiceFactory) {
        this.certificateServiceFactory = certificateServiceFactory;
    }

    @PostMapping("/certificates/register/p12")
    public ResponseEntity<BaseResponseVM<String>> registerP12PKCS(@RequestBody P12RegisterVM p12RegisterVM) throws URISyntaxException {
        CertificateService certificateService = certificateServiceFactory.getService(CertificateType.PKCS12);
        ImportCertificateDto dto = new ImportCertificateDto();
        dto.setP12Base64(p12RegisterVM.getBase64Data());
        dto.setPin(p12RegisterVM.getPin());
        dto.setOwnerId(p12RegisterVM.getOwnerId());
        try {
            Certificate certificate = certificateService.importCertificate(dto);
            return ResponseEntity.ok(new BaseResponseVM<String>());
        } catch (CertificateService.NotImplementedException | CreateCertificateException e) {
            return ResponseEntity.ok(new BaseResponseVM<String>(-1, e.getMessage()));
        }
    }

    @PostMapping("/certificates/gen")
    public ResponseEntity<BaseResponseVM<String>> genCertificate(@RequestBody GenCertificateVM genCertificateVM) throws URISyntaxException {
        try {
            CertificateService certificateService = certificateServiceFactory.getService(CertificateType.PKCS11);
            CertificateGeneratorDto dto = new CertificateGeneratorDto();
            dto.setC(genCertificateVM.getC());
            dto.setCn(genCertificateVM.getCn());
            dto.setFromDate(DateTimeUtils.parse(genCertificateVM.getFromDate()));
            dto.setFromDate(DateTimeUtils.parse(genCertificateVM.getToDate()));
            dto.setKeyLen(genCertificateVM.getKeyLen());
            dto.setL(genCertificateVM.getL());
            dto.setOu(genCertificateVM.getOu());
            dto.setOwnerId(genCertificateVM.getOwnerId());
            dto.setPassword(genCertificateVM.getPassword());
            Certificate certificate = certificateService.genCertificate(dto);
            certificateService.save(certificate);
            return ResponseEntity.ok(new BaseResponseVM<String>());
        } catch (ParseException | CertificateService.NotImplementedException | CreateCertificateException | GenCertificateInputException e) {
            e.printStackTrace();
            return ResponseEntity.ok(new BaseResponseVM<String>(-1, e.getMessage()));
        }
    }

    /**
     * {@code POST  /certificates} : Create a new certificate.
     *
     * @param certificate the certificate to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new certificate, or with status {@code 400 (Bad Request)} if the certificate has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/certificates")
    public ResponseEntity<Certificate> createCertificate(@RequestBody Certificate certificate) throws URISyntaxException {
        log.debug("REST request to save Certificate : {}", certificate);
        if (certificate.getId() != null) {
            throw new BadRequestAlertException("A new certificate cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Certificate result = certificateServiceFactory.getService(certificate.getCertificateType()).save(certificate);
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
    @PutMapping("/certificates")
    public ResponseEntity<Certificate> updateCertificate(@RequestBody Certificate certificate) throws URISyntaxException {
        log.debug("REST request to update Certificate : {}", certificate);
        if (certificate.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Certificate result = resolveService().save(certificate);
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
    @GetMapping("/certificates")
    public ResponseEntity<List<Certificate>> getAllCertificates(Pageable pageable) {
        log.debug("REST request to get a page of Certificates");
        Page<Certificate> page = resolveService().findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /certificates/:id} : get the "id" certificate.
     *
     * @param id the id of the certificate to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the certificate, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/certificates/{id}")
    public ResponseEntity<Certificate> getCertificate(@PathVariable Long id) {
        log.debug("REST request to get Certificate : {}", id);
        Optional<Certificate> certificate = resolveService().findOne(id);
        return ResponseUtil.wrapOrNotFound(certificate);
    }

    /**
     * {@code DELETE  /certificates/:id} : delete the "id" certificate.
     *
     * @param id the id of the certificate to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/certificates/{id}")
    public ResponseEntity<Void> deleteCertificate(@PathVariable Long id) {
        log.debug("REST request to delete Certificate : {}", id);
        resolveService().delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    @PostMapping("/certificate/getbyserial")
    public ResponseEntity<BaseResponseVM<String>> getBase64Cert(@RequestBody String serial) {

//        Certificate certificate = resolveService().findBySerial(serial);
//        if (certificate == null)
//            return ResponseEntity.ok(new BaseResponseVM<String>(-1, "Không tìm thấy chứng thư"));
//        return ResponseEntity.ok(new BaseResponseVM<String>(0,certificate.getRawData()));
        return ResponseEntity.ok(new BaseResponseVM<String>(0, "MIIEGzCCAwOgAwIBAgIQVAT//rcDP7MW1nIgG4DTmDANBgkqhkiG9w0BAQUFADBO\r\nMQswCQYDVQQGEwJWTjESMBAGA1UEBwwJSMOgIE7hu5lpMRYwFAYDVQQKEw1WaWV0\r\ndGVsIEdyb3VwMRMwEQYDVQQDEwpWaWV0dGVsLUNBMB4XDTIwMDMwNTA4MzI1OVoX\r\nDTIxMDMwNTA4MzI1OVowgZYxHjAcBgoJkiaJk/IsZAEBDA5NU1Q6MDEwNTk4NzQz\r\nMjFTMFEGA1UEAwxKQ8OUTkcgVFkgQ+G7lCBQSOG6pk4gxJDhuqZVIFTGryBDw5RO\r\nRyBOR0jhu4YgVsOAIFRIxq/GoE5HIE3huqBJIFNPRlREUkVBTVMxEjAQBgNVBAcM\r\nCUjDgCBO4buYSTELMAkGA1UEBhMCVk4wgZ8wDQYJKoZIhvcNAQEBBQADgY0AMIGJ\r\nAoGBAN45MIHLnjVj1vpa4WJ2tYtyxDrIwkREjDqTaKgGPryrxxcipfQpZdqCSWIk\r\nPUf6K44I5jcK8s1YeoC6hADjVKrpsz8baQz/dBSYy5oJxdiMweTJQq9QlbNw+kx1\r\n5W9aEi2k2DOb+i3yBTQDkc+u/ylLGF5F7njajQhoKR8PxuvXAgMBAAGjggEuMIIB\r\nKjA1BggrBgEFBQcBAQQpMCcwJQYIKwYBBQUHMAGGGWh0dHA6Ly9vY3NwLnZpZXR0\r\nZWwtY2Eudm4wHQYDVR0OBBYEFAz4ANgRsBDOZXFKGBWg0tOa8nHSMAwGA1UdEwEB\r\n/wQCMAAwHwYDVR0jBBgwFoAU/stBFOldp9kyDeSyCFzxXOy2srUwgZIGA1UdHwSB\r\nijCBhzCBhKAuoCyGKmh0dHA6Ly9jcmwudmlldHRlbC1jYS52bi9WaWV0dGVsLUNB\r\nLXYzLmNybKJSpFAwTjETMBEGA1UEAwwKVmlldHRlbC1DQTEWMBQGA1UECgwNVmll\r\ndHRlbCBHcm91cDESMBAGA1UEBwwJSMOgIE7hu5lpMQswCQYDVQQGEwJWTjAOBgNV\r\nHQ8BAf8EBAMCBeAwDQYJKoZIhvcNAQEFBQADggEBAJGwolmW8aFUp7cViSErVDxh\r\nMfgB6mOd5bW+jBphpULpezPJ7vNteuKjKhtGVOkGuwOyuCKR2IK2uRNlMGi6kE9j\r\nUV5W4R5/DVM5oFRmTgs9Q7W1Sy/RytUyJXVtvehDY2hwS3YhtfWJ57Cw0zmPj28a\r\n7vgOy7Pzbx7YAoR2UTrP5gmVuyIAFJ1r+r0BNDcyK8uHeq29h6hKXuRc5K8kUZ3c\r\nnIl7WeNuLCWULB+k5DpxpajDSvSJR7rZlgvg4i64p3lsvSucndM9iD1vEE03VEMY\r\nIMZEWh6LYvQ7f/Ah9V98MTdkRN2CpmtptrMsBDzb6+UDzrE0rqFyZFfICsaGrZ4="));

    }


    private CertificateService resolveService(CertificateType certificateType) {
        return certificateServiceFactory.getService(certificateType);
    }

    private CertificateService resolveService() {
        return certificateServiceFactory.getService((String) null);
    }
}
