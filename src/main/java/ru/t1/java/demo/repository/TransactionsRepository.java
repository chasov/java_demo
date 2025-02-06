package ru.t1.java.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.t1.java.demo.model.entity.Transaction;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionsRepository extends JpaRepository<Transaction, Long> {

    @Query("FROM Transaction t WHERE t.transactionId = :transactionId")
    Optional<Transaction> findByTransactionId(@Param("transactionId") UUID transactionId);
}
