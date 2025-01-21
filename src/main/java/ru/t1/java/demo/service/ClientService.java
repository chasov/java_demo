package ru.t1.java.demo.service;

import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.model.Client;

import java.io.IOException;
import java.util.List;

@LogDataSourceError
public interface ClientService {
    List<Client> parseJson() throws IOException;
}
