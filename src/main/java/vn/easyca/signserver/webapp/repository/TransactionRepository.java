package vn.easyca.signserver.webapp.repository;

import org.springframework.data.repository.query.Param;
import vn.easyca.signserver.webapp.domain.Transaction;
import org.springframework.data.jpa.repository.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Spring Data  repository for the Transaction entity.
 */
@SuppressWarnings({"unused", "JpaQlInspection"})
public interface TransactionRepository extends JpaRepository<Transaction, Long> , TransactionRepositoryCustom {
    @Query(value = " FROM Transaction t WHERE t.triggerTime BETWEEN :startDate  AND :endDate  AND t.type = :type")
    List<Transaction> findAllTransactionTypeAndDate(@Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate, @Param("type") String type);
}
