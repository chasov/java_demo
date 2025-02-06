package ru.t1.java.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {}

