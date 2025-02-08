package ru.t1.java.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountId(UUID accountId);

    void deleteByAccountId(UUID accountId);
}
