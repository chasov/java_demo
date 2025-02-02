package ru.t1.java.demo.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.annotation.LogDataSourceError;
import ru.t1.java.demo.dto.ClientDto;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.util.ClientMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@Slf4j
public class LegacyClientService {
    private final ClientRepository repository;
    private final Map<Long, Client> cache;

    public LegacyClientService(ClientRepository repository) {
        this.repository = repository;
        this.cache = new HashMap<>();
    }

//    @PostConstruct
//    void init() {
//        getClient(1L);
//    }

    public ClientDto getClient(Long id) {
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
            throw new ClientException();
        }

        log.debug("Client info: {}", clientDto.toString());
        return clientDto;
    }
    @Transactional
    @LogDataSourceError
    public ClientDto saveClient(ClientDto clientDto) {
        log.debug("Call method saveClient with id {}", clientDto.getId());
        return ClientMapper.toDto(repository.save(ClientMapper.toEntity(clientDto)));
    }
    @LogDataSourceError
    @Transactional
    public void deleteClient(Long id) {
        log.debug("Call method deleteClient with id {}", id);
        if (id == null){
            throw new ClientException("Client id is null");
        }
        repository.deleteById(id);
    }
    @Transactional
    @LogDataSourceError
    public ClientDto updateClient(Long clientId, ClientDto clientDto) {
        log.debug("Call method updateClient with id {}", clientId);
        if(repository.findById(clientId).isEmpty()){
            throw new NoSuchElementException(String.format("Client with id = %d not exist", clientId));
        }
        clientDto.setId(clientId);
        Client saved = repository.save(ClientMapper.toEntity(clientDto));
        return ClientMapper.toDto(saved);
    }

}
