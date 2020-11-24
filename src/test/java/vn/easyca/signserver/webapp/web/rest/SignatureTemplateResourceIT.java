package vn.easyca.signserver.webapp.web.rest;

import vn.easyca.signserver.webapp.WebappApp;
import vn.easyca.signserver.webapp.domain.SignatureTemplate;
import vn.easyca.signserver.webapp.repository.SignatureTemplateRepository;
import vn.easyca.signserver.webapp.service.SignatureTemplateService;
import vn.easyca.signserver.webapp.service.dto.SignatureTemplateDTO;
import vn.easyca.signserver.webapp.service.mapper.SignatureTemplateMapper;

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
 * Integration tests for the {@link SignatureTemplateResource} REST controller.
 */
@SpringBootTest(classes = WebappApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class SignatureTemplateResourceIT {

    private static final String DEFAULT_SIGNATURE_IMAGE = "AAAAAAAAAA";
    private static final String UPDATED_SIGNATURE_IMAGE = "BBBBBBBBBB";

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;

    @Autowired
    private SignatureTemplateRepository signatureTemplateRepository;

    @Autowired
    private SignatureTemplateMapper signatureTemplateMapper;

    @Autowired
    private SignatureTemplateService signatureTemplateService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSignatureTemplateMockMvc;

    private SignatureTemplate signatureTemplate;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SignatureTemplate createEntity(EntityManager em) {
        SignatureTemplate signatureTemplate = new SignatureTemplate()
            .userId(DEFAULT_USER_ID);
        return signatureTemplate;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SignatureTemplate createUpdatedEntity(EntityManager em) {
        SignatureTemplate signatureTemplate = new SignatureTemplate()
            .userId(UPDATED_USER_ID);
        return signatureTemplate;
    }

    @BeforeEach
    public void initTest() {
        signatureTemplate = createEntity(em);
    }

    @Test
    @Transactional
    public void createSignatureTemplate() throws Exception {
        int databaseSizeBeforeCreate = signatureTemplateRepository.findAll().size();
        // Create the SignatureTemplate
        SignatureTemplateDTO signatureTemplateDTO = signatureTemplateMapper.toDto(signatureTemplate);
        restSignatureTemplateMockMvc.perform(post("/api/signature-templates")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(signatureTemplateDTO)))
            .andExpect(status().isCreated());

        // Validate the SignatureTemplate in the database
        List<SignatureTemplate> signatureTemplateList = signatureTemplateRepository.findAll();
        assertThat(signatureTemplateList).hasSize(databaseSizeBeforeCreate + 1);
        SignatureTemplate testSignatureTemplate = signatureTemplateList.get(signatureTemplateList.size() - 1);
        assertThat(testSignatureTemplate.getUserId()).isEqualTo(DEFAULT_USER_ID);
    }

    @Test
    @Transactional
    public void createSignatureTemplateWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = signatureTemplateRepository.findAll().size();

        // Create the SignatureTemplate with an existing ID
        signatureTemplate.setId(1L);
        SignatureTemplateDTO signatureTemplateDTO = signatureTemplateMapper.toDto(signatureTemplate);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSignatureTemplateMockMvc.perform(post("/api/signature-templates")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(signatureTemplateDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SignatureTemplate in the database
        List<SignatureTemplate> signatureTemplateList = signatureTemplateRepository.findAll();
        assertThat(signatureTemplateList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllSignatureTemplates() throws Exception {
        // Initialize the database
        signatureTemplateRepository.saveAndFlush(signatureTemplate);

        // Get all the signatureTemplateList
        restSignatureTemplateMockMvc.perform(get("/api/signature-templates?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(signatureTemplate.getId().intValue())))
            .andExpect(jsonPath("$.[*].signatureImage").value(hasItem(DEFAULT_SIGNATURE_IMAGE)))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())));
    }

    @Test
    @Transactional
    public void getSignatureTemplate() throws Exception {
        // Initialize the database
        signatureTemplateRepository.saveAndFlush(signatureTemplate);

        // Get the signatureTemplate
        restSignatureTemplateMockMvc.perform(get("/api/signature-templates/{id}", signatureTemplate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(signatureTemplate.getId().intValue()))
            .andExpect(jsonPath("$.signatureImage").value(DEFAULT_SIGNATURE_IMAGE))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID.intValue()));
    }
    @Test
    @Transactional
    public void getNonExistingSignatureTemplate() throws Exception {
        // Get the signatureTemplate
        restSignatureTemplateMockMvc.perform(get("/api/signature-templates/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSignatureTemplate() throws Exception {
        // Initialize the database
        signatureTemplateRepository.saveAndFlush(signatureTemplate);

        int databaseSizeBeforeUpdate = signatureTemplateRepository.findAll().size();

        // Update the signatureTemplate
        SignatureTemplate updatedSignatureTemplate = signatureTemplateRepository.findById(signatureTemplate.getId()).get();
        // Disconnect from session so that the updates on updatedSignatureTemplate are not directly saved in db
        em.detach(updatedSignatureTemplate);
        updatedSignatureTemplate
            .userId(UPDATED_USER_ID);
        SignatureTemplateDTO signatureTemplateDTO = signatureTemplateMapper.toDto(updatedSignatureTemplate);

        restSignatureTemplateMockMvc.perform(put("/api/signature-templates")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(signatureTemplateDTO)))
            .andExpect(status().isOk());

        // Validate the SignatureTemplate in the database
        List<SignatureTemplate> signatureTemplateList = signatureTemplateRepository.findAll();
        assertThat(signatureTemplateList).hasSize(databaseSizeBeforeUpdate);
        SignatureTemplate testSignatureTemplate = signatureTemplateList.get(signatureTemplateList.size() - 1);
        assertThat(testSignatureTemplate.getUserId()).isEqualTo(UPDATED_USER_ID);
    }

    @Test
    @Transactional
    public void updateNonExistingSignatureTemplate() throws Exception {
        int databaseSizeBeforeUpdate = signatureTemplateRepository.findAll().size();

        // Create the SignatureTemplate
        SignatureTemplateDTO signatureTemplateDTO = signatureTemplateMapper.toDto(signatureTemplate);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSignatureTemplateMockMvc.perform(put("/api/signature-templates")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(signatureTemplateDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SignatureTemplate in the database
        List<SignatureTemplate> signatureTemplateList = signatureTemplateRepository.findAll();
        assertThat(signatureTemplateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteSignatureTemplate() throws Exception {
        // Initialize the database
        signatureTemplateRepository.saveAndFlush(signatureTemplate);

        int databaseSizeBeforeDelete = signatureTemplateRepository.findAll().size();

        // Delete the signatureTemplate
        restSignatureTemplateMockMvc.perform(delete("/api/signature-templates/{id}", signatureTemplate.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SignatureTemplate> signatureTemplateList = signatureTemplateRepository.findAll();
        assertThat(signatureTemplateList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
