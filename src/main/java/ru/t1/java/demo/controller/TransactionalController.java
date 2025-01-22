package ru.t1.java.demo.controller;


import jakarta.validation.Valid;
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
import ru.t1.java.demo.dto.SuccessMessage;
import ru.t1.java.demo.dto.TransactionalDTO;
import ru.t1.java.demo.dto.TransactionalRequestDTO;
import ru.t1.java.demo.service.TransactionalService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("api/v1/transactional")
@RequiredArgsConstructor
@Slf4j
@Validated
public class TransactionalController {

    private final TransactionalService transactionalService;

    private static final String SUCCESS_MESSAGE_CREATED = "Транзакция успешно создана и сохранена в системе";
    private static final String SUCCESS_MESSAGE_UPDATED = "Транзакция успешно обновлена";
    private static final String SUCCESS_MESSAGE_DELETED = "Транзакция успешно удалена";
    private static final String URI_TEMPLATE = "api/v1/transactional/";

    @GetMapping()
    public ResponseEntity<List<TransactionalDTO>> getAllTransactional(
            @RequestParam(required = false, defaultValue = "1") @Min(1) int page,
            @RequestParam(required = false, defaultValue = "10") @Min(1) @Max(100) int size) {
        List<TransactionalDTO> allTransactional = transactionalService.getAllTransactional(page, size);
        return allTransactional.isEmpty() ?
                ResponseEntity.noContent().build() :
                ResponseEntity.ok().body(allTransactional);
    }


    @GetMapping(value = "/{id}")
    public ResponseEntity<TransactionalDTO> getTransactional(@PathVariable Long id) {
        TransactionalDTO transactional = transactionalService.getTransactional(id);
        return ResponseEntity.ok().body(transactional);
    }

    @PostMapping()
    public ResponseEntity<SuccessMessage> addTransactional(
            @RequestBody @Valid TransactionalRequestDTO transactionalRequestDTO) {
        Long id = transactionalService.addTransactional(transactionalRequestDTO);
        String response = URI_TEMPLATE + id;
        return ResponseEntity.created(URI.create(response))
                .body(new SuccessMessage(SUCCESS_MESSAGE_CREATED));
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<SuccessMessage> patchTransactional(
            @PathVariable Long id,
            @RequestBody @Valid TransactionalRequestDTO transactionalRequestDTO) {
        transactionalService.patchTransactional(id, transactionalRequestDTO);
        return ResponseEntity.ok().body(new SuccessMessage(SUCCESS_MESSAGE_UPDATED));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<SuccessMessage> deleteTransactional(@PathVariable Long id) {
        transactionalService.deleteTransactional(id);
        return ResponseEntity.ok().body(new SuccessMessage(SUCCESS_MESSAGE_DELETED));
    }
}

