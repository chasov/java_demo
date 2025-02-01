package ru.t1.java.demo.generator;

import lombok.extern.slf4j.Slf4j;
import ru.t1.java.demo.account.enums.AccountScoreType;
import ru.t1.java.demo.account.enums.AccountStatus;
import ru.t1.java.demo.account.model.Account;
import ru.t1.java.demo.transaction.enums.TransactionStatus;
import ru.t1.java.demo.transaction.model.Transaction;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

@Slf4j
public class DataGenerator {

    private static final Random rand = new Random();

    public static Account generateAccount() {
        log.info("Start generating Account");
        Account account = new Account((rand.nextBoolean() ? AccountScoreType.DEBIT : AccountScoreType.CREDIT), BigDecimal.valueOf(rand.nextDouble() * 10000), AccountStatus.OPEN, BigDecimal.ZERO);
        log.info("Successfully generated Transaction with accountScoreType {}, balance {}", account.getAccountScoreType(), account.getBalance());
        return account;
    }

    public static Transaction generateTransaction() {
        log.info("Start generating Transaction");
        Transaction transaction = new Transaction(BigDecimal.valueOf(rand.nextDouble() * 1000), TransactionStatus.ACCEPTED, UUID.randomUUID());
        log.info("Successfully generated Transaction with amount {}, time {}", transaction.getAmount(), transaction.getTransactionTime());
        return transaction;
    }
}
