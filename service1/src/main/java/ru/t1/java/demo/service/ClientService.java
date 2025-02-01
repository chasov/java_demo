package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Client;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ClientService {
    //List<Client> parseJson() throws IOException;

    public Optional<Client> findById(Long id);
}
