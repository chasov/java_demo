package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.AccountStatus;
import ru.t1.java.demo.model.Transaction;

import java.io.IOException;
import java.util.List;

public interface AccountService {
    Account createAccount(Account account, Long clientId);
    Account updateAccount(Long accountId, Account updatedAccount) ;
    void changeAccountStatus(Long accountId, AccountStatus status);
    List<Transaction> findAllAccountTransactions(Long accountId);

    List<Account> parseJson() throws IOException;

    // Сделано для тестирования producer и consumer Kafka
    void sendAccountToKafka() ;
}
