package ru.t1.java.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;

import java.util.List;
import java.util.UUID;


public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findAllByAccountId(UUID accountId);
    Transaction findByTransactionId(UUID transactionId);
    void deleteByTransactionId(UUID transactionId);

}