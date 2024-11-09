package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.Track;
import ru.t1.java.demo.aop.HandlingResult;
import ru.t1.java.demo.aop.LogExecution;
import ru.t1.java.demo.dto.ClientDto;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.service.ClientService;
import ru.t1.java.demo.util.ClientMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientRepository repository;

//    @PostConstruct
//    void init() {
//        try {
//            List<Client> clients = parseJson();
////            repository.saveAll(clients);
//        } catch (IOException e) {
//            log.error("Ошибка во время обработки записей", e);
//        }
//
//    }


    @Override
    public Optional<Client> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<Client> registerClients(List<Client> clients) {
        List<Client> savedClients = new ArrayList<>();
        for (Client client : clients) {
//            Optional<CheckResponse> check = checkWebClient.check(client.getId());
//            check.ifPresent(checkResponse -> {
//                if (!checkResponse.getBlocked()) {
//                    Client saved = repository.save(client);
//                    kafkaClientProducer.send(saved.getId());
//                    savedClients.add(saved);
//                }
//            });
            savedClients.add(repository.save(client));
        }

        return savedClients;
    }

    @Override
    public Client registerClient(Client client) {
        Client saved = null;
//        Optional<CheckResponse> check = checkWebClient.check(client.getId());
//        if (check.isPresent()) {
//            if (!check.get().getBlocked()) {
//                saved = repository.save(client);
//                kafkaClientProducer.send(client.getId());
//            }
//        }
        saved = repository.save(client);

        return saved;
    }

    @Override
    public List<ClientDto> parseJson() {
        log.info("Parsing json");
        ObjectMapper mapper = new ObjectMapper();
        ClientDto[] clients = new ClientDto[0];
        try {
//            clients = mapper.readValue(new File("src/main/resources/MOCK_DATA.json"), ClientDto[].class);
            clients = new ClientDto[]{ClientDto.builder()
                    .firstName("first_name_1")
                    .build(),
                    ClientDto.builder()
                            .firstName("first_name_2")
                            .build()};
        } catch (Exception e) {
//            throw new RuntimeException(e);
            log.warn("Exception: ", e);
        }
        log.info("Found {} clients", clients.length);
        return Arrays.asList(clients);
    }

    @Override
    public void clearMiddleName(List<ClientDto> dtos) {
        log.info("Clearing middle name");
        dtos.forEach(dto -> dto.setMiddleName(null));
        log.info("Done clearing middle name");
    }

}
