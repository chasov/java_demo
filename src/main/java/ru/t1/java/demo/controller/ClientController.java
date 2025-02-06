package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.dto.ClientDto;
import ru.t1.java.demo.kafka.producer.KafkaClientProducer;
import ru.t1.java.demo.service.ClientService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/clients")
@Slf4j
public class ClientController {

    private final ClientService clientService;

    private final KafkaClientProducer clientProducer;

    @GetMapping("/{id}")
    public ClientDto getClient(@PathVariable("id") Long clientId) {
        return clientService.getById(clientId);
    }

    @GetMapping
    public Collection<ClientDto> getAllClients() {
        return clientService.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientDto createClient(@RequestBody ClientDto clientDto) {
        return clientService.create(clientDto);
    }

    /**
     * Method  and endpoint to create Client with sending message to Kafka
     *
     * @param @RequestBody clientDto
     * @return ResponseEntity clientDto
     */
    @PostMapping("/send")
    @ResponseStatus(HttpStatus.OK)
    public ClientDto sendCreateClientRequest(@RequestBody ClientDto clientDto) {
        clientProducer.send(clientDto);
        return clientDto;
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ClientDto updateClient(@PathVariable("id") Long id
            , @RequestBody ClientDto updatedClientDto) {
        return clientService.update(id, updatedClientDto);
    }
}
