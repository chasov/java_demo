package ru.t1.java.demo.account.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.t1.java.demo.account.model.Account;

import java.util.UUID;

@Repository
public interface AccountRepository extends CrudRepository<Account, UUID> {
}
