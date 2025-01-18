package ru.t1.java.demo.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.ClientDto;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.util.ClientMapper;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ClientService {
    private final ClientRepository repository;
    private final Map<Long, Client> cache;

    public ClientService(ClientRepository repository) {
        this.repository = repository;
        this.cache = new HashMap<>();
    }

    @PostConstruct
    void init() {
        getClientById(1L);
    }

    public ClientDto getClientById(Long id) {
        log.debug("Call method getClient with id {}", id);
        ClientDto clientDto = null;

        if (cache.containsKey(id)) {
            return ClientMapper.toDto(cache.get(id));
        }

        try {
            Client entity = repository.findById(id).get();
            clientDto = ClientMapper.toDto(entity);
            cache.put(id, entity);
        } catch (Exception e) {
            log.error("Error: ", e);
//            throw new ClientException();
        }

//        log.debug("Client info: {}", clientDto.toString());
        return clientDto;
    }

}
