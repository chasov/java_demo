package ru.t1.java.demo.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.annotation.LogAfterThrowing;
import ru.t1.java.demo.dto.TransactionAcceptDto;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.entity.Account;
import ru.t1.java.demo.model.entity.Transaction;
import ru.t1.java.demo.model.enums.AccountStatus;
import ru.t1.java.demo.model.enums.AccountType;
import ru.t1.java.demo.model.enums.TransactionStatus;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.util.TransactionMapper;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@LogAfterThrowing
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;
    private final KafkaTemplate<String, TransactionAcceptDto> kafkaTemplate;

    @Value("${app.kafka.topics.transaction-accept}")
    private String transactionAcceptTopic;

    public List<TransactionDto> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(transactionMapper::toDto)
                .collect(Collectors.toList());
    }

    public TransactionDto getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .map(transactionMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
    }

    public TransactionDto createTransaction(TransactionDto transactionDto) {
        Transaction transaction = transactionMapper.toEntity(transactionDto);
        return transactionMapper.toDto(transactionRepository.save(transaction));
    }

    public TransactionDto updateTransaction(Long id, TransactionDto transactionDto) {
        Transaction existingTransaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id:" + id));
        Account account = accountRepository.findById(transactionDto.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + transactionDto.getAccountId()));
        existingTransaction.setAccount(account);
        existingTransaction.setAmount(transactionDto.getAmount());
        existingTransaction.setTransactionTime(transactionDto.getTransactionTime());    //TODO: здесь возможна передача значения null при незаполненном поле в запросе
        return transactionMapper.toDto(transactionRepository.save(existingTransaction));
    }

    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }

    public void saveTransaction(TransactionDto transactionDto) {
        transactionRepository.save(transactionMapper.toEntity(transactionDto));
    }

    @Transactional
    public void processTransaction(TransactionDto transactionDto) {
        Account account = accountRepository.findByIdAndStatus(transactionDto.getAccountId(), AccountStatus.OPEN)
                .orElseThrow(() -> new IllegalArgumentException("Account is not OPEN or does not exist"));

        Transaction transaction = transactionMapper.toEntity(transactionDto);
        transactionRepository.save(transaction);

        account.setBalance(account.getAccountType() == AccountType.CREDIT
                ? account.getBalance().subtract(transactionDto.getAmount())
                : account.getBalance().add(transactionDto.getAmount()));
        accountRepository.save(account);

        // Отправляем сообщение в топик t1_demo_transaction_accept
        TransactionAcceptDto acceptDto = new TransactionAcceptDto(
                account.getClient().getClientId(),
                account.getAccountId(),
                transaction.getTransactionId(),
                transaction.getTimestamp(),
                transactionDto.getAmount(),
                account.getBalance()
        );
        kafkaTemplate.send(transactionAcceptTopic, acceptDto);
    }

    @Override
    @Transactional
    public void updateTransactionStatus(UUID transactionId, TransactionStatus status) {
        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + transactionId));
        if (!transaction.getStatus().equals(TransactionStatus.REQUESTED)) {
            return;
        }
        transaction.setStatus(status);
        transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public void blockTransaction(UUID transactionId) {
        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + transactionId));
        if (!(transaction.getStatus().equals(TransactionStatus.REQUESTED)
                || transaction.getStatus().equals(TransactionStatus.ACCEPTED))) {
            return;
        }
        transaction.setStatus(TransactionStatus.BLOCKED);
        transactionRepository.save(transaction);

        Account account = transaction.getAccount();
        account.setStatus(AccountStatus.BLOCKED);
        account.setFrozenAmount(account.getFrozenAmount().add(transaction.getAmount()));
        accountRepository.save(account);
    }

    @Override
    @Transactional
    public void rejectTransaction(UUID transactionId) {
        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + transactionId));
        if (!transaction.getStatus().equals(TransactionStatus.REQUESTED)) {
            return;
        }
        transaction.setStatus(TransactionStatus.REJECTED);
        transactionRepository.save(transaction);

        Account account = transaction.getAccount();
        account.setBalance(account.getAccountType() == AccountType.CREDIT
                        ? account.getBalance().add(transaction.getAmount())
                        : account.getBalance().subtract(transaction.getAmount()));
        accountRepository.save(account);
    }
}
