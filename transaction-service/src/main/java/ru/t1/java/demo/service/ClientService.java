package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Client;

import java.util.List;


public interface ClientService {
    List<Client> getAllClients();

    Client getClientById(Long id);

    Client createClient(Client client);

    Client updateClient(Long id, Client client);

    void createClients(List<Client> clients);

    void deleteClient(Long id);
}