package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Client;

import java.io.IOException;
import java.util.List;

public interface ClientService {
    public List<Client> registerClients(List<Client> clients);

    public Client registerClient(Client client)

    List<Client> parseJson() throws IOException;
}
