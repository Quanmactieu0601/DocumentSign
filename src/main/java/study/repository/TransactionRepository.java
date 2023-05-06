package study.repository;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.domain.Transaction;
import study.enums.TransactionType;

public interface TransactionRepository extends JpaRepository<Transaction, Long>, TransactionRepositoryCustom {
    @Query(
        value = "SELECT SUM(CASE when t.status = study.enums.TransactionStatus.SUCCESS THEN 1 ELSE 0 END)  AS TotalSuccess, " +
        "                  SUM(CASE WHEN t.status = study.enums.TransactionStatus.FAIL THEN 1 ELSE 0 END) AS TotalFail " +
        "FROM Transaction t " +
        "WHERE (t.triggerTime >= :startDate or :startDate is null) " +
        "AND (t.triggerTime <= :endDate or :endDate is null) " +
        "AND (t.type = :type or :type is null) "
    )
    Map<String, BigInteger> findAllTransactionTypeAndDate(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("type") TransactionType type
    );
}
