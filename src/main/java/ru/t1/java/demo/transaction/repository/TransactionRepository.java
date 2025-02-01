package ru.t1.java.demo.transaction.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.t1.java.demo.transaction.model.Transaction;

import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, UUID> {

    Set<Transaction> findAllByAccountUuidAndTransactionTimeBetween(UUID accountId, Timestamp from, Timestamp to);
}
