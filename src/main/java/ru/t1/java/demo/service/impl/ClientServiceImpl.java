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
import ru.t1.java.demo.kafka.KafkaClientProducer;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.service.ClientService;
import ru.t1.java.demo.util.ClientMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final KafkaClientProducer kafkaClientProducer;
    private final ClientRepository repository;
    private final ClientMapper clientMapper;

//    @PostConstruct
    void init() {
        try {
            List<Client> clients = parseJson();
            repository.saveAll(clients);
        } catch (IOException e) {
            log.error("Ошибка во время обработки записей", e);
        }
    }

    @Override
    public List<Client> registerClients(List<Client> clients) {
        List<Client> savedClients = new ArrayList<>();
        for (Client client : clients) {
            Optional<CheckResponse> check = checkWebClient.check(client.getClientId());
            check.ifPresent(checkResponse -> {
                if (!checkResponse.getBlocked()) {
//                    Client saved = repository.save(client);
                    kafkaClientProducer.send(client.getId());
                    savedClients.add(client);
                }
            });
            savedClients.add(repository.save(client));
        }

        return savedClients
                .stream()
                .sorted(Comparator.comparing(Client::getId))
                .toList();
    }

    @Override
    public Client registerClient(Client client) {
        Client saved = null;
        Optional<CheckResponse> check = checkWebClient.check(client.getId());
        if (check.isPresent()) {
            if (!check.get().getBlocked()) {
                saved = repository.save(client);
                kafkaClientProducer.send(client.getId());
            }
        }
        return client;
    }



    @Override
    @LogExecution
    @Track
    @HandlingResult
    public List<Client> parseJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        ClientDto[] clients = mapper.readValue(new File("src/main/resources/MOCK_DATA.json"), ClientDto[].class);

        return Arrays.stream(clients)
                .map(clientMapper::toEntity)
                .collect(Collectors.toList());
    }
}
