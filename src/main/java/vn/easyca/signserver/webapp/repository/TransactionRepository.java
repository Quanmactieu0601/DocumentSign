package vn.easyca.signserver.webapp.repository;

import org.springframework.data.repository.query.Param;
import vn.easyca.signserver.webapp.domain.Transaction;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * Spring Data  repository for the Transaction entity.
 */


@SuppressWarnings({"unused", "JpaQlInspection"})
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {


    @Query(value = " FROM Transaction t WHERE t.triggerTime BETWEEN :startDate  AND :endDate  AND t.type = :type")
    List<Transaction> findAllTransactionTypeAndDate(@Param("startDate") Instant startDate,
                                                    @Param("endDate") Instant endDate, @Param("type") String type);
}
