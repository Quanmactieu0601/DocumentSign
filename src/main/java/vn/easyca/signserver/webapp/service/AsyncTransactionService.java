package vn.easyca.signserver.webapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import vn.easyca.signserver.webapp.enm.Action;
import vn.easyca.signserver.webapp.enm.Method;
import vn.easyca.signserver.webapp.enm.Status;
import vn.easyca.signserver.webapp.enm.TransactionType;
import vn.easyca.signserver.webapp.service.dto.TransactionDTO;

import java.time.LocalDateTime;

@Component
public class AsyncTransactionService {
    @Autowired
    TransactionService transactionService;

    @Async
    public void newThread(String api, TransactionType type, Method method, Status status, String message, String createdBy) {
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setApi(api);
        transactionDTO.setType(String.valueOf(type));
        transactionDTO.setMethod(String.valueOf(method));
        transactionDTO.setStatus(status.isSucess());
        transactionDTO.setMessage(message);
        transactionDTO.setCreatedBy(createdBy);
        transactionDTO.setTriggerTime(LocalDateTime.now());
//        transactionDTO.setAction(String.valueOf(action));
        transactionService.save(transactionDTO);
    }
}
