package vn.easyca.signserver.webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.easyca.signserver.webapp.domain.Transaction;
import vn.easyca.signserver.webapp.enm.TransactionType;
import vn.easyca.signserver.webapp.service.dto.TransactionDTO;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Spring Data  repository for the Transaction entity.
 */

@SuppressWarnings({"unused", "JpaQlInspection"})
public interface TransactionRepository extends JpaRepository<Transaction, Long>, TransactionRepositoryCustom {
    @Query(value = "SELECT COUNT(CASE WHEN t.status = true THEN 1 END)  AS TotalSuccess, " +
        "                  COUNT(CASE WHEN t.status = false THEN 1 END) AS TotalFail " +
        "FROM transaction_log t " +
        "WHERE (t.trigger_time >= :startDate or :startDate is null) " +
        "AND (t.trigger_time <= :endDate or :endDate is null) " +
        "AND (t.type = :type or :type is null); ", nativeQuery = true)
    Map<String, BigInteger> findAllTransactionTypeAndDate(@Param("startDate") LocalDateTime startDate,
                                                          @Param("endDate") LocalDateTime endDate, @Param("type") String type);

}
