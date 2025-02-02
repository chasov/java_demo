package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.dto.TransactionAcceptDto;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.dto.TransactionResultDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.enums.AccountStatus;
import ru.t1.java.demo.model.enums.TransactionStatus;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.util.TransactionMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository repository;
    private final TransactionMapper transactionMapper;
    private final AccountService accountService;
    private final KafkaTemplate<String, TransactionAcceptDto> kafkaTemplate;
    @Value("${t1.kafka.topic.transaction-accept}")
    private String transactionAcceptTopic;

    @Override
    @Transactional
    public List<Transaction> getAll() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public Transaction getById(UUID id) {
        return repository.findByTransactionId(id)
                .orElseThrow(() -> new NoSuchElementException("Transaction with ID " + id + " not found"));
    }

//    @Override
//    @Transactional
//    public void delete(UUID id) {
//        Transaction transaction = repository.findByTransactionId(id)
//                .orElseThrow(() -> new NoSuchElementException("Transaction with ID " + id + " not found"));
//        repository.deleteById(id);
//    }

    @Override
    @Transactional
    public Transaction create(TransactionDto dto) {
        Transaction transaction = transactionMapper.toEntity(dto);
        return repository.save(transaction);
    }


    @Override
    @Transactional
    public void registerTransactions(List<Transaction> transactions) {
        repository.saveAll(transactions);
    }

    @Override
    @Transactional
    public void processing(TransactionDto dto) {
        Account account = accountService.getById(dto.getAccountId());

        if (account.getStatus().equals(AccountStatus.OPEN)) {

            dto.setStatus(TransactionStatus.REQUESTED);
            dto.setTimestamp(LocalDateTime.now());

            Transaction transaction = transactionMapper.toEntity(dto);

            account.setBalance(account.getBalance() - transaction.getAmount());
            accountService.save(account);
            repository.save(transaction);

            TransactionAcceptDto acceptDto = TransactionAcceptDto.builder()
                    .clientId(account.getClientId().getClientId())
                    .accountId(account.getAccountId())
                    .transactionId(transaction.getTransactionId())
                    .timestamp(transaction.getTimestamp())
                    .transactionAmount(transaction.getAmount())
                    .accountBalance(account.getBalance())
                    .build();
            System.out.println("!!!!!!!!!!!acceptdto" + acceptDto);

            kafkaTemplate.send(transactionAcceptTopic, acceptDto);

        }
    }

    @Override
    @Transactional
    public void addToData(TransactionResultDto dto) {
        Transaction transaction = getById(dto.getTransactionId());
        transaction.setStatus(dto.getStatus());
        repository.save(transaction);
    }

}
