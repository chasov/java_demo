package ru.t1.java.demo.service;

import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;

import java.io.IOException;
import java.util.List;

public interface ClientService {
    List<Client> parseJson() throws IOException;

    void registerClients(List<Client> clients);

    Client findById(Long clientId);

    List<Client> findAll();

    Client save(Client entity);

    @LogDataSourceError
    void delete(Long clientId) throws ClientException;

    @LogDataSourceError
    @Transactional(readOnly = true)
    List<Account> findAccountsByClientId(Long clientId) throws ClientException;

    @LogDataSourceError
    Client updateClient(Long clientId, Client clientDetails) throws ClientException;

    Client createClient(Client entity);

}
