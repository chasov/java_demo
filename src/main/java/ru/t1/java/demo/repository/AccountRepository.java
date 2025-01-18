package ru.t1.java.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.t1.java.demo.model.entity.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
}
