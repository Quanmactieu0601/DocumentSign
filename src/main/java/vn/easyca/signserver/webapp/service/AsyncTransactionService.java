package vn.easyca.signserver.webapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import vn.easyca.signserver.webapp.enm.*;
import vn.easyca.signserver.webapp.service.dto.TransactionDTO;

import java.time.LocalDateTime;

@Component
public class AsyncTransactionService {
    @Autowired
    TransactionService transactionService;

    @Async
    public void newThread(String api, TransactionType type, Action action, Extension extension, Method method, TransactionStatus status, String message, String createdBy) {
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setApi(api);
        transactionDTO.setType(type);
        transactionDTO.setMethod(method);
        transactionDTO.setStatus(status);
        transactionDTO.setMessage(message);
        transactionDTO.setCreatedBy(createdBy);
        transactionDTO.setTriggerTime(LocalDateTime.now());
        transactionDTO.setAction(action);
        transactionDTO.setExtension(extension);
        transactionService.save(transactionDTO);
    }
}
