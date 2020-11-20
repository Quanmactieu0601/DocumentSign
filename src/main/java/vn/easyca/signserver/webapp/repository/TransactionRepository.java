package vn.easyca.signserver.webapp.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.easyca.signserver.infrastructure.database.jpa.entity.UserEntity;
import org.springframework.data.repository.query.Param;
import vn.easyca.signserver.webapp.domain.Transaction;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import vn.easyca.signserver.webapp.repository.TransactionRepositoryCustom;

/**
 * Spring Data  repository for the Transaction entity.
 */
@SuppressWarnings({"unused", "JpaQlInspection"})
public interface TransactionRepository extends JpaRepository<Transaction, Long> , TransactionRepositoryCustom {
    @Query(value = " FROM Transaction t WHERE t.triggerTime BETWEEN :startDate  AND :endDate  AND t.type = :type")
    List<Transaction> findAllTransactionTypeAndDate(@Param("startDate") Instant startDate,
                                                    @Param("endDate") Instant endDate, @Param("type") String type);

}
