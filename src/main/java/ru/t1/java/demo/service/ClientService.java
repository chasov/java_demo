package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.dto.ClientDto;

import java.util.List;

public interface ClientService {
    List<Client> registerClients(List<Client> clients);

    Client registerClient(Client client);

    List<ClientDto> parseJson();

    void clearMiddleName(List<ClientDto> dtos);

    Client patchByClientId(String clientId, ClientDto dto);

    Client findByClientId(String clientId);

    void deleteByClientId(String clientId);

}
