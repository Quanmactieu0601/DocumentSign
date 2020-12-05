package vn.easyca.signserver.webapp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.easyca.signserver.webapp.service.dto.TransactionDTO;

import java.text.ParseException;

public interface TransactionRepositoryCustom {

     Page<TransactionDTO> findByFilter(Pageable pageable, String triggerTime, String api, String status, String message, String data, String type, String host, String method, String createdBy, String fullName, String startDate, String endDate, String action, String extension) throws ParseException;
}
