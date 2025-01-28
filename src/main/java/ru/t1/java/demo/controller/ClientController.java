package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.aop.HandlingResult;
import ru.t1.java.demo.aop.LogException;
import ru.t1.java.demo.aop.Metric;
import ru.t1.java.demo.aop.Track;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.dto.ClientDto;
import ru.t1.java.demo.service.ClientService;
import ru.t1.java.demo.util.ClientMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@Slf4j
public class ClientController {

    private final ClientService clientService;
    private final ClientMapper clientMapper;

    @LogException
    @Metric(maxExecutionTime = 1)
    @GetMapping(value = "/parse")
    public void parseSource() {

    }


    @LogException
    @Metric(maxExecutionTime = 1)
    @GetMapping("/client")
    public ResponseEntity<ClientDto> getClient() {
        log.debug("Getting client with id ");
        return ResponseEntity.ok()
                .body(ClientDto.builder()
                        .firstName("John")
                        .build());
    }

    @LogException
    @Metric(maxExecutionTime = 1)
    @GetMapping("/admin")
//    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin Board.";
    }

    @LogException
    @Metric(maxExecutionTime = 1)
    @HandlingResult
    @PostMapping("/register")
    public ResponseEntity<Client> register(@RequestBody ClientDto clientDto) {
        log.info("Registering client: {}", clientDto);
        Client client = clientService.registerClient(clientMapper.toEntityWithId(clientDto));
        log.info("Client registered: {}", client.getId());
        return ResponseEntity.ok().body(client);
    }

    @LogException
    @Track
    @PatchMapping("client/{clientId}")
    @HandlingResult
    public ClientDto patchById(@PathVariable Long clientId,
                               @RequestBody ClientDto dto) {
        return clientService.patchById(clientId, dto);
    }

    @LogException
    @Track
    @GetMapping(value = "/client/{clientId}")
    @HandlingResult
    public ClientDto getById(@PathVariable Long clientId) {
        return clientService.getById(clientId);
    }

    @LogException
    @Track
    @DeleteMapping("client/{clientId}")
    @HandlingResult
    public void deleteById(@PathVariable Long clientId) {
        clientService.deleteById(clientId);
    }
}


