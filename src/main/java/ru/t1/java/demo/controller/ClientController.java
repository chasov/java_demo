package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.dto.ClientDto;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.service.impl.AccountService;
import ru.t1.java.demo.service.impl.ClientService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/client")
public class ClientController {

    private final ClientService clientService;
    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<ClientDto> createClient(@RequestBody ClientDto clientDto) {
        ClientDto savedClient = clientService.createClient(clientDto);
        return new ResponseEntity<>(savedClient, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ClientDto>> getAllClients() {
        return new ResponseEntity<>(clientService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDto> getClientById(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(clientService.findById(id), HttpStatus.OK);
        } catch (ClientException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientDto> updateClient(@PathVariable Long id, @RequestBody ClientDto clientDetails) {
        try {
            ClientDto updatedClient = clientService.updateClient(id, clientDetails);
            return new ResponseEntity<>(updatedClient, HttpStatus.OK);
        } catch (ClientException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        try {
            clientService.delete(id);
            return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        } catch (ClientException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/accounts/{id}")
    public ResponseEntity<List<AccountDto>> getAllAccountsByClientId(@PathVariable Long id) {
        return new ResponseEntity<>(accountService.findAllAccountsById(id), HttpStatus.OK);
    }
}
