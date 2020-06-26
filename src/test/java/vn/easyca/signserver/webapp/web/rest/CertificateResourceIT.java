package vn.easyca.signserver.webapp.web.rest;

import vn.easyca.signserver.webapp.WebappApp;
import vn.easyca.signserver.webapp.domain.Certificate;
import vn.easyca.signserver.webapp.repository.CertificateRepository;
import vn.easyca.signserver.webapp.service.CertificateService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link CertificateResource} REST controller.
 */
@SpringBootTest(classes = WebappApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class CertificateResourceIT {

    private static final String DEFAULT_LAST_UPDATE = "AAAAAAAAAA";
    private static final String UPDATED_LAST_UPDATE = "BBBBBBBBBB";

    private static final String DEFAULT_TOKEN_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TOKEN_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_SERIAL = "AAAAAAAAAA";
    private static final String UPDATED_SERIAL = "BBBBBBBBBB";

    private static final String DEFAULT_OWNER_TAXCODE = "AAAAAAAAAA";
    private static final String UPDATED_OWNER_TAXCODE = "BBBBBBBBBB";

    private static final String DEFAULT_SUBJECT_INFO = "AAAAAAAAAA";
    private static final String UPDATED_SUBJECT_INFO = "BBBBBBBBBB";

    private static final String DEFAULT_ALIAS = "AAAAAAAAAA";
    private static final String UPDATED_ALIAS = "BBBBBBBBBB";

    private static final String DEFAULT_TOKEN_INFO = "AAAAAAAAAA";
    private static final String UPDATED_TOKEN_INFO = "BBBBBBBBBB";

    private static final String DEFAULT_RAW_DATA = "AAAAAAAAAA";
    private static final String UPDATED_RAW_DATA = "BBBBBBBBBB";

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCertificateMockMvc;

    private Certificate certificate;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Certificate createEntity(EntityManager em) {
        Certificate certificate = new Certificate()
            .lastUpdate(DEFAULT_LAST_UPDATE)
            .tokenType(DEFAULT_TOKEN_TYPE)
            .serial(DEFAULT_SERIAL)
            .ownerTaxcode(DEFAULT_OWNER_TAXCODE)
            .subjectInfo(DEFAULT_SUBJECT_INFO)
            .alias(DEFAULT_ALIAS)
            .tokenInfo(DEFAULT_TOKEN_INFO)
            .rawData(DEFAULT_RAW_DATA);
        return certificate;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Certificate createUpdatedEntity(EntityManager em) {
        Certificate certificate = new Certificate()
            .lastUpdate(UPDATED_LAST_UPDATE)
            .tokenType(UPDATED_TOKEN_TYPE)
            .serial(UPDATED_SERIAL)
            .ownerTaxcode(UPDATED_OWNER_TAXCODE)
            .subjectInfo(UPDATED_SUBJECT_INFO)
            .alias(UPDATED_ALIAS)
            .tokenInfo(UPDATED_TOKEN_INFO)
            .rawData(UPDATED_RAW_DATA);
        return certificate;
    }

    @BeforeEach
    public void initTest() {
        certificate = createEntity(em);
    }

    @Test
    @Transactional
    public void createCertificate() throws Exception {
        int databaseSizeBeforeCreate = certificateRepository.findAll().size();
        // Create the Certificate
        restCertificateMockMvc.perform(post("/api/certificates")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(certificate)))
            .andExpect(status().isCreated());

        // Validate the Certificate in the database
        List<Certificate> certificateList = certificateRepository.findAll();
        assertThat(certificateList).hasSize(databaseSizeBeforeCreate + 1);
        Certificate testCertificate = certificateList.get(certificateList.size() - 1);
        assertThat(testCertificate.getLastUpdate()).isEqualTo(DEFAULT_LAST_UPDATE);
        assertThat(testCertificate.getTokenType()).isEqualTo(DEFAULT_TOKEN_TYPE);
        assertThat(testCertificate.getSerial()).isEqualTo(DEFAULT_SERIAL);
        assertThat(testCertificate.getOwnerTaxcode()).isEqualTo(DEFAULT_OWNER_TAXCODE);
        assertThat(testCertificate.getSubjectInfo()).isEqualTo(DEFAULT_SUBJECT_INFO);
        assertThat(testCertificate.getAlias()).isEqualTo(DEFAULT_ALIAS);
        assertThat(testCertificate.getTokenInfo()).isEqualTo(DEFAULT_TOKEN_INFO);
        assertThat(testCertificate.getRawData()).isEqualTo(DEFAULT_RAW_DATA);
    }

    @Test
    @Transactional
    public void createCertificateWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = certificateRepository.findAll().size();

        // Create the Certificate with an existing ID
        certificate.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCertificateMockMvc.perform(post("/api/certificates")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(certificate)))
            .andExpect(status().isBadRequest());

        // Validate the Certificate in the database
        List<Certificate> certificateList = certificateRepository.findAll();
        assertThat(certificateList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllCertificates() throws Exception {
        // Initialize the database
        certificateRepository.saveAndFlush(certificate);

        // Get all the certificateList
        restCertificateMockMvc.perform(get("/api/certificates?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(certificate.getId().intValue())))
            .andExpect(jsonPath("$.[*].lastUpdate").value(hasItem(DEFAULT_LAST_UPDATE)))
            .andExpect(jsonPath("$.[*].tokenType").value(hasItem(DEFAULT_TOKEN_TYPE)))
            .andExpect(jsonPath("$.[*].serial").value(hasItem(DEFAULT_SERIAL)))
            .andExpect(jsonPath("$.[*].ownerTaxcode").value(hasItem(DEFAULT_OWNER_TAXCODE)))
            .andExpect(jsonPath("$.[*].subjectInfo").value(hasItem(DEFAULT_SUBJECT_INFO)))
            .andExpect(jsonPath("$.[*].alias").value(hasItem(DEFAULT_ALIAS)))
            .andExpect(jsonPath("$.[*].tokenInfo").value(hasItem(DEFAULT_TOKEN_INFO)))
            .andExpect(jsonPath("$.[*].rawData").value(hasItem(DEFAULT_RAW_DATA)));
    }
    
    @Test
    @Transactional
    public void getCertificate() throws Exception {
        // Initialize the database
        certificateRepository.saveAndFlush(certificate);

        // Get the certificate
        restCertificateMockMvc.perform(get("/api/certificates/{id}", certificate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(certificate.getId().intValue()))
            .andExpect(jsonPath("$.lastUpdate").value(DEFAULT_LAST_UPDATE))
            .andExpect(jsonPath("$.tokenType").value(DEFAULT_TOKEN_TYPE))
            .andExpect(jsonPath("$.serial").value(DEFAULT_SERIAL))
            .andExpect(jsonPath("$.ownerTaxcode").value(DEFAULT_OWNER_TAXCODE))
            .andExpect(jsonPath("$.subjectInfo").value(DEFAULT_SUBJECT_INFO))
            .andExpect(jsonPath("$.alias").value(DEFAULT_ALIAS))
            .andExpect(jsonPath("$.tokenInfo").value(DEFAULT_TOKEN_INFO))
            .andExpect(jsonPath("$.rawData").value(DEFAULT_RAW_DATA));
    }
    @Test
    @Transactional
    public void getNonExistingCertificate() throws Exception {
        // Get the certificate
        restCertificateMockMvc.perform(get("/api/certificates/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCertificate() throws Exception {
        // Initialize the database
        certificateService.save(certificate);

        int databaseSizeBeforeUpdate = certificateRepository.findAll().size();

        // Update the certificate
        Certificate updatedCertificate = certificateRepository.findById(certificate.getId()).get();
        // Disconnect from session so that the updates on updatedCertificate are not directly saved in db
        em.detach(updatedCertificate);
        updatedCertificate
            .lastUpdate(UPDATED_LAST_UPDATE)
            .tokenType(UPDATED_TOKEN_TYPE)
            .serial(UPDATED_SERIAL)
            .ownerTaxcode(UPDATED_OWNER_TAXCODE)
            .subjectInfo(UPDATED_SUBJECT_INFO)
            .alias(UPDATED_ALIAS)
            .tokenInfo(UPDATED_TOKEN_INFO)
            .rawData(UPDATED_RAW_DATA);

        restCertificateMockMvc.perform(put("/api/certificates")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedCertificate)))
            .andExpect(status().isOk());

        // Validate the Certificate in the database
        List<Certificate> certificateList = certificateRepository.findAll();
        assertThat(certificateList).hasSize(databaseSizeBeforeUpdate);
        Certificate testCertificate = certificateList.get(certificateList.size() - 1);
        assertThat(testCertificate.getLastUpdate()).isEqualTo(UPDATED_LAST_UPDATE);
        assertThat(testCertificate.getTokenType()).isEqualTo(UPDATED_TOKEN_TYPE);
        assertThat(testCertificate.getSerial()).isEqualTo(UPDATED_SERIAL);
        assertThat(testCertificate.getOwnerTaxcode()).isEqualTo(UPDATED_OWNER_TAXCODE);
        assertThat(testCertificate.getSubjectInfo()).isEqualTo(UPDATED_SUBJECT_INFO);
        assertThat(testCertificate.getAlias()).isEqualTo(UPDATED_ALIAS);
        assertThat(testCertificate.getTokenInfo()).isEqualTo(UPDATED_TOKEN_INFO);
        assertThat(testCertificate.getRawData()).isEqualTo(UPDATED_RAW_DATA);
    }

    @Test
    @Transactional
    public void updateNonExistingCertificate() throws Exception {
        int databaseSizeBeforeUpdate = certificateRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCertificateMockMvc.perform(put("/api/certificates")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(certificate)))
            .andExpect(status().isBadRequest());

        // Validate the Certificate in the database
        List<Certificate> certificateList = certificateRepository.findAll();
        assertThat(certificateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteCertificate() throws Exception {
        // Initialize the database
        certificateService.save(certificate);

        int databaseSizeBeforeDelete = certificateRepository.findAll().size();

        // Delete the certificate
        restCertificateMockMvc.perform(delete("/api/certificates/{id}", certificate.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Certificate> certificateList = certificateRepository.findAll();
        assertThat(certificateList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
