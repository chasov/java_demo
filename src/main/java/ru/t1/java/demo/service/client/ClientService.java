package ru.t1.java.demo.service.client;

import ru.t1.java.demo.model.dto.client.ClientDto;
import ru.t1.java.demo.model.entity.Client;

import java.io.IOException;
import java.util.List;

public interface ClientService {
    List<Client> parseJson() throws IOException;

    List<Client> registerClients(List<Client> clients);

    ClientDto registerClient(Client client);

    ClientDto registerClient(ClientDto clientDto);

    void clearMiddleName(List<ClientDto> dtos);

    ClientDto getClient(Long clientId);

    void deleteClient(Long clientId);
}
