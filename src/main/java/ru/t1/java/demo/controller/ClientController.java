package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.t1.java.demo.annotation.LogDataSourceError;
import ru.t1.java.demo.service.ClientService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
@LogDataSourceError
public class ClientController {

    private final ClientService clientService;

    @LogDataSourceError
    @GetMapping(value = "/client")
    public void doSomething() throws IOException, InterruptedException {
//        try {
//            clientService.parseJson();
        Thread.sleep(30L);
        throw new RuntimeException("Simulated exception during CRUD operation");
//        } catch (Exception e) {
//            log.info("Catching exception from ClientController");
//            throw new ClientException();
//        }
    }

}
