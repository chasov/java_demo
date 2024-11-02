package ru.t1.java.demo.service;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.ClientDto;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.util.mapper.ClientMapper;

import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class LegacyClientService {
    private final ClientRepository repository;
    private final Map<Long, Client> cache;
    private final ClientMapper clientMapper;

    @PostConstruct
    void init() {
        getClient(1L);
    }

    public ClientDto getClient(Long id) {
        log.debug("Call method getClient with id {}", id);
        ClientDto clientDto = null;

        if (cache.containsKey(id)) {
            return clientMapper.toDto(cache.get(id));
        }

//        try {
//            Client entity = repository.findById(id).get();
//            clientDto = ClientMapper.toDto(entity);
//            cache.put(id, entity);
//        } catch (Exception e) {
//            log.error("Error: ", e);
////            throw new ClientException();
//        }

//        log.debug("Client info: {}", clientDto.toString());
        return clientDto;
    }

}
