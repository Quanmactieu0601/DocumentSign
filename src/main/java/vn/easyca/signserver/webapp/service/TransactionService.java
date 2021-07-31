package vn.easyca.signserver.webapp.service;

import vn.easyca.signserver.webapp.enm.Method;
import vn.easyca.signserver.webapp.enm.TransactionType;
import vn.easyca.signserver.webapp.service.dto.TransactionDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.easyca.signserver.webapp.service.dto.TransactionDTO;

import java.math.BigInteger;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service Interface for managing {@link vn.easyca.signserver.webapp.domain.Transaction}.
 */
public interface TransactionService {

    /**
     * Save a transaction.
     *
     * @param transactionDTO the entity to save.
     * @return the persisted entity.
     */

    TransactionDTO save(TransactionDTO transactionDTO);

    /**
     * Get all the transactions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TransactionDTO> findAll(Pageable pageable);

    /**
     * Get the "id" transaction.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TransactionDTO> findOne(Long id);

    void delete(Long id);

     Page<TransactionDTO> getByFilter(Pageable pageable, String api, String triggerTime, String status,
                                      String message, String data, String type, String createdBy, String host, String method, String fullName, String startDate, String endDate, String action, String extension) throws ParseException;


    /**
     * get total request transaction success and fail .
     *
     * @param startDate , enddate ,ttype
     */
    List<TransactionDTO> findTransaction(String startDate, String endDate, String type);

    Map<String, BigInteger> findTransactionType(String startDate, String endDate, TransactionType type);
}
