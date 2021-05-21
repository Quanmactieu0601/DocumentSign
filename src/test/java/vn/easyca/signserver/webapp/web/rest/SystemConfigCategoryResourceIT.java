package vn.easyca.signserver.webapp.web.rest;

import vn.easyca.signserver.webapp.WebappApp;
import vn.easyca.signserver.webapp.domain.SystemConfigCategory;
import vn.easyca.signserver.webapp.repository.SystemConfigCategoryRepository;

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
 * Integration tests for the {@link SystemConfigCategoryResource} REST controller.
 */
@SpringBootTest(classes = WebappApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class SystemConfigCategoryResourceIT {

    private static final String DEFAULT_CONFIG_KEY = "AAAAAAAAAA";
    private static final String UPDATED_CONFIG_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_DATA_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_DATA_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    @Autowired
    private SystemConfigCategoryRepository systemConfigCategoryRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSystemConfigCategoryMockMvc;

    private SystemConfigCategory systemConfigCategory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SystemConfigCategory createEntity(EntityManager em) {
        SystemConfigCategory systemConfigCategory = new SystemConfigCategory()
            .configKey(DEFAULT_CONFIG_KEY)
            .dataType(DEFAULT_DATA_TYPE)
            .description(DEFAULT_DESCRIPTION);
        return systemConfigCategory;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SystemConfigCategory createUpdatedEntity(EntityManager em) {
        SystemConfigCategory systemConfigCategory = new SystemConfigCategory()
            .configKey(UPDATED_CONFIG_KEY)
            .dataType(UPDATED_DATA_TYPE)
            .description(UPDATED_DESCRIPTION);
        return systemConfigCategory;
    }

    @BeforeEach
    public void initTest() {
        systemConfigCategory = createEntity(em);
    }

    @Test
    @Transactional
    public void createSystemConfigCategory() throws Exception {
        int databaseSizeBeforeCreate = systemConfigCategoryRepository.findAll().size();
        // Create the SystemConfigCategory
        restSystemConfigCategoryMockMvc.perform(post("/api/system-config-categories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(systemConfigCategory)))
            .andExpect(status().isCreated());

        // Validate the SystemConfigCategory in the database
        List<SystemConfigCategory> systemConfigCategoryList = systemConfigCategoryRepository.findAll();
        assertThat(systemConfigCategoryList).hasSize(databaseSizeBeforeCreate + 1);
        SystemConfigCategory testSystemConfigCategory = systemConfigCategoryList.get(systemConfigCategoryList.size() - 1);
        assertThat(testSystemConfigCategory.getConfigKey()).isEqualTo(DEFAULT_CONFIG_KEY);
        assertThat(testSystemConfigCategory.getDataType()).isEqualTo(DEFAULT_DATA_TYPE);
        assertThat(testSystemConfigCategory.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void createSystemConfigCategoryWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = systemConfigCategoryRepository.findAll().size();

        // Create the SystemConfigCategory with an existing ID
        systemConfigCategory.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSystemConfigCategoryMockMvc.perform(post("/api/system-config-categories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(systemConfigCategory)))
            .andExpect(status().isBadRequest());

        // Validate the SystemConfigCategory in the database
        List<SystemConfigCategory> systemConfigCategoryList = systemConfigCategoryRepository.findAll();
        assertThat(systemConfigCategoryList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllSystemConfigCategories() throws Exception {
        // Initialize the database
        systemConfigCategoryRepository.saveAndFlush(systemConfigCategory);

        // Get all the systemConfigCategoryList
        restSystemConfigCategoryMockMvc.perform(get("/api/system-config-categories?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(systemConfigCategory.getId().intValue())))
            .andExpect(jsonPath("$.[*].configKey").value(hasItem(DEFAULT_CONFIG_KEY)))
            .andExpect(jsonPath("$.[*].dataType").value(hasItem(DEFAULT_DATA_TYPE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }
    
    @Test
    @Transactional
    public void getSystemConfigCategory() throws Exception {
        // Initialize the database
        systemConfigCategoryRepository.saveAndFlush(systemConfigCategory);

        // Get the systemConfigCategory
        restSystemConfigCategoryMockMvc.perform(get("/api/system-config-categories/{id}", systemConfigCategory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(systemConfigCategory.getId().intValue()))
            .andExpect(jsonPath("$.configKey").value(DEFAULT_CONFIG_KEY))
            .andExpect(jsonPath("$.dataType").value(DEFAULT_DATA_TYPE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }
    @Test
    @Transactional
    public void getNonExistingSystemConfigCategory() throws Exception {
        // Get the systemConfigCategory
        restSystemConfigCategoryMockMvc.perform(get("/api/system-config-categories/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSystemConfigCategory() throws Exception {
        // Initialize the database
        systemConfigCategoryRepository.saveAndFlush(systemConfigCategory);

        int databaseSizeBeforeUpdate = systemConfigCategoryRepository.findAll().size();

        // Update the systemConfigCategory
        SystemConfigCategory updatedSystemConfigCategory = systemConfigCategoryRepository.findById(systemConfigCategory.getId()).get();
        // Disconnect from session so that the updates on updatedSystemConfigCategory are not directly saved in db
        em.detach(updatedSystemConfigCategory);
        updatedSystemConfigCategory
            .configKey(UPDATED_CONFIG_KEY)
            .dataType(UPDATED_DATA_TYPE)
            .description(UPDATED_DESCRIPTION);

        restSystemConfigCategoryMockMvc.perform(put("/api/system-config-categories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedSystemConfigCategory)))
            .andExpect(status().isOk());

        // Validate the SystemConfigCategory in the database
        List<SystemConfigCategory> systemConfigCategoryList = systemConfigCategoryRepository.findAll();
        assertThat(systemConfigCategoryList).hasSize(databaseSizeBeforeUpdate);
        SystemConfigCategory testSystemConfigCategory = systemConfigCategoryList.get(systemConfigCategoryList.size() - 1);
        assertThat(testSystemConfigCategory.getConfigKey()).isEqualTo(UPDATED_CONFIG_KEY);
        assertThat(testSystemConfigCategory.getDataType()).isEqualTo(UPDATED_DATA_TYPE);
        assertThat(testSystemConfigCategory.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void updateNonExistingSystemConfigCategory() throws Exception {
        int databaseSizeBeforeUpdate = systemConfigCategoryRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSystemConfigCategoryMockMvc.perform(put("/api/system-config-categories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(systemConfigCategory)))
            .andExpect(status().isBadRequest());

        // Validate the SystemConfigCategory in the database
        List<SystemConfigCategory> systemConfigCategoryList = systemConfigCategoryRepository.findAll();
        assertThat(systemConfigCategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteSystemConfigCategory() throws Exception {
        // Initialize the database
        systemConfigCategoryRepository.saveAndFlush(systemConfigCategory);

        int databaseSizeBeforeDelete = systemConfigCategoryRepository.findAll().size();

        // Delete the systemConfigCategory
        restSystemConfigCategoryMockMvc.perform(delete("/api/system-config-categories/{id}", systemConfigCategory.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SystemConfigCategory> systemConfigCategoryList = systemConfigCategoryRepository.findAll();
        assertThat(systemConfigCategoryList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
