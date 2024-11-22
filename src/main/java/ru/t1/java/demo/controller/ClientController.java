package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.aop.HandlingResult;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.aop.Track;
import ru.t1.java.demo.aop.LogException;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.dto.ClientDto;
import ru.t1.java.demo.service.ClientService;
import ru.t1.java.demo.util.ClientMapper;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/client")
public class ClientController {

    private final ClientService clientService;

    /*//@LogException
    @LogDataSourceError
    //@Track
    @GetMapping(value = "/client")
    //@HandlingResult
    public void doSomething() throws IOException, InterruptedException {
        try {
            clientService.parseJson();
            Thread.sleep(1000L);
        } catch (ClientException e) {
            log.error(e.getMessage(), e);
        }
    }*/

    @PostMapping
    public ResponseEntity<ClientDto> createClient(@RequestBody ClientDto client) {
        Client savedClient = clientService.createClient(ClientMapper.toEntity(client));
        return new ResponseEntity<>(ClientMapper.toDto(savedClient),
                                    HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ClientDto>> getAllClients() {
        return new ResponseEntity<>(clientService.findAll().stream().map(ClientMapper::toDto).toList(),
                                    HttpStatus.OK);
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<ClientDto> getClientById(@PathVariable Long clientId) {
        try {
            return new ResponseEntity<>(ClientMapper.toDto(clientService.findById(clientId)),
                                        HttpStatus.OK);
            
        } catch (ClientException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{clientId}")
    public ResponseEntity<ClientDto> updateClient(@PathVariable Long clientId, @RequestBody ClientDto clientDto) {
        try {
            Client updatedClient = clientService.updateClient(clientId, ClientMapper.toEntity(clientDto));
            return new ResponseEntity<>(ClientMapper.toDto(updatedClient),
                                        HttpStatus.OK);
        } catch (ClientException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
    }

    @DeleteMapping("/{clientId}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long clientId) {
        try {
            clientService.delete(clientId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ClientException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}