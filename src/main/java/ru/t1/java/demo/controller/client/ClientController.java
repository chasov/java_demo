package ru.t1.java.demo.controller.client;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.t1.java.demo.aop.annotation.Metric;
import ru.t1.java.demo.model.dto.ClientDto;
import ru.t1.java.demo.service.client.ClientService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/client")
public class ClientController {

    private final ClientService clientService;

    @Metric
    @PostMapping
    public ClientDto createClient(@RequestBody ClientDto client) {
        return clientService.registerClient(client);
    }

    @Metric
    @GetMapping("/{clientId}")
    public ClientDto getClient(@PathVariable Long clientId) {
        return clientService.getClient(clientId);
    }

    @Metric
    @DeleteMapping("/{clientId}")
    public void deleteClient(@PathVariable Long clientId) {
        clientService.deleteClient(clientId);
    }
}
