package ru.t1.java.demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.Metric;
import ru.t1.java.demo.dto.AccountDTO;
import ru.t1.java.demo.dto.ClientDTO;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.util.AccountMapper;
import ru.t1.java.demo.util.ClientMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientService {
    private final ClientRepository clientRepository;

    private final AccountRepository accountRepository;

    private static final String CLIENT_NOT_FOUND_WITH_ID = "Client not found with id: ";
    private static final String NULL_REQUEST_DTO_MESSAGE = "Client must not be null";

    public List<ClientDTO> getAllClients(int page, int size) {
        log.info("Получение всех клиентов - Страница: {}, Размер: {}", page, size);
        return clientRepository
                .findAll(PageRequest.of(page - 1, size))
                .stream()
                .map(ClientMapper::toDTO)
                .toList();
    }

    @Metric
    public ClientDTO getClient(UUID id) {
        log.info("Получение клиента с ID: {}", id);
        return ClientMapper.toDTO(findById(id));
    }

    public List<AccountDTO> findAccountsByClientId(UUID clientId) {
        log.info("Получение списка счетов клиента с ID: {}", clientId);
        Client client = findById(clientId);
        List<Account> listAccounts = accountRepository.findByClient(client);
        return listAccounts.stream().map(AccountMapper::toDTO).toList();
    }

    public UUID addClient(ClientDTO clientDTO) {
        if (clientDTO == null) {
            throw new IllegalArgumentException(NULL_REQUEST_DTO_MESSAGE);
        }
        Client client = ClientMapper.toEntity(clientDTO);
        UUID clientId = clientRepository.save(client).getClientId();
        log.info("Добавлен новый клиент с ID: {}", clientId);
        return clientId;
    }

    public void patchClient(ClientDTO clientDTO) {
        log.info("Обновление клиент с ID: {}", clientDTO.getClientId());
        Client client = findById(clientDTO.getClientId());
        Optional.ofNullable(clientDTO.getFirstName())
                .ifPresent(client::setFirstName);
        Optional.ofNullable(clientDTO.getMiddleName())
                .ifPresent(client::setMiddleName);
        Optional.ofNullable(clientDTO.getLastName())
                .ifPresent(client::setLastName);
        clientRepository.save(client);
        log.info("Клиент с ID: {} обновлен", client.getClientId());
    }

    public void deleteClient(UUID id) {
        log.info("Попытка удалить клиента с ID: {}", id);
        if (!clientRepository.existsById(id)) {
            throw new ClientException(CLIENT_NOT_FOUND_WITH_ID);
        }
        clientRepository.deleteById(id);
        log.info("Клиент с ID: {} удален", id);
    }

    private Client findById(UUID id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ClientException(CLIENT_NOT_FOUND_WITH_ID));
    }
}

