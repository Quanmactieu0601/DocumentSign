package vn.easyca.signserver.webapp.web.rest;

import vn.easyca.signserver.webapp.WebappApp;
import vn.easyca.signserver.webapp.domain.Authority;
import vn.easyca.signserver.webapp.domain.UserEntity;
import vn.easyca.signserver.webapp.repository.UserRepository;
import vn.easyca.signserver.webapp.security.AuthoritiesConstants;
import vn.easyca.signserver.webapp.service.dto.UserDTO;
import vn.easyca.signserver.webapp.service.mapper.UserMapper;
import vn.easyca.signserver.webapp.web.rest.controller.UserResource;
import vn.easyca.signserver.webapp.web.rest.vm.ManagedUserVM;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link UserResource} REST controller.
 */
@AutoConfigureMockMvc
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
@SpringBootTest(classes = WebappApp.class)
public class UserEntityResourceIT {

    private static final String DEFAULT_LOGIN = "johndoe";
    private static final String UPDATED_LOGIN = "jhipster";

    private static final Long DEFAULT_ID = 1L;

    private static final String DEFAULT_PASSWORD = "passjohndoe";
    private static final String UPDATED_PASSWORD = "passjhipster";

    private static final String DEFAULT_EMAIL = "johndoe@localhost";
    private static final String UPDATED_EMAIL = "jhipster@localhost";

    private static final String DEFAULT_FIRSTNAME = "john";
    private static final String UPDATED_FIRSTNAME = "jhipsterFirstName";

    private static final String DEFAULT_LASTNAME = "doe";
    private static final String UPDATED_LASTNAME = "jhipsterLastName";

    private static final String DEFAULT_IMAGEURL = "http://placehold.it/50x50";
    private static final String UPDATED_IMAGEURL = "http://placehold.it/40x40";

    private static final String DEFAULT_LANGKEY = "en";
    private static final String UPDATED_LANGKEY = "fr";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private MockMvc restUserMockMvc;

    private UserEntity userEntity;

