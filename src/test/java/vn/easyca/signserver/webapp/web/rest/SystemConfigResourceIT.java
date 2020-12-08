package vn.easyca.signserver.webapp.web.rest;

import vn.easyca.signserver.webapp.WebappApp;
import vn.easyca.signserver.webapp.domain.SystemConfig;
import vn.easyca.signserver.webapp.repository.SystemConfigRepository;
import vn.easyca.signserver.webapp.service.SystemConfigService;
import vn.easyca.signserver.webapp.service.dto.SystemConfigDTO;
import vn.easyca.signserver.webapp.service.mapper.SystemConfigMapper;

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
 * Integration tests for the {@link SystemConfigResource} REST controller.
 */
@SpringBootTest(classes = WebappApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class SystemConfigResourceIT {

    private static final Long DEFAULT_COM_ID = 1L;
    private static final Long UPDATED_COM_ID = 2L;

    private static final String DEFAULT_KEY = "AAAAAAAAAA";
    private static final String UPDATED_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_DATA_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_DATA_TYPE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVATED = false;
    private static final Boolean UPDATED_ACTIVATED = true;

    @Autowired
    private SystemConfigRepository systemConfigRepository;

    @Autowired
    private SystemConfigMapper systemConfigMapper;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSystemConfigMockMvc;

    private SystemConfig systemConfig;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SystemConfig createEntity(EntityManager em) {
        SystemConfig systemConfig = new SystemConfig()
            .comId(DEFAULT_COM_ID)
            .key(DEFAULT_KEY)
            .value(DEFAULT_VALUE)
            .description(DEFAULT_DESCRIPTION)
            .dataType(DEFAULT_DATA_TYPE)
            .activated(DEFAULT_ACTIVATED);
        return systemConfig;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SystemConfig createUpdatedEntity(EntityManager em) {
        SystemConfig systemConfig = new SystemConfig()
            .comId(UPDATED_COM_ID)
            .key(UPDATED_KEY)
            .value(UPDATED_VALUE)
            .description(UPDATED_DESCRIPTION)
            .dataType(UPDATED_DATA_TYPE)
            .activated(UPDATED_ACTIVATED);
        return systemConfig;
    }

    @BeforeEach
    public void initTest() {
        systemConfig = createEntity(em);
    }

    @Test
    @Transactional
    public void createSystemConfig() throws Exception {
        int databaseSizeBeforeCreate = systemConfigRepository.findAll().size();
        // Create the SystemConfig
        SystemConfigDTO systemConfigDTO = systemConfigMapper.toDto(systemConfig);
        restSystemConfigMockMvc.perform(post("/api/system-configs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(systemConfigDTO)))
            .andExpect(status().isCreated());

        // Validate the SystemConfig in the database
        List<SystemConfig> systemConfigList = systemConfigRepository.findAll();
        assertThat(systemConfigList).hasSize(databaseSizeBeforeCreate + 1);
        SystemConfig testSystemConfig = systemConfigList.get(systemConfigList.size() - 1);
        assertThat(testSystemConfig.getComId()).isEqualTo(DEFAULT_COM_ID);
        assertThat(testSystemConfig.getKey()).isEqualTo(DEFAULT_KEY);
        assertThat(testSystemConfig.getValue()).isEqualTo(DEFAULT_VALUE);
        assertThat(testSystemConfig.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testSystemConfig.getDataType()).isEqualTo(DEFAULT_DATA_TYPE);
        assertThat(testSystemConfig.isActivated()).isEqualTo(DEFAULT_ACTIVATED);
    }

    @Test
    @Transactional
    public void createSystemConfigWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = systemConfigRepository.findAll().size();

        // Create the SystemConfig with an existing ID
        systemConfig.setId(1L);
        SystemConfigDTO systemConfigDTO = systemConfigMapper.toDto(systemConfig);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSystemConfigMockMvc.perform(post("/api/system-configs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(systemConfigDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SystemConfig in the database
        List<SystemConfig> systemConfigList = systemConfigRepository.findAll();
        assertThat(systemConfigList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllSystemConfigs() throws Exception {
        // Initialize the database
        systemConfigRepository.saveAndFlush(systemConfig);

        // Get all the systemConfigList
        restSystemConfigMockMvc.perform(get("/api/system-configs?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(systemConfig.getId().intValue())))
            .andExpect(jsonPath("$.[*].comId").value(hasItem(DEFAULT_COM_ID.intValue())))
            .andExpect(jsonPath("$.[*].key").value(hasItem(DEFAULT_KEY)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].dataType").value(hasItem(DEFAULT_DATA_TYPE)))
            .andExpect(jsonPath("$.[*].activated").value(hasItem(DEFAULT_ACTIVATED.booleanValue())));
    }
    
    @Test
    @Transactional
    public void getSystemConfig() throws Exception {
        // Initialize the database
        systemConfigRepository.saveAndFlush(systemConfig);

        // Get the systemConfig
        restSystemConfigMockMvc.perform(get("/api/system-configs/{id}", systemConfig.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(systemConfig.getId().intValue()))
            .andExpect(jsonPath("$.comId").value(DEFAULT_COM_ID.intValue()))
            .andExpect(jsonPath("$.key").value(DEFAULT_KEY))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.dataType").value(DEFAULT_DATA_TYPE))
            .andExpect(jsonPath("$.activated").value(DEFAULT_ACTIVATED.booleanValue()));
    }
    @Test
    @Transactional
    public void getNonExistingSystemConfig() throws Exception {
        // Get the systemConfig
        restSystemConfigMockMvc.perform(get("/api/system-configs/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSystemConfig() throws Exception {
        // Initialize the database
        systemConfigRepository.saveAndFlush(systemConfig);

        int databaseSizeBeforeUpdate = systemConfigRepository.findAll().size();

        // Update the systemConfig
        SystemConfig updatedSystemConfig = systemConfigRepository.findById(systemConfig.getId()).get();
        // Disconnect from session so that the updates on updatedSystemConfig are not directly saved in db
        em.detach(updatedSystemConfig);
        updatedSystemConfig
            .comId(UPDATED_COM_ID)
            .key(UPDATED_KEY)
            .value(UPDATED_VALUE)
            .description(UPDATED_DESCRIPTION)
            .dataType(UPDATED_DATA_TYPE)
            .activated(UPDATED_ACTIVATED);
        SystemConfigDTO systemConfigDTO = systemConfigMapper.toDto(updatedSystemConfig);

        restSystemConfigMockMvc.perform(put("/api/system-configs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(systemConfigDTO)))
            .andExpect(status().isOk());

        // Validate the SystemConfig in the database
        List<SystemConfig> systemConfigList = systemConfigRepository.findAll();
        assertThat(systemConfigList).hasSize(databaseSizeBeforeUpdate);
        SystemConfig testSystemConfig = systemConfigList.get(systemConfigList.size() - 1);
        assertThat(testSystemConfig.getComId()).isEqualTo(UPDATED_COM_ID);
        assertThat(testSystemConfig.getKey()).isEqualTo(UPDATED_KEY);
        assertThat(testSystemConfig.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testSystemConfig.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testSystemConfig.getDataType()).isEqualTo(UPDATED_DATA_TYPE);
        assertThat(testSystemConfig.isActivated()).isEqualTo(UPDATED_ACTIVATED);
    }

    @Test
    @Transactional
    public void updateNonExistingSystemConfig() throws Exception {
        int databaseSizeBeforeUpdate = systemConfigRepository.findAll().size();

        // Create the SystemConfig
        SystemConfigDTO systemConfigDTO = systemConfigMapper.toDto(systemConfig);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSystemConfigMockMvc.perform(put("/api/system-configs")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(systemConfigDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SystemConfig in the database
        List<SystemConfig> systemConfigList = systemConfigRepository.findAll();
        assertThat(systemConfigList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteSystemConfig() throws Exception {
        // Initialize the database
        systemConfigRepository.saveAndFlush(systemConfig);

        int databaseSizeBeforeDelete = systemConfigRepository.findAll().size();

        // Delete the systemConfig
        restSystemConfigMockMvc.perform(delete("/api/system-configs/{id}", systemConfig.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SystemConfig> systemConfigList = systemConfigRepository.findAll();
        assertThat(systemConfigList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
