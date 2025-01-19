package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.aop.annotation.HandlingResult;
import ru.t1.java.demo.aop.annotation.LogDataSourceError;
import ru.t1.java.demo.aop.annotation.Track;
import ru.t1.java.demo.aop.annotation.LogException;
import ru.t1.java.demo.dto.ClientDto;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.service.ClientService;
import ru.t1.java.demo.service.LegacyClientService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor()
@RequestMapping("/api/clients")
@Slf4j
public class ClientController {

    private final LegacyClientService legacyClientService;

    private final ClientService clientService;

    @LogDataSourceError
    @GetMapping("/parse")
    public void parseSource(){
        throw new IllegalStateException("Exception in parseSource");
    }

    @LogException
//    @Track
    @GetMapping(value = "/client")
//    @HandlingResult
    public void doSomething() throws IOException, InterruptedException {
//        try {
//            clientService.parseJson();
        throw new ClientException();
//        } catch (Exception e) {
//            log.info("Catching exception from ClientController");
//            throw new ClientException();
//        }
    }

    @PutMapping("/update")
    public ClientDto updateClient(@RequestBody Long clientId, @RequestBody ClientDto clientDto) {
        return legacyClientService.updateClient(clientId, clientDto);
    }

    @PostMapping("/create")
    public ClientDto saveClient(@RequestBody ClientDto clientDto) {
        return legacyClientService.saveClient(clientDto);
    }

    @GetMapping("/getById/{id}")
    public ClientDto getClient(@PathVariable Long id) {
        return legacyClientService.getClient(id);
    }

    @DeleteMapping("/delete")
    public void deleteClient(@RequestBody Long id) {
        legacyClientService.deleteClient(id);
    }
}
