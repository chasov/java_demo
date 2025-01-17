package ru.t1.java.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.model.Transaction;

@LogDataSourceError
public interface TransactionRepository extends JpaRepository<Transaction,Long> {
}
