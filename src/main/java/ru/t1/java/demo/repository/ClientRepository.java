package ru.t1.java.demo.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.aop.Transaction;
import ru.t1.java.demo.model.Client;

import java.util.Optional;

import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Override
    Optional<Client> findById(Long aLong);

    @Transactional(propagation = NOT_SUPPORTED)
    @Transaction
    Client findClientByFirstName(String firstName);
}