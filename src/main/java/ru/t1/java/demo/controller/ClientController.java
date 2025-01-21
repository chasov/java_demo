package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.dto.ClientDto;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.service.ClientService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    public ResponseEntity<List<ClientDto>> getAllClients() {
        List<ClientDto> clients = clientService.getAllClients()
                .stream()
                .map(clientService::toDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(clients, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ClientDto> createClient(@RequestBody ClientDto client) {
        Client newClient = clientService.toEntity(client);
        Client savedClient = clientService.createClient(newClient);

        return new ResponseEntity<>(clientService.toDto(savedClient), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientDto> updateClient(@PathVariable Long id, @RequestBody ClientDto client) {
        if (clientService.getClientById(id).isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Client clientToUpdate = clientService.toEntity(client);
        clientToUpdate.setId(id);
        Client updatedClient = clientService.updateClient(clientToUpdate);
        return new ResponseEntity<>(clientService.toDto(updatedClient), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClientById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
