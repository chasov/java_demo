package ru.t1.java.demo.service;

import ru.t1.java.demo.dto.ClientDto;
import ru.t1.java.demo.model.Client;

import java.util.List;


import java.util.Optional;

public interface ClientService {
    Client convertToEntity(ClientDto clientDto);

    ClientDto convertToDto(Client client);

    List<Client> getAllClients();

    Optional<Client> getClientById(Long id);

    Client createClient(Client client);

    Client updateClient(Client client);

    void deleteClient(Long id);
}