package vn.easyca.signserver.webapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.easyca.signserver.webapp.domain.Transaction;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Spring Data  repository for the Transaction entity.
 */

@SuppressWarnings({"unused", "JpaQlInspection"})
public interface TransactionRepository extends JpaRepository<Transaction, Long>, TransactionRepositoryCustom {
    @Query(value = " FROM Transaction t WHERE t.triggerTime BETWEEN :startDate  AND :endDate  AND t.type = :type")
    List<Transaction> findAllTransaction(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate, @Param("type") String type);


    @Query(value = " SELECT  count(  CASE WHEN  t.code='200' THEN 1 end )  as TotalSuccess , count(  CASE WHEN  t.code<>'200' THEN 1 end ) as TotalFail FROM (  Select * from transaction t where t.trigger_time between :startDate and :endDate   and  t.type=:type)  as t ", nativeQuery = true)
    Map<String, BigInteger> findAllTransactionTypeAndDate(@Param("startDate") LocalDateTime startDate,
                                                          @Param("endDate") LocalDateTime endDate, @Param("type") String type);

}
