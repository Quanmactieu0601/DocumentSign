package vn.easyca.signserver.webapp.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import vn.easyca.signserver.webapp.enm.Method;
import vn.easyca.signserver.webapp.enm.TransactionType;
import vn.easyca.signserver.webapp.service.TransactionService;
import vn.easyca.signserver.webapp.service.dto.TransactionDTO;

import java.time.Instant;

@Component
public class AsyncTransaction {
    @Autowired
    TransactionService transactionService;

    @Async
    public void newThread(String api, TransactionType type, Method method, String code, String message, String createdBy) {
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setApi(api);
        transactionDTO.setType(String.valueOf(type));
        transactionDTO.setMethod(String.valueOf(method));
        transactionDTO.setCode(code);
        transactionDTO.setMessage(message);
        transactionDTO.setCreatedBy(createdBy);
        transactionDTO.setTriggerTime(Instant.now());
        transactionService.save(transactionDTO);
    }
}
