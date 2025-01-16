package ru.t1.java.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import ru.t1.java.demo.DataSourceErrorLog.DataSourceErrorLogRepository;
import ru.t1.java.demo.account.enums.AccountScoreType;
import ru.t1.java.demo.account.model.Account;
import ru.t1.java.demo.account.service.AccountService;
import ru.t1.java.demo.generator.DataGenerator;
import ru.t1.java.demo.transaction.model.Transaction;
import ru.t1.java.demo.transaction.repository.TransactionRepository;
import ru.t1.java.demo.transaction.service.TransactionService;


import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@EnableAspectJAutoProxy
class T1JavaDemoApplicationTests {

    @Autowired
    private AccountService accountService;

    @Autowired
    private DataSourceErrorLogRepository errorLogRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        errorLogRepository.deleteAll();
        transactionRepository.deleteAll();
    }

    @Test
    void testCreateAccount(){
        Account account = DataGenerator.generateAccount();
        account = accountService.createAccount(account);
        assertNotNull(account.getId());
    }

    @Test
    void testGetAccount(){
        assertThrows(IllegalArgumentException.class, () -> accountService.getAccountById(UUID.randomUUID()));
    }

    @Test
    void testAccountNullBalance() {
        Account account = new Account(AccountScoreType.DEBIT, null);
        assertThrows(IllegalArgumentException.class, () -> accountService.createAccount(account));
    }

    @Test
    void updateAccount(){
        Account oldAccount = accountService.getAccountById(UUID.fromString("f25d34d0-01be-4462-b59d-aa5f68c8975b"));
        Account newAccount = new Account(AccountScoreType.CREDIT, BigDecimal.ZERO);
        assertThrows(IllegalArgumentException.class, () -> accountService.updateAccountById(oldAccount.getId(), newAccount));
    }

    @Test
    void testCreateTransaction(){
        Transaction transaction = DataGenerator.generateTransaction();
        transaction = transactionService.createTransaction(transaction);
        assertNotNull(transaction.getId());
    }

    @Test
    void testGetTransaction(){
        assertThrows(IllegalArgumentException.class, () -> transactionService.getTransactionById(UUID.randomUUID()));
    }

    @Test
    void testTransactionNullAmount() {
        Transaction transaction = new Transaction(BigDecimal.ZERO);
        assertThrows(IllegalArgumentException.class, () -> transactionService.createTransaction(transaction));
    }

}
