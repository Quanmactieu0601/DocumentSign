package vn.easyca.signserver.webapp.web.rest;

import vn.easyca.signserver.webapp.WebappApp;
import vn.easyca.signserver.webapp.domain.SignatureImage;
import vn.easyca.signserver.webapp.repository.SignatureImageRepository;
import vn.easyca.signserver.webapp.service.SignatureImageService;
import vn.easyca.signserver.webapp.service.dto.SignatureImageDTO;
import vn.easyca.signserver.webapp.service.mapper.SignatureImageMapper;

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
 * Integration tests for the {@link SignatureImageResource} REST controller.
 */
@SpringBootTest(classes = WebappApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class SignatureImageResourceIT {

    private static final String DEFAULT_IMG_DATA = "AAAAAAAAAA";
    private static final String UPDATED_IMG_DATA = "BBBBBBBBBB";

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;

    @Autowired
    private SignatureImageRepository signatureImageRepository;

    @Autowired
    private SignatureImageMapper signatureImageMapper;

    @Autowired
    private SignatureImageService signatureImageService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSignatureImageMockMvc;

    private SignatureImage signatureImage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SignatureImage createEntity(EntityManager em) {
        SignatureImage signatureImage = new SignatureImage()
            .imgData(DEFAULT_IMG_DATA)
            .userId(DEFAULT_USER_ID);
        return signatureImage;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SignatureImage createUpdatedEntity(EntityManager em) {
        SignatureImage signatureImage = new SignatureImage()
            .imgData(UPDATED_IMG_DATA)
            .userId(UPDATED_USER_ID);
        return signatureImage;
    }

    @BeforeEach
    public void initTest() {
        signatureImage = createEntity(em);
    }

    @Test
    @Transactional
    public void createSignatureImage() throws Exception {
        int databaseSizeBeforeCreate = signatureImageRepository.findAll().size();
        // Create the SignatureImage
        SignatureImageDTO signatureImageDTO = signatureImageMapper.toDto(signatureImage);
        restSignatureImageMockMvc.perform(post("/api/signature-images")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(signatureImageDTO)))
            .andExpect(status().isCreated());

        // Validate the SignatureImage in the database
        List<SignatureImage> signatureImageList = signatureImageRepository.findAll();
        assertThat(signatureImageList).hasSize(databaseSizeBeforeCreate + 1);
        SignatureImage testSignatureImage = signatureImageList.get(signatureImageList.size() - 1);
        assertThat(testSignatureImage.getImgData()).isEqualTo(DEFAULT_IMG_DATA);
        assertThat(testSignatureImage.getUserId()).isEqualTo(DEFAULT_USER_ID);
    }

    @Test
    @Transactional
    public void createSignatureImageWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = signatureImageRepository.findAll().size();

        // Create the SignatureImage with an existing ID
        signatureImage.setId(1L);
        SignatureImageDTO signatureImageDTO = signatureImageMapper.toDto(signatureImage);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSignatureImageMockMvc.perform(post("/api/signature-images")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(signatureImageDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SignatureImage in the database
        List<SignatureImage> signatureImageList = signatureImageRepository.findAll();
        assertThat(signatureImageList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllSignatureImages() throws Exception {
        // Initialize the database
        signatureImageRepository.saveAndFlush(signatureImage);

        // Get all the signatureImageList
        restSignatureImageMockMvc.perform(get("/api/signature-images?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(signatureImage.getId().intValue())))
            .andExpect(jsonPath("$.[*].imgData").value(hasItem(DEFAULT_IMG_DATA)))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())));
    }
    
    @Test
    @Transactional
    public void getSignatureImage() throws Exception {
        // Initialize the database
        signatureImageRepository.saveAndFlush(signatureImage);

        // Get the signatureImage
        restSignatureImageMockMvc.perform(get("/api/signature-images/{id}", signatureImage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(signatureImage.getId().intValue()))
            .andExpect(jsonPath("$.imgData").value(DEFAULT_IMG_DATA))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID.intValue()));
    }
    @Test
    @Transactional
    public void getNonExistingSignatureImage() throws Exception {
        // Get the signatureImage
        restSignatureImageMockMvc.perform(get("/api/signature-images/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSignatureImage() throws Exception {
        // Initialize the database
        signatureImageRepository.saveAndFlush(signatureImage);

        int databaseSizeBeforeUpdate = signatureImageRepository.findAll().size();

        // Update the signatureImage
        SignatureImage updatedSignatureImage = signatureImageRepository.findById(signatureImage.getId()).get();
        // Disconnect from session so that the updates on updatedSignatureImage are not directly saved in db
        em.detach(updatedSignatureImage);
        updatedSignatureImage
            .imgData(UPDATED_IMG_DATA)
            .userId(UPDATED_USER_ID);
        SignatureImageDTO signatureImageDTO = signatureImageMapper.toDto(updatedSignatureImage);

        restSignatureImageMockMvc.perform(put("/api/signature-images")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(signatureImageDTO)))
            .andExpect(status().isOk());

        // Validate the SignatureImage in the database
        List<SignatureImage> signatureImageList = signatureImageRepository.findAll();
        assertThat(signatureImageList).hasSize(databaseSizeBeforeUpdate);
        SignatureImage testSignatureImage = signatureImageList.get(signatureImageList.size() - 1);
        assertThat(testSignatureImage.getImgData()).isEqualTo(UPDATED_IMG_DATA);
        assertThat(testSignatureImage.getUserId()).isEqualTo(UPDATED_USER_ID);
    }

    @Test
    @Transactional
    public void updateNonExistingSignatureImage() throws Exception {
        int databaseSizeBeforeUpdate = signatureImageRepository.findAll().size();

        // Create the SignatureImage
        SignatureImageDTO signatureImageDTO = signatureImageMapper.toDto(signatureImage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSignatureImageMockMvc.perform(put("/api/signature-images")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(signatureImageDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SignatureImage in the database
        List<SignatureImage> signatureImageList = signatureImageRepository.findAll();
        assertThat(signatureImageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteSignatureImage() throws Exception {
        // Initialize the database
        signatureImageRepository.saveAndFlush(signatureImage);

        int databaseSizeBeforeDelete = signatureImageRepository.findAll().size();

        // Delete the signatureImage
        restSignatureImageMockMvc.perform(delete("/api/signature-images/{id}", signatureImage.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SignatureImage> signatureImageList = signatureImageRepository.findAll();
        assertThat(signatureImageList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
