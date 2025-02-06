package ru.t1.java.demo.controller;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.t1.java.demo.dto.AccountDTO;
import ru.t1.java.demo.dto.ClientDTO;
import ru.t1.java.demo.dto.SuccessMessage;
import ru.t1.java.demo.service.ClientService;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/client")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ClientController {

    private final ClientService clientService;

    private static final String SUCCESS_MESSAGE_CREATED = "Клиент успешно создан и сохранен в системе";
    private static final String SUCCESS_MESSAGE_UPDATED = "Клиент успешно обновлен";
    private static final String SUCCESS_MESSAGE_DELETED = "Клиент успешно удален";
    private static final String CLIENT_URL = "api/v1/client/";


    @GetMapping()
    public ResponseEntity<List<ClientDTO>> getAllClient(
            @RequestParam(required = false, defaultValue = "1") @Min(1) int page,
            @RequestParam(required = false, defaultValue = "10") @Min(1) @Max(100) int size) {
        List<ClientDTO> account = clientService.getAllClients(page, size);
        return account.isEmpty() ?
                ResponseEntity.noContent().build() :
                ResponseEntity.ok().body(account);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ClientDTO> getClient(@PathVariable UUID id) {
        ClientDTO clientDTO = clientService.getClient(id);
        return ResponseEntity.ok().body(clientDTO);
    }

    @GetMapping("/{id}/accounts")
    public ResponseEntity<List<AccountDTO>> getAccountsByClient(@PathVariable UUID id) {
        List<AccountDTO> accountsByClientId = clientService.findAccountsByClientId(id);
        return accountsByClientId.isEmpty() ?
                ResponseEntity.noContent().build() :
                ResponseEntity.ok().body(accountsByClientId);
    }

    @PostMapping()
    public ResponseEntity<SuccessMessage> addClient(@RequestBody ClientDTO clientDTO) {
        UUID clientId = clientService.addClient(clientDTO);
        String response = CLIENT_URL + clientId;
        return ResponseEntity.created(URI.create(response))
                .body(new SuccessMessage(SUCCESS_MESSAGE_CREATED));
    }

    @PatchMapping()
    public ResponseEntity<SuccessMessage> patchClient(@RequestBody ClientDTO clientDTO) {
        clientService.patchClient(clientDTO);
        return ResponseEntity.ok().body(new SuccessMessage(SUCCESS_MESSAGE_UPDATED));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<SuccessMessage> deleteClient(@PathVariable UUID id) {
        clientService.deleteClient(id);
        return ResponseEntity.ok().body(new SuccessMessage(SUCCESS_MESSAGE_DELETED));
    }
}

