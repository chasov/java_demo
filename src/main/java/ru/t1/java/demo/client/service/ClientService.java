package ru.t1.java.demo.client.service;

import ru.t1.java.demo.client.model.Client;

import java.io.IOException;
import java.util.List;

public interface ClientService {
    List<Client> parseJson() throws IOException;
}
