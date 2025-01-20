package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.dto.ClientDto;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.service.ClientService;
import ru.t1.java.demo.util.ClientMapper;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;

    @LogDataSourceError
    @Override
    public ClientDto save(ClientDto dto) {
        return ClientMapper.toDto(clientRepository.save(ClientMapper.toEntity(dto)));
    }

    @LogDataSourceError
    @Override
    public ClientDto patchById(Long clientId, ClientDto dto) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientException("Client not found"));
        client.setFirstName(dto.getFirstName());
        client.setLastName(dto.getLastName());
        client.setMiddleName(dto.getMiddleName());

        return ClientMapper.toDto(clientRepository.save(client));
    }

    @LogDataSourceError
    @Override
    public ClientDto getById(Long clientId) {
        return ClientMapper.toDto(clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientException("Client not found")));
    }

    @LogDataSourceError
    @Override
    public void deleteById(Long clientId) {
        clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientException("Client not found"));
        clientRepository.deleteById(clientId);
    }
}
