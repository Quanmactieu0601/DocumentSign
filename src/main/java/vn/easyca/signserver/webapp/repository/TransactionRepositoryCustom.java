package vn.easyca.signserver.webapp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.easyca.signserver.webapp.domain.Transaction;

import java.time.Instant;

public interface TransactionRepositoryCustom {
     Page<Transaction> findByFilter(Pageable pageable, String triggerTime, String api, String code, String message, String data, String type ,Long userID, String host, String method);
}
