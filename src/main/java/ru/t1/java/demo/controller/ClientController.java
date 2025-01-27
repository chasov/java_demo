package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.aop.HandlingResult;
import ru.t1.java.demo.aop.LogException;
import ru.t1.java.demo.aop.Metric;
import ru.t1.java.demo.aop.Track;
import ru.t1.java.demo.kafka.KafkaClientProducer;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.dto.ClientDto;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.service.ClientService;
import ru.t1.java.demo.util.ClientMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;


@RestController
@RequiredArgsConstructor
@Slf4j
public class ClientController {

    private final ClientService clientService;
    //    private final KafkaClientProducer kafkaClientProducer;
//    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    //   private final MetricService metricService;

//    @Value("${t1.kafka.topic.client_registration}")
//    private String topic;

    @LogException
    @Metric(maxExecutionTime = 1)
    @GetMapping(value = "/parse")
    public void parseSource() {
//        try {
        // ...
        throw new IllegalStateException();
//        } catch (IllegalStateException e) {
//            log.warn(e.getMessage());
//        }

//        clientRepository.save(Client.builder()
//                .firstName("John42")
//                .build());
//        clientRepository.findClientByFirstName("John42");
//        metricService.incrementByName(Metrics.CLIENT_CONTROLLER_REQUEST_COUNT.getValue());
    }


    @LogException
    @Metric(maxExecutionTime = 1)
    @GetMapping("/client")
    public ResponseEntity<ClientDto> getClient() {
        log.debug("Getting client with id ");
        return ResponseEntity.ok()
                .body(ClientDto.builder()
                        .firstName("John")
                        .build());
    }

    @LogException
    @Metric(maxExecutionTime = 1)
    @GetMapping("/admin")
//    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin Board.";
    }

    @LogException
    @Metric(maxExecutionTime = 1)
    @HandlingResult
    @PostMapping("/register")
    public ResponseEntity<Client> register(@RequestBody ClientDto clientDto) {
        log.info("Registering client: {}", clientDto);
        Client client = clientService.registerClient(clientMapper.toEntityWithId(clientDto));
        log.info("Client registered: {}", client.getId());
        //      metricService.incrementByName(Metrics.CLIENT_CONTROLLER_REQUEST_COUNT.getValue());
        return ResponseEntity.ok().body(client);
    }

    @LogException
    @Track
    @PatchMapping("client/{clientId}")
    @HandlingResult
    public ClientDto patchById(@PathVariable Long clientId,
                               @RequestBody ClientDto dto) {
        return clientService.patchById(clientId, dto);
    }

    @LogException
    @Track
    @GetMapping(value = "/client/{clientId}")
    @HandlingResult
    public ClientDto getById(@PathVariable Long clientId) {
        return clientService.getById(clientId);
    }

    @LogException
    @Track
    @DeleteMapping("client/{clientId}")
    @HandlingResult
    public void deleteById(@PathVariable Long clientId) {
        clientService.deleteById(clientId);
    }
}


