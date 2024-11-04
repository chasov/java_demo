package ru.t1.java.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.t1.java.demo.model.Client;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {
    @Override
    public List<Client> findAll();
    @Override
    public Client save(Client client);
}