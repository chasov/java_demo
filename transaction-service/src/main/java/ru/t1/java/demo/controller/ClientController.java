package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.aop.annotations.Metric;
import ru.t1.java.demo.dto.transaction_serviceDto.ClientDto;
import ru.t1.java.demo.mapper.ClientMapper;
import ru.t1.java.demo.service.impl.ClientServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {
    private final ClientServiceImpl clientService;
    private final ClientMapper clientMapper;

    @Metric(time = 1)
    @GetMapping
    public List<ClientDto> getAllClients() {
        return clientService.getAllClients()
                .stream()
                .map(clientMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ClientDto getClientById(@PathVariable Long id) {
        return clientMapper.toDto(clientService.getClientById(id));
    }

    @PostMapping
    public ClientDto createClient(@RequestBody ClientDto clientDto) {
        return clientMapper.toDto(clientService.createClient(clientMapper.toEntity(clientDto)));
    }

    @PutMapping("/{id}")
    public ClientDto updateClient(@PathVariable Long id, @RequestBody ClientDto clientDto) {
        return clientMapper.toDto(clientService.updateClient(id, clientMapper.toEntity(clientDto)));
    }

    @DeleteMapping("/{id}")
    public void deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
    }


}