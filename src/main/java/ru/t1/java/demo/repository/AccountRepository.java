package ru.t1.java.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.t1.java.demo.model.Account;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository  extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountId(UUID accountId);
}
