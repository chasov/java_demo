package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.kafka.KafkaClientProducer;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.dto.CheckResponse;
import ru.t1.java.demo.model.dto.ClientDto;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.service.ClientService;
import ru.t1.java.demo.util.ClientMapper;
import ru.t1.java.demo.web.CheckWebClient;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository repository;
    private final KafkaClientProducer kafkaClientProducer;
    private final CheckWebClient checkWebClient;

    @Override
    public List<Client> registerClients(List<Client> clients) {
        List<Client> savedClients = new ArrayList<>();
        for (Client client : clients) {
            Optional<CheckResponse> check = checkWebClient.check((client.getClientId()));
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
        Optional<CheckResponse> check = checkWebClient.check(client.getClientId());
        if (check.isPresent()) {
            if (!check.get().getBlocked()) {
                saved = repository.save(client);
                kafkaClientProducer.send(client.getId());
            }
        }
          client.setId(Long.valueOf(222));
        return client;
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

    private final KafkaClientProducer producer;
    private final ClientRepository clientRepository;

    @LogDataSourceError
    @Override
    public ClientDto save(ClientDto dto) {


        String topic = "t1_demo_client_registration";

        Message<ClientDto> message = MessageBuilder.withPayload(dto)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(KafkaHeaders.KEY, null)
                .setHeader("uuid", UUID.randomUUID().toString())
                .build();

        producer.sendMessage(message);

        return ClientMapper.toDto(clientRepository.save(ClientMapper.toEntity(dto)));
    }

    @LogDataSourceError
    @Override
    public ClientDto patchById(Long clientId, ClientDto dto) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientException("Client not found"));

        client.setFirstName(dto.getFirstName());
        client.setLastName(dto.getLastName());
        client.setMiddleName(dto.getMiddleName());

        return ClientMapper.toDto(clientRepository.save(client));
    }

    @LogDataSourceError
    @Override
    public ClientDto getById(Long clientId) {
        return ClientMapper.toDto(clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientException("Client not found")));
    }

    @LogDataSourceError
    @Override
    public void deleteById(Long clientId) {
        clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientException("Client not found"));
        clientRepository.deleteById(clientId);
    }

}
