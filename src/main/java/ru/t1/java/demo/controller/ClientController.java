package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.t1.java.demo.aop.HandlingResult;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.aop.Track;
import ru.t1.java.demo.aop.LogException;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.service.ClientService;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ClientController {

    private final ClientService clientService;

    //@LogException
    @LogDataSourceError
    //@Track
    @GetMapping(value = "/client")
    //@HandlingResult
    public void doSomething() throws IOException, InterruptedException {
        try {
            clientService.parseJson();
            Thread.sleep(1000L);
        } catch (Exception e) {
            log.error("Catching exception from ClientController");
            throw new ClientException("Client error");
        }
    }

}
