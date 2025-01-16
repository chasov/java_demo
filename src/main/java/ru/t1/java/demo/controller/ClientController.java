package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.aop.HandlingResult;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.aop.Track;
import ru.t1.java.demo.aop.LogException;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.service.ClientService;
import ru.t1.java.demo.service.LegacyClientService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ClientController {

 private final LegacyClientService legacyClientService;

 private final ClientService clientService;

    @LogException
    @Track
    @GetMapping(value = "/client")
    @HandlingResult
    public void doSomething() throws IOException, InterruptedException {
//        try {
//            clientService.parseJson();
        Thread.sleep(3000L);
        throw new ClientException();
//        } catch (Exception e) {
//            log.info("Catching exception from ClientController");
//            throw new ClientException();
//        }
    }

    // Эндпоинт для сохранения нового клиента
    // Эндпоинт для сохранения клиента
    @LogDataSourceError
    @PostMapping("/save")
    public String saveClient(
            @RequestParam(required = false) String first_name,
            @RequestParam String last_name,
            @RequestParam(required = false) String middle_name // необязательный параметр
    ) {
        try {
            // Создаем объект клиента
            Client client = new Client();
            client.setFirstName(first_name);
            client.setLastName(last_name);
            client.setMiddleName(middle_name);

            // Сохраняем клиента
            legacyClientService.saveClient(client);
            return "Client saved successfully: " + client;
        } catch (Exception e) {
            log.error("Error while saving client: ", e);
            return "Failed to save client";
        }
    }

    // Эндпоинт для удаления клиента по id
    @LogDataSourceError
    @DeleteMapping("/delete")
    public String deleteClient(@RequestParam Long id) {
        try {
            legacyClientService.deleteClientById(id);
            return "Client deleted successfully with ID: " + id;
        } catch (Exception e) {
            log.error("Error while deleting client: ", e);
            return "Failed to delete client with ID: " + id;
        }
    }

}
