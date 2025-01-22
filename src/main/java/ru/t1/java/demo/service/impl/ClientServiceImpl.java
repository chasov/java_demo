package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.annotation.LogDataSourceError;
import ru.t1.java.demo.aop.annotation.Metric;
import ru.t1.java.demo.exception.client.ClientException;
import ru.t1.java.demo.kafka.producer.KafkaClientProducer;
import ru.t1.java.demo.model.dto.CheckResponse;
import ru.t1.java.demo.model.dto.ClientDto;
import ru.t1.java.demo.model.entity.Client;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.service.client.ClientService;
import ru.t1.java.demo.util.ClientMapper;
import ru.t1.java.demo.web.CheckWebClient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository repository;
    private final KafkaClientProducer<ClientDto> kafkaClientProducer;
    private final CheckWebClient checkWebClient;

    @PostConstruct
    @LogDataSourceError
    @Metric
    void init() {
        List<Client> clients = null;
        try {
            clients = parseJson();
        } catch (IOException e) {
            log.error("Ошибка во время обработки записей", e);
        }
        repository.saveAll(clients);
    }

    @Override
    @LogDataSourceError
    @Metric
    public List<Client> parseJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        ClientDto[] clients = mapper.readValue(new File("src/main/resources/MOCK_DATA.json"), ClientDto[].class);

        return Arrays.stream(clients)
                .map(ClientMapper::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    @LogDataSourceError
    @Metric
    public List<Client> registerClients(List<Client> clients) {
        List<Client> savedClients = new ArrayList<>();
        for (Client client : clients) {
            Optional<CheckResponse> check = checkWebClient.check(client.getId());
            check.ifPresent(checkResponse -> {
                if (!checkResponse.getBlocked()) {
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
    @LogDataSourceError
    @Metric
    public Client registerClient(Client client) {
        Client saved = null;
        Optional<CheckResponse> check = checkWebClient.check(client.getId());
        if (check.isPresent()) {
            if (!check.get().getBlocked()) {
                saved = repository.save(client);
                kafkaClientProducer.send(client.getId());
            }
        }
        return saved;
    }

    @Override
    @LogDataSourceError
    @Metric
    public ClientDto registerClient(ClientDto clientDto) {
        Client saved = ClientMapper.toEntity(clientDto);
        saved = repository.save(saved);
        kafkaClientProducer.send(saved.getId());
        return ClientMapper.toDto(saved);
    }

    @Override
    @LogDataSourceError
    @Metric
    public void clearMiddleName(List<ClientDto> dtos) {
        log.info("Clearing middle name");
        dtos.forEach(dto -> dto.setMiddleName(null));
        log.info("Done clearing middle name");
    }

    @Override
    @LogDataSourceError
    @Metric
    public ClientDto getClient(Long clientId) {
        Client client = repository.findById(clientId)
                .orElseThrow(() -> new ClientException("Client not found"));
        return ClientMapper.toDto(client);
    }

    @Override
    @LogDataSourceError
    @Metric
    public void deleteClient(Long clientId) {
        repository.deleteById(clientId);
    }
}
