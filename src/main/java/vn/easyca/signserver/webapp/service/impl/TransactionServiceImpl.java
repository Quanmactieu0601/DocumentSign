package vn.easyca.signserver.webapp.service.impl;

import vn.easyca.signserver.webapp.enm.*;
import vn.easyca.signserver.webapp.service.TransactionService;
import vn.easyca.signserver.webapp.domain.Transaction;
import vn.easyca.signserver.webapp.repository.TransactionRepository;
import vn.easyca.signserver.webapp.service.dto.TransactionDTO;
import vn.easyca.signserver.webapp.service.mapper.TransactionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.easyca.signserver.webapp.utils.DateTimeUtils;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static vn.easyca.signserver.webapp.utils.DateTimeUtils.convertToLocalDateTime;

/**
 * Service Implementation for managing {@link Transaction}.
 */
@Service
@Transactional
public class TransactionServiceImpl implements TransactionService  {

    private final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final TransactionRepository transactionRepository;


    private final TransactionMapper transactionMapper;

    public TransactionServiceImpl(TransactionRepository transactionRepository, TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }

    /**
     * Save a transaction.
     *
     * @param transactionDTO the entity to save.
     * @return the persisted entity.
     */


    @Override
    public TransactionDTO save(TransactionDTO transactionDTO) {
        log.debug("Request to save Transaction : {}", transactionDTO);
        Transaction transaction = transactionMapper.toEntity(transactionDTO);
        transaction = transactionRepository.save(transaction);
        return transactionMapper.toDto(transaction);
    }

    /**
     * Get all the transactions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TransactionDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Transactions");
        return transactionRepository.findAll(pageable)
            .map(transactionMapper::toDto);
    }
    /**
     * Get one transaction by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<TransactionDTO> findOne(Long id) {
        log.debug("Request to get Transaction : {}", id);
        return transactionRepository.findById(id)
            .map(transactionMapper::toDto);
    }
    @Override
    @Transactional(readOnly = true)
    public Page<TransactionDTO> getByFilter(Pageable pageable, String api, String triggerTime, String status, String message, String data, String type, String host, String method, String createdBy, String fullName, String startDate, String endDate, String action, String extension) throws ParseException {
        LocalDateTime startDateConverted = DateTimeUtils.convertToLocalDateTime(startDate);
        LocalDateTime endDateConverted = DateTimeUtils.convertToLocalDateTime(endDate);
        Method methodEnum = Method.from(method);
        Action actionEnum = Action.from(action);
        Extension extensionEnum = Extension.from(extension);
        TransactionStatus statusEnum = TransactionStatus.from(status);
        TransactionType typeEnum = TransactionType.from(type);
        return transactionRepository.findByFilter(pageable, api, triggerTime, statusEnum, message, data, typeEnum, host, methodEnum, createdBy, fullName, startDateConverted, endDateConverted, actionEnum, extensionEnum);
    }

    /**
     * Delete the transaction by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete( Long id) {
        log.debug("Request to delete Transaction : {}", id);
        transactionRepository.deleteById(id);
    }

    /**
     * get all transaction between startDate and endDate
     */
    @Override
    public List<TransactionDTO> findTransactionType(String startDate, String endDate, String type) {
        List<Transaction> listTransaction  = transactionRepository.findAllTransactionTypeAndDate(convertToLocalDateTime(startDate), convertToLocalDateTime(endDate), type);
        return transactionMapper.toDto(listTransaction);
    }
}
