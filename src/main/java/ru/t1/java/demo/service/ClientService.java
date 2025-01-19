package ru.t1.java.demo.service;

import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.dto.ClientDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;

import java.io.IOException;
import java.util.List;

public interface ClientService {
    List<Client> parseJson() throws IOException;
}
