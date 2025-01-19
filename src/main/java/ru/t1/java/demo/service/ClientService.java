package ru.t1.java.demo.service;

import ru.t1.java.demo.dto.ClientDto;

public interface ClientService {

    ClientDto save(ClientDto dto);
    ClientDto patchById(Long clientId, ClientDto dto);
    ClientDto getById(Long clientId);
    void deleteById(Long clientId);

}
