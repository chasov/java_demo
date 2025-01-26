package ru.t1.java.demo.service.impl;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.kafka.KafkaClientProducer;
import ru.t1.java.demo.model.MetricErrorLog;
import ru.t1.java.demo.model.dto.ClientDto;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.service.ClientService;
import ru.t1.java.demo.util.ClientMapper;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static ru.t1.java.demo.enums.ErrorType.METRICS;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

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
