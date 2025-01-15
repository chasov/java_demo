package ru.t1.java.demo.client.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.t1.java.demo.client.model.Client;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    @Override
    Optional<Client> findById(Long aLong);
}