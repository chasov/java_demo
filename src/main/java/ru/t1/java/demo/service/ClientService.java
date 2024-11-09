package ru.t1.java.demo.service;

import ru.t1.java.demo.dto.ClientDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ClientService {

    Optional<Client> findById(Long id);
    List<Client> registerClients(List<Client> clients);

    Client registerClient(Client client);

    List<ClientDto> parseJson();

    void clearMiddleName(List<ClientDto> dtos);
}
