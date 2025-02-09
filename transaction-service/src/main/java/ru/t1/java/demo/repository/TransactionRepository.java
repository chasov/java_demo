package ru.t1.java.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.t1.java.demo.aop.annotations.LogDataSourceError;
import ru.t1.java.demo.model.Transaction;

import java.util.List;

@LogDataSourceError
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query(value = "SELECT * FROM transactions t WHERE t.account_id = :accountId ORDER BY t.transaction_time DESC LIMIT :limit", nativeQuery = true)
    List<Transaction> findTopNTransactionsByAccountId(@Param("accountId") long accountId, @Param("limit") int limit);
}