    @BeforeEach
    public void setup() {
        cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).clear();
        cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE).clear();
    }

    /**
     * Create a User.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which has a required relationship to the User entity.
     */
    public static UserEntity createEntity(EntityManager em) {
        UserEntity userEntity = new UserEntity();
        userEntity.setLogin(DEFAULT_LOGIN + RandomStringUtils.randomAlphabetic(5));
        userEntity.setPassword(RandomStringUtils.random(60));
        userEntity.setActivated(true);
        userEntity.setEmail(RandomStringUtils.randomAlphabetic(5) + DEFAULT_EMAIL);
        userEntity.setFirstName(DEFAULT_FIRSTNAME);
        userEntity.setLastName(DEFAULT_LASTNAME);
        userEntity.setImageUrl(DEFAULT_IMAGEURL);
        userEntity.setLangKey(DEFAULT_LANGKEY);
        return userEntity;
    }

    @BeforeEach
    public void initTest() {
        userEntity = createEntity(em);
        userEntity.setLogin(DEFAULT_LOGIN);
        userEntity.setEmail(DEFAULT_EMAIL);
    }

    @Test
    @Transactional
    public void createUser() throws Exception {
        int databaseSizeBeforeCreate = userRepository.findAll().size();

        // Create the User
        ManagedUserVM managedUserVM = new ManagedUserVM();
        managedUserVM.setLogin(DEFAULT_LOGIN);
        managedUserVM.setPassword(DEFAULT_PASSWORD);
        managedUserVM.setFirstName(DEFAULT_FIRSTNAME);
        managedUserVM.setLastName(DEFAULT_LASTNAME);
        managedUserVM.setEmail(DEFAULT_EMAIL);
        managedUserVM.setActivated(true);
        managedUserVM.setImageUrl(DEFAULT_IMAGEURL);
        managedUserVM.setLangKey(DEFAULT_LANGKEY);
        managedUserVM.setAuthorities(Collections.singleton(AuthoritiesConstants.USER));

        restUserMockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(managedUserVM)))
            .andExpect(status().isCreated());

        // Validate the User in the database
        assertPersistedUsers(users -> {
            assertThat(users).hasSize(databaseSizeBeforeCreate + 1);
            UserEntity testUserEntity = users.get(users.size() - 1);
            assertThat(testUserEntity.getLogin()).isEqualTo(DEFAULT_LOGIN);
            assertThat(testUserEntity.getFirstName()).isEqualTo(DEFAULT_FIRSTNAME);
            assertThat(testUserEntity.getLastName()).isEqualTo(DEFAULT_LASTNAME);
            assertThat(testUserEntity.getEmail()).isEqualTo(DEFAULT_EMAIL);
            assertThat(testUserEntity.getImageUrl()).isEqualTo(DEFAULT_IMAGEURL);
            assertThat(testUserEntity.getLangKey()).isEqualTo(DEFAULT_LANGKEY);
        });
    }

    @Test
    @Transactional
    public void createUserWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = userRepository.findAll().size();

        ManagedUserVM managedUserVM = new ManagedUserVM();
        managedUserVM.setId(1L);
        managedUserVM.setLogin(DEFAULT_LOGIN);
        managedUserVM.setPassword(DEFAULT_PASSWORD);
        managedUserVM.setFirstName(DEFAULT_FIRSTNAME);
        managedUserVM.setLastName(DEFAULT_LASTNAME);
        managedUserVM.setEmail(DEFAULT_EMAIL);
        managedUserVM.setActivated(true);
        managedUserVM.setImageUrl(DEFAULT_IMAGEURL);
        managedUserVM.setLangKey(DEFAULT_LANGKEY);
        managedUserVM.setAuthorities(Collections.singleton(AuthoritiesConstants.USER));

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserMockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(managedUserVM)))
            .andExpect(status().isBadRequest());

        // Validate the User in the database
        assertPersistedUsers(users -> assertThat(users).hasSize(databaseSizeBeforeCreate));
    }

    @Test
    @Transactional
    public void createUserWithExistingLogin() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(userEntity);
        int databaseSizeBeforeCreate = userRepository.findAll().size();

        ManagedUserVM managedUserVM = new ManagedUserVM();
        managedUserVM.setLogin(DEFAULT_LOGIN);// this login should already be used
        managedUserVM.setPassword(DEFAULT_PASSWORD);
        managedUserVM.setFirstName(DEFAULT_FIRSTNAME);
        managedUserVM.setLastName(DEFAULT_LASTNAME);
        managedUserVM.setEmail("anothermail@localhost");
        managedUserVM.setActivated(true);
        managedUserVM.setImageUrl(DEFAULT_IMAGEURL);
        managedUserVM.setLangKey(DEFAULT_LANGKEY);
        managedUserVM.setAuthorities(Collections.singleton(AuthoritiesConstants.USER));

        // Create the User
        restUserMockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(managedUserVM)))
            .andExpect(status().isBadRequest());

        // Validate the User in the database
        assertPersistedUsers(users -> assertThat(users).hasSize(databaseSizeBeforeCreate));
    }

    @Test
    @Transactional
    public void createUserWithExistingEmail() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(userEntity);
        int databaseSizeBeforeCreate = userRepository.findAll().size();

        ManagedUserVM managedUserVM = new ManagedUserVM();
        managedUserVM.setLogin("anotherlogin");
        managedUserVM.setPassword(DEFAULT_PASSWORD);
        managedUserVM.setFirstName(DEFAULT_FIRSTNAME);
        managedUserVM.setLastName(DEFAULT_LASTNAME);
        managedUserVM.setEmail(DEFAULT_EMAIL);// this email should already be used
        managedUserVM.setActivated(true);
        managedUserVM.setImageUrl(DEFAULT_IMAGEURL);
        managedUserVM.setLangKey(DEFAULT_LANGKEY);
        managedUserVM.setAuthorities(Collections.singleton(AuthoritiesConstants.USER));

        // Create the User
        restUserMockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(managedUserVM)))
            .andExpect(status().isBadRequest());

        // Validate the User in the database
        assertPersistedUsers(users -> assertThat(users).hasSize(databaseSizeBeforeCreate));
    }

    @Test
    @Transactional
    public void getAllUsers() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(userEntity);

        // Get all the users
        restUserMockMvc.perform(get("/api/users?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].login").value(hasItem(DEFAULT_LOGIN)))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRSTNAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LASTNAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].imageUrl").value(hasItem(DEFAULT_IMAGEURL)))
            .andExpect(jsonPath("$.[*].langKey").value(hasItem(DEFAULT_LANGKEY)));
    }

    @Test
    @Transactional
    public void getUser() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(userEntity);

        assertThat(cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).get(userEntity.getLogin())).isNull();

        // Get the user
        restUserMockMvc.perform(get("/api/users/{login}", userEntity.getLogin()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.login").value(userEntity.getLogin()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRSTNAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LASTNAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.imageUrl").value(DEFAULT_IMAGEURL))
            .andExpect(jsonPath("$.langKey").value(DEFAULT_LANGKEY));

        assertThat(cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).get(userEntity.getLogin())).isNotNull();
    }

    @Test
    @Transactional
    public void getNonExistingUser() throws Exception {
        restUserMockMvc.perform(get("/api/users/unknown"))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateUser() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(userEntity);
        int databaseSizeBeforeUpdate = userRepository.findAll().size();

        // Update the user
        UserEntity updatedUserEntity = userRepository.findById(userEntity.getId()).get();

        ManagedUserVM managedUserVM = new ManagedUserVM();
        managedUserVM.setId(updatedUserEntity.getId());
        managedUserVM.setLogin(updatedUserEntity.getLogin());
        managedUserVM.setPassword(UPDATED_PASSWORD);
        managedUserVM.setFirstName(UPDATED_FIRSTNAME);
        managedUserVM.setLastName(UPDATED_LASTNAME);
        managedUserVM.setEmail(UPDATED_EMAIL);
        managedUserVM.setActivated(updatedUserEntity.getActivated());
        managedUserVM.setImageUrl(UPDATED_IMAGEURL);
        managedUserVM.setLangKey(UPDATED_LANGKEY);
        managedUserVM.setCreatedBy(updatedUserEntity.getCreatedBy());
        managedUserVM.setCreatedDate(updatedUserEntity.getCreatedDate());
        managedUserVM.setLastModifiedBy(updatedUserEntity.getLastModifiedBy());
        managedUserVM.setLastModifiedDate(updatedUserEntity.getLastModifiedDate());
        managedUserVM.setAuthorities(Collections.singleton(AuthoritiesConstants.USER));

        restUserMockMvc.perform(put("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(managedUserVM)))
            .andExpect(status().isOk());

        // Validate the User in the database
        assertPersistedUsers(users -> {
            assertThat(users).hasSize(databaseSizeBeforeUpdate);
            UserEntity testUserEntity = users.get(users.size() - 1);
            assertThat(testUserEntity.getFirstName()).isEqualTo(UPDATED_FIRSTNAME);
            assertThat(testUserEntity.getLastName()).isEqualTo(UPDATED_LASTNAME);
            assertThat(testUserEntity.getEmail()).isEqualTo(UPDATED_EMAIL);
            assertThat(testUserEntity.getImageUrl()).isEqualTo(UPDATED_IMAGEURL);
            assertThat(testUserEntity.getLangKey()).isEqualTo(UPDATED_LANGKEY);
        });
    }

    @Test
    @Transactional
    public void updateUserLogin() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(userEntity);
        int databaseSizeBeforeUpdate = userRepository.findAll().size();

        // Update the user
        UserEntity updatedUserEntity = userRepository.findById(userEntity.getId()).get();

        ManagedUserVM managedUserVM = new ManagedUserVM();
        managedUserVM.setId(updatedUserEntity.getId());
        managedUserVM.setLogin(UPDATED_LOGIN);
        managedUserVM.setPassword(UPDATED_PASSWORD);
        managedUserVM.setFirstName(UPDATED_FIRSTNAME);
        managedUserVM.setLastName(UPDATED_LASTNAME);
        managedUserVM.setEmail(UPDATED_EMAIL);
        managedUserVM.setActivated(updatedUserEntity.getActivated());
        managedUserVM.setImageUrl(UPDATED_IMAGEURL);
        managedUserVM.setLangKey(UPDATED_LANGKEY);
        managedUserVM.setCreatedBy(updatedUserEntity.getCreatedBy());
        managedUserVM.setCreatedDate(updatedUserEntity.getCreatedDate());
        managedUserVM.setLastModifiedBy(updatedUserEntity.getLastModifiedBy());
        managedUserVM.setLastModifiedDate(updatedUserEntity.getLastModifiedDate());
        managedUserVM.setAuthorities(Collections.singleton(AuthoritiesConstants.USER));

        restUserMockMvc.perform(put("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(managedUserVM)))
            .andExpect(status().isOk());

        // Validate the User in the database
        assertPersistedUsers(users -> {
            assertThat(users).hasSize(databaseSizeBeforeUpdate);
            UserEntity testUserEntity = users.get(users.size() - 1);
            assertThat(testUserEntity.getLogin()).isEqualTo(UPDATED_LOGIN);
            assertThat(testUserEntity.getFirstName()).isEqualTo(UPDATED_FIRSTNAME);
            assertThat(testUserEntity.getLastName()).isEqualTo(UPDATED_LASTNAME);
            assertThat(testUserEntity.getEmail()).isEqualTo(UPDATED_EMAIL);
            assertThat(testUserEntity.getImageUrl()).isEqualTo(UPDATED_IMAGEURL);
            assertThat(testUserEntity.getLangKey()).isEqualTo(UPDATED_LANGKEY);
        });
    }

    @Test
    @Transactional
    public void updateUserExistingEmail() throws Exception {
        // Initialize the database with 2 users
        userRepository.saveAndFlush(userEntity);

        UserEntity anotherUserEntity = new UserEntity();
        anotherUserEntity.setLogin("jhipster");
        anotherUserEntity.setPassword(RandomStringUtils.random(60));
        anotherUserEntity.setActivated(true);
        anotherUserEntity.setEmail("jhipster@localhost");
        anotherUserEntity.setFirstName("java");
        anotherUserEntity.setLastName("hipster");
        anotherUserEntity.setImageUrl("");
        anotherUserEntity.setLangKey("en");
        userRepository.saveAndFlush(anotherUserEntity);

        // Update the user
        UserEntity updatedUserEntity = userRepository.findById(userEntity.getId()).get();

        ManagedUserVM managedUserVM = new ManagedUserVM();
        managedUserVM.setId(updatedUserEntity.getId());
        managedUserVM.setLogin(updatedUserEntity.getLogin());
        managedUserVM.setPassword(updatedUserEntity.getPassword());
        managedUserVM.setFirstName(updatedUserEntity.getFirstName());
        managedUserVM.setLastName(updatedUserEntity.getLastName());
        managedUserVM.setEmail("jhipster@localhost");// this email should already be used by anotherUser
        managedUserVM.setActivated(updatedUserEntity.getActivated());
        managedUserVM.setImageUrl(updatedUserEntity.getImageUrl());
        managedUserVM.setLangKey(updatedUserEntity.getLangKey());
        managedUserVM.setCreatedBy(updatedUserEntity.getCreatedBy());
        managedUserVM.setCreatedDate(updatedUserEntity.getCreatedDate());
        managedUserVM.setLastModifiedBy(updatedUserEntity.getLastModifiedBy());
        managedUserVM.setLastModifiedDate(updatedUserEntity.getLastModifiedDate());
        managedUserVM.setAuthorities(Collections.singleton(AuthoritiesConstants.USER));

        restUserMockMvc.perform(put("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(managedUserVM)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void updateUserExistingLogin() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(userEntity);

        UserEntity anotherUserEntity = new UserEntity();
        anotherUserEntity.setLogin("jhipster");
        anotherUserEntity.setPassword(RandomStringUtils.random(60));
        anotherUserEntity.setActivated(true);
        anotherUserEntity.setEmail("jhipster@localhost");
        anotherUserEntity.setFirstName("java");
        anotherUserEntity.setLastName("hipster");
        anotherUserEntity.setImageUrl("");
        anotherUserEntity.setLangKey("en");
        userRepository.saveAndFlush(anotherUserEntity);

        // Update the user
        UserEntity updatedUserEntity = userRepository.findById(userEntity.getId()).get();

        ManagedUserVM managedUserVM = new ManagedUserVM();
        managedUserVM.setId(updatedUserEntity.getId());
        managedUserVM.setLogin("jhipster");// this login should already be used by anotherUser
        managedUserVM.setPassword(updatedUserEntity.getPassword());
        managedUserVM.setFirstName(updatedUserEntity.getFirstName());
        managedUserVM.setLastName(updatedUserEntity.getLastName());
        managedUserVM.setEmail(updatedUserEntity.getEmail());
        managedUserVM.setActivated(updatedUserEntity.getActivated());
        managedUserVM.setImageUrl(updatedUserEntity.getImageUrl());
        managedUserVM.setLangKey(updatedUserEntity.getLangKey());
        managedUserVM.setCreatedBy(updatedUserEntity.getCreatedBy());
        managedUserVM.setCreatedDate(updatedUserEntity.getCreatedDate());
        managedUserVM.setLastModifiedBy(updatedUserEntity.getLastModifiedBy());
        managedUserVM.setLastModifiedDate(updatedUserEntity.getLastModifiedDate());
        managedUserVM.setAuthorities(Collections.singleton(AuthoritiesConstants.USER));

        restUserMockMvc.perform(put("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(managedUserVM)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void deleteUser() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(userEntity);
        int databaseSizeBeforeDelete = userRepository.findAll().size();

        // Delete the user
        restUserMockMvc.perform(delete("/api/users/{login}", userEntity.getLogin())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        assertThat(cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).get(userEntity.getLogin())).isNull();

        // Validate the database is empty
        assertPersistedUsers(users -> assertThat(users).hasSize(databaseSizeBeforeDelete - 1));
    }

    @Test
    @Transactional
    public void getAllAuthorities() throws Exception {
        restUserMockMvc.perform(get("/api/users/authorities")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").value(hasItems(AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN)));
    }

    @Test
    public void testUserEquals() throws Exception {
        TestUtil.equalsVerifier(UserEntity.class);
        UserEntity userEntity1 = new UserEntity();
        userEntity1.setId(1L);
        UserEntity userEntity2 = new UserEntity();
        userEntity2.setId(userEntity1.getId());
        assertThat(userEntity1).isEqualTo(userEntity2);
        userEntity2.setId(2L);
        assertThat(userEntity1).isNotEqualTo(userEntity2);
        userEntity1.setId(null);
        assertThat(userEntity1).isNotEqualTo(userEntity2);
    }

    @Test
    public void testUserDTOtoUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(DEFAULT_ID);
        userDTO.setLogin(DEFAULT_LOGIN);
        userDTO.setFirstName(DEFAULT_FIRSTNAME);
        userDTO.setLastName(DEFAULT_LASTNAME);
        userDTO.setEmail(DEFAULT_EMAIL);
        userDTO.setActivated(true);
        userDTO.setImageUrl(DEFAULT_IMAGEURL);
        userDTO.setLangKey(DEFAULT_LANGKEY);
        userDTO.setCreatedBy(DEFAULT_LOGIN);
        userDTO.setLastModifiedBy(DEFAULT_LOGIN);
        userDTO.setAuthorities(Collections.singleton(AuthoritiesConstants.USER));

        UserEntity userEntity = userMapper.userDTOToUser(userDTO);
        assertThat(userEntity.getId()).isEqualTo(DEFAULT_ID);
        assertThat(userEntity.getLogin()).isEqualTo(DEFAULT_LOGIN);
        assertThat(userEntity.getFirstName()).isEqualTo(DEFAULT_FIRSTNAME);
        assertThat(userEntity.getLastName()).isEqualTo(DEFAULT_LASTNAME);
        assertThat(userEntity.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(userEntity.getActivated()).isEqualTo(true);
        assertThat(userEntity.getImageUrl()).isEqualTo(DEFAULT_IMAGEURL);
        assertThat(userEntity.getLangKey()).isEqualTo(DEFAULT_LANGKEY);
        assertThat(userEntity.getCreatedBy()).isNull();
        assertThat(userEntity.getCreatedDate()).isNotNull();
        assertThat(userEntity.getLastModifiedBy()).isNull();
        assertThat(userEntity.getLastModifiedDate()).isNotNull();
        assertThat(userEntity.getAuthorities()).extracting("name").containsExactly(AuthoritiesConstants.USER);
    }

    @Test
    public void testUserToUserDTO() {
        userEntity.setId(DEFAULT_ID);
        userEntity.setCreatedBy(DEFAULT_LOGIN);
        userEntity.setCreatedDate(Instant.now());
        userEntity.setLastModifiedBy(DEFAULT_LOGIN);
        userEntity.setLastModifiedDate(Instant.now());
        Set<Authority> authorities = new HashSet<>();
        Authority authority = new Authority();
        authority.setName(AuthoritiesConstants.USER);
        authorities.add(authority);
        userEntity.setAuthorities(authorities);

        UserDTO userDTO = userMapper.userToUserDTO(userEntity);

        assertThat(userDTO.getId()).isEqualTo(DEFAULT_ID);
        assertThat(userDTO.getLogin()).isEqualTo(DEFAULT_LOGIN);
        assertThat(userDTO.getFirstName()).isEqualTo(DEFAULT_FIRSTNAME);
        assertThat(userDTO.getLastName()).isEqualTo(DEFAULT_LASTNAME);
        assertThat(userDTO.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(userDTO.isActivated()).isEqualTo(true);
        assertThat(userDTO.getImageUrl()).isEqualTo(DEFAULT_IMAGEURL);
        assertThat(userDTO.getLangKey()).isEqualTo(DEFAULT_LANGKEY);
        assertThat(userDTO.getCreatedBy()).isEqualTo(DEFAULT_LOGIN);
        assertThat(userDTO.getCreatedDate()).isEqualTo(userEntity.getCreatedDate());
        assertThat(userDTO.getLastModifiedBy()).isEqualTo(DEFAULT_LOGIN);
        assertThat(userDTO.getLastModifiedDate()).isEqualTo(userEntity.getLastModifiedDate());
        assertThat(userDTO.getAuthorities()).containsExactly(AuthoritiesConstants.USER);
        assertThat(userDTO.toString()).isNotNull();
    }

    @Test
    public void testAuthorityEquals() {
        Authority authorityA = new Authority();
        assertThat(authorityA).isEqualTo(authorityA);
        assertThat(authorityA).isNotEqualTo(null);
        assertThat(authorityA).isNotEqualTo(new Object());
        assertThat(authorityA.hashCode()).isEqualTo(0);
        assertThat(authorityA.toString()).isNotNull();

        Authority authorityB = new Authority();
        assertThat(authorityA).isEqualTo(authorityB);

        authorityB.setName(AuthoritiesConstants.ADMIN);
        assertThat(authorityA).isNotEqualTo(authorityB);

        authorityA.setName(AuthoritiesConstants.USER);
        assertThat(authorityA).isNotEqualTo(authorityB);

        authorityB.setName(AuthoritiesConstants.USER);
        assertThat(authorityA).isEqualTo(authorityB);
        assertThat(authorityA.hashCode()).isEqualTo(authorityB.hashCode());
    }

    private void assertPersistedUsers(Consumer<List<UserEntity>> userAssertion) {
        userAssertion.accept(userRepository.findAll());
    }
}
