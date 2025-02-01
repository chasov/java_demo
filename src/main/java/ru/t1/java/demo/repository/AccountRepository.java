package ru.t1.java.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.t1.java.demo.model.Account;

import java.util.List;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, String> {

    List<Account> findAllByClientId(UUID clientId);
    Account findByAccountId(UUID accountId);
    void deleteByAccountId(UUID accountId);

}