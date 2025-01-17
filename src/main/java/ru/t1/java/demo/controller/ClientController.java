package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.dto.ClientDto;
import ru.t1.java.demo.service.ClientService;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/clients")
@Slf4j
public class ClientController {

    private final ClientService clientService;

    @GetMapping("/{id}")
    public ResponseEntity<ClientDto> getClient(@PathVariable("id") Long clientId) {
        return ResponseEntity.ok(
                clientService.getById(clientId)
        );
    }

    @GetMapping
    public ResponseEntity<Collection<ClientDto>> getAllClients() {
        return ResponseEntity.ok(
                clientService.getAll()
        );
    }

    @PostMapping
    public ResponseEntity<ClientDto> createClient(@RequestBody ClientDto clientDto) {
        ClientDto newClient = clientService.create(clientDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(newClient);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientDto> updateClient(@PathVariable("id") Long id, @RequestBody
                                                    ClientDto updatedClientDto){
        ClientDto clientDto = clientService.update(id, updatedClientDto);
        return ResponseEntity.ok(clientDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClient(@PathVariable("id") Long clientId) {
        clientService.delete(clientId);
        return ResponseEntity.ok("Client with id " + clientId + " deleted successfully");
    }
}
