package ru.t1.java.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.t1.java.demo.model.Account;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, String> {

    List<Account> findAllByClientId(String clientId);

  //  Account findByAccountId(String accountId);

}