package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.dto.TransactionAcceptDto;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.dto.TransactionResultDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.enums.AccountStatus;
import ru.t1.java.demo.model.enums.TransactionStatus;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.util.AccountMapper;
import ru.t1.java.demo.util.TransactionMapper;
import org.springframework.beans.factory.annotation.Value;


import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final AccountMapper accountMapper;
    private final AccountService accountService;
    private final KafkaTemplate<String, TransactionAcceptDto> kafkaTemplate;
    @Value("${t1.kafka.topic.transaction-accept}")
    private String transactionAcceptTopic;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public List<TransactionDto> getAll() {
        return transactionRepository.findAll().stream()
                .map(transactionMapper::toDto).toList();
    }

    @Override
    @Transactional
    public TransactionDto getById(UUID id) {
        return transactionRepository.findByTransactionId(id)
                .map(transactionMapper::toDto)
                .orElseThrow(() -> new NoSuchElementException("Transaction with ID " + id + " not found"));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        transactionRepository.deleteByTransactionId(id);
    }

    @Override
    @Transactional
    public TransactionDto create(TransactionDto dto) {
        Transaction transaction = transactionMapper.toEntity(dto);
        return transactionMapper.toDto(transactionRepository.save(transaction));
    }

    @Override
    @Transactional
    public void save(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public void registerTransactions(List<TransactionDto> transactionDtoList) {
        List<Transaction> transactions = transactionDtoList.stream()
                .map(transactionMapper::toEntity).toList();
        transactionRepository.saveAll(transactions);
    }

    @Override
    @Transactional
    public void processing(TransactionDto dto) {
        Account account = accountRepository.findByAccountId(dto.getAccountId())
                .orElseThrow(() -> new NoSuchElementException("Account with ID " + dto.getAccountId() + " not found"));

        if (account.getStatus().equals(AccountStatus.OPEN)) {

            dto.setStatus(TransactionStatus.REQUESTED);
            dto.setTransactionTime(LocalDateTime.now());

            Transaction transaction = transactionMapper.toEntity(dto);

            account.setBalance(account.getBalance() - transaction.getAmount());
            log.info("Баланс аккаунта {} изменен", dto.getAccountId());
            accountService.save(account);
            transactionRepository.save(transaction);

            TransactionAcceptDto acceptDto = TransactionAcceptDto.builder()
                    .clientId(account.getClientId().getClientId())
                    .accountId(account.getAccountId())
                    .transactionId(transaction.getTransactionId())
                    .transactionTime(transaction.getTransactionTime())
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
        Transaction transaction = transactionRepository.findByTransactionId(dto.getTransactionId())
                .orElseThrow(() -> new NoSuchElementException("Transaction with ID " + dto.getTransactionId() + " not found"));
        Account account = accountRepository.findByAccountId(dto.getAccountId())
                .orElseThrow(() -> new NoSuchElementException("Account with ID " + dto.getAccountId() + " not found"));

        TransactionStatus status = dto.getStatus();

        if (status == TransactionStatus.REJECTED) {
            account.setBalance(account.getBalance() + transaction.getAmount());
            log.info("Баланс аккаунта {} изменен", dto.getAccountId());
        } else if (status == TransactionStatus.BLOCKED) {
            account.setFrozenAmount(account.getFrozenAmount() + transaction.getAmount());
            account.setBalance(account.getBalance() + transaction.getAmount());
            log.info("Баланс аккаунта {} изменен", dto.getAccountId());
        }

        transaction.setStatus(status);
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

}