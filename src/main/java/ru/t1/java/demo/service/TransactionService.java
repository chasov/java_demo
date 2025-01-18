package ru.t1.java.demo.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.ClientDto;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.util.ClientMapper;
import ru.t1.java.demo.util.TransactionMapper;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class TransactionService {
    private final TransactionRepository repository;
    private final Map<Long, Transaction> cache;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
        this.cache = new HashMap<>();
    }

    @PostConstruct
    void init() {
        getTransactionById(1L);
    }

    public TransactionDto getTransactionById(Long id) {
        log.debug("Call method getTransactionById with id {}", id);
        TransactionDto transactionDto = null;

        if (cache.containsKey(id)) {
            return TransactionMapper.toDto(cache.get(id));
        }

        try {
            Transaction entity = repository.findById(id).get();
            transactionDto = TransactionMapper.toDto(entity);
            cache.put(id, entity);
        } catch (Exception e) {
            log.error("Error: ", e);
//            throw new ClientException();
        }

//        log.debug("Client info: {}", clientDto.toString());
        return transactionDto;
    }


    public void deleteTransactionById(Long id) {
        repository.deleteById(id);
    }

}
