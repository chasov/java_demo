package ru.t1.java.demo.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transactional;

import java.util.UUID;

public interface TransactionalRepository extends JpaRepository<Transactional, UUID> {
    Page<Transactional> findByAccount(Account account, Pageable pageable);
}
