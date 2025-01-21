package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.aop.HandlingResult;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.aop.Track;
import ru.t1.java.demo.aop.LogException;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.service.ClientService;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.service.LegacyClientService;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ClientController {

    private final ClientService clientService;
    private final LegacyClientService legacyClientService;

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

    @LogDataSourceError
    @PostMapping("/save")
    public String saveClieint(@RequestParam String first_name, @RequestParam String last_name, @RequestParam String middle_name) {
        try {
            Client client = new Client();
            client.setFirstName(first_name);
            client.setMiddleName(middle_name);
            client.setLastName(last_name);
            legacyClientService.saveClient(client);
        } catch (Exception e){
            log.error("Problem with saving client: ", e);
            return "Failed to save client";
        }
        return "Client saved OK";
    }

    @LogDataSourceError
    @PutMapping("/update")
    public String updateClient(@RequestParam Long id, @RequestParam String first_name, @RequestParam String middle_name, @RequestParam String last_name){
        try{
            Optional<Client> client_opt = legacyClientService.findClientById(id);
            if(client_opt.isEmpty()){
                return "No client with this ID";
            }
            Client client = client_opt.get();
            client.setLastName(last_name);
            client.setFirstName(first_name);
            client.setMiddleName(middle_name);
            legacyClientService.saveClient(client);

        } catch (Exception e){
            log.error("Error with updating client: " + e);
            return "Error with updating client";
        }
        return "Client with ID: " + id + "updated successfully";
    }

    @LogDataSourceError
    @DeleteMapping("/delete")
    public String deleteClient(@RequestParam Long id){
        try{
            legacyClientService.deleteClientById(id);
        } catch (Exception e){
            log.error("Error with deleting client: " + e);
            return "Error with deleting client";
        }
        return "Client with ID: " + id + " deleted successfully";
    }
}
