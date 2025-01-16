package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.amplicode.rautils.patch.ObjectPatcher;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.util.TransactionMapperImpl;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final ObjectPatcher objectPatcher;
    private final TransactionRepository repository;


    @PostConstruct
    void init() {
        try {
            List<Transaction> transactions = parseJson();
            repository.saveAll(transactions);
        }  catch (IOException e) {
            log.error("Ошибка при загрузке транзакций из JSON", e);
        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка при сохранении транзакций в базу данных", e);
        }
    }


    public Page<Transaction> getList(Pageable pageable) {
        return transactionRepository.findAll(pageable);
    }

    public Optional<Transaction> getOne(UUID id) {
        return transactionRepository.findById(id);
    }

    public List<Transaction> getMany(Collection<UUID> ids) {
        return transactionRepository.findAllById(ids);
    }

    public Transaction create(Transaction dto) {
        return transactionRepository.save(dto);
    }

    public Transaction patch(Transaction id, JsonNode patchNode) {
        // Применяем изменения из patchNode к объекту account
        objectPatcher.patchAndValidate(id, patchNode);  // Патчим объект

        // Сохраняем обновленный объект в базе данных
        return transactionRepository.save(id);
    }

    public List<Transaction> patchMany(Collection<Transaction> ids, JsonNode patchNode) {
        // Применяем изменения из patchNode ко всем аккаунтам
        for (Transaction transaction : ids) {
            objectPatcher.patchAndValidate(ids, patchNode);  // Патчим каждый аккаунт
        }

        // Сохраняем все обновленные аккаунты в базе данных
        return transactionRepository.saveAll(ids);
    }

    public void delete(Transaction id) {
        transactionRepository.delete(id);
    }

    public void deleteMany(Collection<UUID> ids) {
        transactionRepository.deleteAllById(ids);
    }

    public List<Transaction> parseJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        TransactionDto[] transactionDtos = mapper.readValue(new File("src/main/resources/MOCK_DATA_TRANSACTIONS.json"), TransactionDto[].class);

        return Arrays.stream(transactionDtos)
                .map(TransactionMapperImpl::toEntity)
                .collect(Collectors.toList());
    }
}
