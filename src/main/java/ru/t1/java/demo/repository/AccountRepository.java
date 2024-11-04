package ru.t1.java.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.t1.java.demo.model.Account;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    public List<Account> findAllAccountsByClientId(Long id);
    @Override
    public Account save (Account account);
}
