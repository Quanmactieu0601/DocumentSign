//package vn.easyca.signserver.webapp.web.rest;
//
//import vn.easyca.signserver.webapp.WebappApp;
//import vn.easyca.signserver.webapp.domain.Transaction;
//import vn.easyca.signserver.webapp.repository.TransactionRepository;
//import vn.easyca.signserver.webapp.service.TransactionService;
//import vn.easyca.signserver.webapp.service.dto.TransactionDTO;
//import vn.easyca.signserver.webapp.service.mapper.TransactionMapper;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.transaction.annotation.Transactional;
//import vn.easyca.signserver.webapp.web.rest.TransactionResource;
//
//import javax.persistence.EntityManager;
//import java.time.Instant;
//import java.time.LocalDateTime;
//import java.time.temporal.ChronoUnit;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.hamcrest.Matchers.hasItem;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
///**
// * Integration tests for the {@link TransactionResource} REST controller.
// */
//@SpringBootTest(classes = WebappApp.class)
//@AutoConfigureMockMvc
//@WithMockUser
//public class TransactionResourceIT {
//
//    private static final String DEFAULT_API = "AAAAAAAAAA";
//    private static final String UPDATED_API = "BBBBBBBBBB";
//
//    private static final LocalDateTime DEFAULT_TRIGGER_TIME = LocalDateTime.now();
//    private static final LocalDateTime UPDATED_TRIGGER_TIME = LocalDateTime.now();
//
//    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
//    private static final String UPDATE_STATUS = "BBBBBBBBBB";
//
//    private static final String DEFAULT_MESSAGE = "AAAAAAAAAA";
//    private static final String UPDATED_MESSAGE = "BBBBBBBBBB";
//
//    private static final String DEFAULT_DATA = "AAAAAAAAAA";
//    private static final String UPDATED_DATA = "BBBBBBBBBB";
//
//    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
//    private static final String UPDATED_TYPE = "BBBBBBBBBB";
//
//    @Autowired
//    private TransactionRepository transactionRepository;
//
//    @Autowired
//    private TransactionMapper transactionMapper;
//
//    @Autowired
//    private TransactionService transactionService;
//
//    @Autowired
//    private EntityManager em;
//
//    @Autowired
//    private MockMvc restTransactionMockMvc;
//
//    private Transaction transaction;
//
//    /**
//     * Create an entity for this test.
//     *
//     * This is a static method, as tests for other entities might also need it,
//     * if they test an entity which requires the current entity.
//     */
//    public static Transaction createEntity(EntityManager em) {
//        Transaction transaction = new Transaction()
//            .api(DEFAULT_API)
//            .triggerTime(DEFAULT_TRIGGER_TIME)
//            .status(Boolean.valueOf(DEFAULT_STATUS))
//            .message(DEFAULT_MESSAGE)
//            .data(DEFAULT_DATA)
//            .type(DEFAULT_TYPE);
//        return transaction;
//    }
//    /**
//     * Create an updated entity for this test.
//     *
//     * This is a static method, as tests for other entities might also need it,
//     * if they test an entity which requires the current entity.
//     */
//    public static Transaction createUpdatedEntity(EntityManager em) {
//        Transaction transaction = new Transaction()
//            .api(UPDATED_API)
//            .triggerTime(UPDATED_TRIGGER_TIME)
//            .status(Boolean.valueOf(DEFAULT_STATUS))
//            .message(UPDATED_MESSAGE)
//            .data(UPDATED_DATA)
//            .type(UPDATED_TYPE);
//        return transaction;
//    }
//
//    @BeforeEach
//    public void initTest() {
//        transaction = createEntity(em);
//    }
//
//    @Test
//    @Transactional
//    public void createTransaction() throws Exception {
//        int databaseSizeBeforeCreate = transactionRepository.findAll().size();
//        // Create the Transaction
//        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);
//        restTransactionMockMvc.perform(post("/api/transactions")
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(TestUtil.convertObjectToJsonBytes(transactionDTO)))
//            .andExpect(status().isCreated());
//
//        // Validate the Transaction in the database
//        List<Transaction> transactionList = transactionRepository.findAll();
//        assertThat(transactionList).hasSize(databaseSizeBeforeCreate + 1);
//        Transaction testTransaction = transactionList.get(transactionList.size() - 1);
//        assertThat(testTransaction.getApi()).isEqualTo(DEFAULT_API);
//        assertThat(testTransaction.getTriggerTime()).isEqualTo(DEFAULT_TRIGGER_TIME);
//        assertThat(testTransaction.getStatus()).isEqualTo(DEFAULT_STATUS);
//        assertThat(testTransaction.getMessage()).isEqualTo(DEFAULT_MESSAGE);
//        assertThat(testTransaction.getData()).isEqualTo(DEFAULT_DATA);
//        assertThat(testTransaction.getType()).isEqualTo(DEFAULT_TYPE);
//    }
//
//    @Test
//    @Transactional
//    public void createTransactionWithExistingId() throws Exception {
//        int databaseSizeBeforeCreate = transactionRepository.findAll().size();
//
//        // Create the Transaction with an existing ID
//        transaction.setId(1L);
//        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);
//
//        // An entity with an existing ID cannot be created, so this API call must fail
//        restTransactionMockMvc.perform(post("/api/transactions")
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(TestUtil.convertObjectToJsonBytes(transactionDTO)))
//            .andExpect(status().isBadRequest());
//
//        // Validate the Transaction in the database
//        List<Transaction> transactionList = transactionRepository.findAll();
//        assertThat(transactionList).hasSize(databaseSizeBeforeCreate);
//    }
//
//
//    @Test
//    @Transactional
//    public void getAllTransactions() throws Exception {
//        // Initialize the database
//        transactionRepository.saveAndFlush(transaction);
//
//        // Get all the transactionList
//        restTransactionMockMvc.perform(get("/api/transactions?sort=id,desc"))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
//            .andExpect(jsonPath("$.[*].id").value(hasItem(transaction.getId().intValue())))
//            .andExpect(jsonPath("$.[*].api").value(hasItem(DEFAULT_API)))
//            .andExpect(jsonPath("$.[*].triggerTime").value(hasItem(DEFAULT_TRIGGER_TIME.toString())))
//            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_STATUS)))
//            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
//            .andExpect(jsonPath("$.[*].data").value(hasItem(DEFAULT_DATA)))
//            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)));
//    }
//
//    @Test
//    @Transactional
//    public void getTransaction() throws Exception {
//        // Initialize the database
//        transactionRepository.saveAndFlush(transaction);
//
//        // Get the transaction
//        restTransactionMockMvc.perform(get("/api/transactions/{id}", transaction.getId()))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
//            .andExpect(jsonPath("$.id").value(transaction.getId().intValue()))
//            .andExpect(jsonPath("$.api").value(DEFAULT_API))
//            .andExpect(jsonPath("$.triggerTime").value(DEFAULT_TRIGGER_TIME.toString()))
//            .andExpect(jsonPath("$.code").value(DEFAULT_STATUS))
//            .andExpect(jsonPath("$.message").value(DEFAULT_MESSAGE))
//            .andExpect(jsonPath("$.data").value(DEFAULT_DATA))
//            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE));
//    }
//    @Test
//    @Transactional
//    public void getNonExistingTransaction() throws Exception {
//        // Get the transaction
//        restTransactionMockMvc.perform(get("/api/transactions/{id}", Long.MAX_VALUE))
//            .andExpect(status().isNotFound());
//    }
//
//    @Test
//    @Transactional
//    public void updateTransaction() throws Exception {
//        // Initialize the database
//        transactionRepository.saveAndFlush(transaction);
//
//        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();
//
//        // Update the transaction
//        Transaction updatedTransaction = transactionRepository.findById(transaction.getId()).get();
//        // Disconnect from session so that the updates on updatedTransaction are not directly saved in db
//        em.detach(updatedTransaction);
//        updatedTransaction
//            .api(UPDATED_API)
//            .triggerTime(UPDATED_TRIGGER_TIME)
//            .status(Boolean.valueOf(DEFAULT_STATUS))
//            .message(UPDATED_MESSAGE)
//            .data(UPDATED_DATA)
//            .type(UPDATED_TYPE);
//        TransactionDTO transactionDTO = transactionMapper.toDto(updatedTransaction);
//
//        restTransactionMockMvc.perform(put("/api/transactions")
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(TestUtil.convertObjectToJsonBytes(transactionDTO)))
//            .andExpect(status().isOk());
//
//        // Validate the Transaction in the database
//        List<Transaction> transactionList = transactionRepository.findAll();
//        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
//        Transaction testTransaction = transactionList.get(transactionList.size() - 1);
//        assertThat(testTransaction.getApi()).isEqualTo(UPDATED_API);
//        assertThat(testTransaction.getTriggerTime()).isEqualTo(UPDATED_TRIGGER_TIME);
//        assertThat(testTransaction.getStatus()).isEqualTo(DEFAULT_STATUS);
//        assertThat(testTransaction.getMessage()).isEqualTo(UPDATED_MESSAGE);
//        assertThat(testTransaction.getData()).isEqualTo(UPDATED_DATA);
//        assertThat(testTransaction.getType()).isEqualTo(UPDATED_TYPE);
//    }
//
//    @Test
//    @Transactional
//    public void updateNonExistingTransaction() throws Exception {
//        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();
//
//        // Create the Transaction
//        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);
//
//        // If the entity doesn't have an ID, it will throw BadRequestAlertException
//        restTransactionMockMvc.perform(put("/api/transactions")
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(TestUtil.convertObjectToJsonBytes(transactionDTO)))
//            .andExpect(status().isBadRequest());
//
//        // Validate the Transaction in the database
//        List<Transaction> transactionList = transactionRepository.findAll();
//        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
//    }
//
//    @Test
//    @Transactional
//    public void deleteTransaction() throws Exception {
//        // Initialize the database
//        transactionRepository.saveAndFlush(transaction);
//
//        int databaseSizeBeforeDelete = transactionRepository.findAll().size();
//
//        // Delete the transaction
//        restTransactionMockMvc.perform(delete("/api/transactions/{id}", transaction.getId())
//            .accept(MediaType.APPLICATION_JSON))
//            .andExpect(status().isNoContent());
//
//        // Validate the database contains one less item
//        List<Transaction> transactionList = transactionRepository.findAll();
//        assertThat(transactionList).hasSize(databaseSizeBeforeDelete - 1);
//    }
//}
