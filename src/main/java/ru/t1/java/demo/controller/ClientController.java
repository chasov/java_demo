package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.aop.HandlingResult;
import ru.t1.java.demo.aop.LogException;
import ru.t1.java.demo.aop.Track;
import ru.t1.java.demo.dto.ClientDto;
import ru.t1.java.demo.service.ClientService;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class ClientController {
    private final ClientService clientService;

    @LogException
    @Track
    @PostMapping(value = "/client")
    @HandlingResult
    public ClientDto save(@RequestBody @NonNull ClientDto dto) {
        return clientService.save(dto);

    }

    @LogException
    @Track
    @PatchMapping("client/{clientId}")
    @HandlingResult
    public ClientDto patchById(@PathVariable @NonNull Long clientId,
                               @RequestBody @NonNull ClientDto dto) {
        return clientService.patchById(clientId, dto);
    }

    @LogException
    @Track
    @GetMapping(value = "/client/{clientId}")
    @HandlingResult
    public ClientDto getById(@PathVariable @NonNull Long clientId) {
        return clientService.getById(clientId);
    }

    @LogException
    @Track
    @DeleteMapping("client/{clientId}")
    @HandlingResult
    public void deleteById(@PathVariable @NonNull Long clientId) {
        clientService.deleteById(clientId);
    }
}
