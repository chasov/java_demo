package ru.t1.java.demo.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.t1.java.demo.DataSourceErrorLog.model.LogDataSourceError;
import ru.t1.java.demo.metric.model.Metric;
import ru.t1.java.demo.transaction.model.Transaction;
import ru.t1.java.demo.transaction.service.TransactionService;

import java.util.UUID;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @LogDataSourceError
    @Metric
    @GetMapping("")
    private Transaction createAccount(Transaction transaction) {
        return transactionService.createTransaction(transaction);
    }

    @LogDataSourceError
    @Metric
    @GetMapping("/id")
    private Transaction getTransactionById(UUID id){
        return transactionService.getTransactionById(id);
    }

    @LogDataSourceError
    @DeleteMapping("/id")
    @Metric
    private void deleteTransactionById(UUID id){
        transactionService.deleteTransactionById(id);
    }
}
