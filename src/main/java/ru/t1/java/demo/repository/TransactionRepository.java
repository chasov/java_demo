package ru.t1.java.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.t1.java.demo.model.transaction.Transaction;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Override
    Optional<Transaction> findById(Long id);
    Optional<Transaction> findByTransactionId(Integer transactionId);
}
