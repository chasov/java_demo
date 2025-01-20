package ru.t1.java.demo.repository;

import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.t1.java.demo.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Override
    @NonNull
    Page<Transaction> findAll(@NonNull Pageable pageable);
}
