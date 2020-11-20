package vn.easyca.signserver.webapp.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import vn.easyca.signserver.webapp.service.TransactionService;
import vn.easyca.signserver.webapp.service.dto.TransactionDTO;

@Component
@Async
public class AsyncTransaction {
    @Autowired
    TransactionService transactionService;
    public void newThread(TransactionDTO transactionDTO)  {
//        Thread.sleep(30000);
        transactionService.save(transactionDTO);

    }
}
