package vn.easyca.signserver.webapp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.easyca.signserver.webapp.enm.*;
import vn.easyca.signserver.webapp.service.dto.TransactionDTO;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepositoryCustom {
     Page<TransactionDTO> findByFilter(Pageable pageable, String triggerTime, String api, TransactionStatus statusEnum, String message, String data, TransactionType typeEnum, String host, Method method, String createdBy, String fullName, LocalDateTime startDateConverted, LocalDateTime endDateConverted, Action actionEnum, Extension extensionEnum) throws ParseException;

     List<TransactionDTO> findAllTransaction(LocalDateTime startDate, LocalDateTime endDate, TransactionType type);
}
