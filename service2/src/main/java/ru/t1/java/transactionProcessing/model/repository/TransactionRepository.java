package ru.t1.java.transactionProcessing.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.t1.java.transactionProcessing.model.entity.TransactionEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {

    int countByAccountIdAndTimestampBetween(UUID accountId, LocalDateTime start, LocalDateTime end);

}
