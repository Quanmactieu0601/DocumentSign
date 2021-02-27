package vn.easyca.signserver.webapp.web.rest;

import vn.easyca.signserver.webapp.WebappApp;
import vn.easyca.signserver.webapp.domain.CoreParser;
import vn.easyca.signserver.webapp.repository.CoreParserRepository;
import vn.easyca.signserver.webapp.service.CoreParserService;
import vn.easyca.signserver.webapp.service.dto.CoreParserDTO;
import vn.easyca.signserver.webapp.service.mapper.CoreParserMapper;
import vn.easyca.signserver.webapp.service.dto.CoreParserCriteria;
import vn.easyca.signserver.webapp.service.CoreParserQueryService;

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
 * Integration tests for the {@link CoreParserResource} REST controller.
 */
@SpringBootTest(classes = WebappApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class CoreParserResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Autowired
    private CoreParserRepository coreParserRepository;

    @Autowired
    private CoreParserMapper coreParserMapper;

    @Autowired
    private CoreParserService coreParserService;

    @Autowired
    private CoreParserQueryService coreParserQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCoreParserMockMvc;

    private CoreParser coreParser;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CoreParser createEntity(EntityManager em) {
        CoreParser coreParser = new CoreParser()
            .name(DEFAULT_NAME);
        return coreParser;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CoreParser createUpdatedEntity(EntityManager em) {
        CoreParser coreParser = new CoreParser()
            .name(UPDATED_NAME);
        return coreParser;
    }

    @BeforeEach
    public void initTest() {
        coreParser = createEntity(em);
    }

    @Test
    @Transactional
    public void getAllCoreParsers() throws Exception {
        // Initialize the database
        coreParserRepository.saveAndFlush(coreParser);

        // Get all the coreParserList
        restCoreParserMockMvc.perform(get("/api/core-parsers?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(coreParser.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }
    
    @Test
    @Transactional
    public void getCoreParser() throws Exception {
        // Initialize the database
        coreParserRepository.saveAndFlush(coreParser);

        // Get the coreParser
        restCoreParserMockMvc.perform(get("/api/core-parsers/{id}", coreParser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(coreParser.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }


    @Test
    @Transactional
    public void getCoreParsersByIdFiltering() throws Exception {
        // Initialize the database
        coreParserRepository.saveAndFlush(coreParser);

        Long id = coreParser.getId();

        defaultCoreParserShouldBeFound("id.equals=" + id);
        defaultCoreParserShouldNotBeFound("id.notEquals=" + id);

        defaultCoreParserShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultCoreParserShouldNotBeFound("id.greaterThan=" + id);

        defaultCoreParserShouldBeFound("id.lessThanOrEqual=" + id);
        defaultCoreParserShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllCoreParsersByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        coreParserRepository.saveAndFlush(coreParser);

        // Get all the coreParserList where name equals to DEFAULT_NAME
        defaultCoreParserShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the coreParserList where name equals to UPDATED_NAME
        defaultCoreParserShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllCoreParsersByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        coreParserRepository.saveAndFlush(coreParser);

        // Get all the coreParserList where name not equals to DEFAULT_NAME
        defaultCoreParserShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the coreParserList where name not equals to UPDATED_NAME
        defaultCoreParserShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllCoreParsersByNameIsInShouldWork() throws Exception {
        // Initialize the database
        coreParserRepository.saveAndFlush(coreParser);

        // Get all the coreParserList where name in DEFAULT_NAME or UPDATED_NAME
        defaultCoreParserShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the coreParserList where name equals to UPDATED_NAME
        defaultCoreParserShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllCoreParsersByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        coreParserRepository.saveAndFlush(coreParser);

        // Get all the coreParserList where name is not null
        defaultCoreParserShouldBeFound("name.specified=true");

        // Get all the coreParserList where name is null
        defaultCoreParserShouldNotBeFound("name.specified=false");
    }
                @Test
    @Transactional
    public void getAllCoreParsersByNameContainsSomething() throws Exception {
        // Initialize the database
        coreParserRepository.saveAndFlush(coreParser);

        // Get all the coreParserList where name contains DEFAULT_NAME
        defaultCoreParserShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the coreParserList where name contains UPDATED_NAME
        defaultCoreParserShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllCoreParsersByNameNotContainsSomething() throws Exception {
        // Initialize the database
        coreParserRepository.saveAndFlush(coreParser);

        // Get all the coreParserList where name does not contain DEFAULT_NAME
        defaultCoreParserShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the coreParserList where name does not contain UPDATED_NAME
        defaultCoreParserShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCoreParserShouldBeFound(String filter) throws Exception {
        restCoreParserMockMvc.perform(get("/api/core-parsers?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(coreParser.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));

        // Check, that the count call also returns 1
        restCoreParserMockMvc.perform(get("/api/core-parsers/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCoreParserShouldNotBeFound(String filter) throws Exception {
        restCoreParserMockMvc.perform(get("/api/core-parsers?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCoreParserMockMvc.perform(get("/api/core-parsers/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingCoreParser() throws Exception {
        // Get the coreParser
        restCoreParserMockMvc.perform(get("/api/core-parsers/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }
}
