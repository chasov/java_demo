package ru.t1.java.demo.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.dto.ClientDto;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.util.ClientMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class LegacyClientService {
    private final ClientRepository repository;
    private final Map<Long, Client> cache;

    public LegacyClientService(ClientRepository repository) {
        this.repository = repository;
        this.cache = new HashMap<>();
    }

    @PostConstruct
    void init() {
        getClient(3L);
    }

    public ClientDto getClient(Long id) {
        log.info("Call method getClient with id {}", id);
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
            throw new ClientException();
        }

        log.info("Client info: {}", clientDto.toString());
        return clientDto;
    }

    @LogDataSourceError
    @Transactional
    public void saveClient(Client client) {
        repository.save(client);
    }

    @LogDataSourceError
    @Transactional
    public void deleteClientById(Long clientId) {
        if (clientId == null) {
            throw new IllegalArgumentException("Client ID cannot be null");
        }
        repository.deleteById(clientId);
    }

    public Optional<Client> findClientById(Long clientId) {
        return repository.findById(clientId);
    }





}
