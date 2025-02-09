package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.aop.annotation.LogDataSourceError;
import ru.t1.java.demo.aop.annotation.Metric;
import ru.t1.java.demo.exception.account.AccountException;
import ru.t1.java.demo.exception.transaction.TransactionException;
import ru.t1.java.demo.kafka.producer.transaction.KafkaTransactionAcceptProducer;
import ru.t1.java.demo.mapper.AccountMapper;
import ru.t1.java.demo.mapper.TransactionMapper;
import ru.t1.java.demo.model.dto.account.AccountDto;
import ru.t1.java.demo.model.dto.transaction.TransactionAcceptEvent;
import ru.t1.java.demo.model.dto.transaction.TransactionDto;
import ru.t1.java.demo.model.dto.transaction.TransactionResultEvent;
import ru.t1.java.demo.model.entity.Account;
import ru.t1.java.demo.model.entity.Transaction;
import ru.t1.java.demo.model.enums.AccountStatus;
import ru.t1.java.demo.model.enums.TransactionStatus;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.TransactionsRepository;
import ru.t1.java.demo.service.account.AccountService;
import ru.t1.java.demo.service.transaction.TransactionService;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final AccountMapper accountMapper;
    private final AccountRepository accountRepository;
    private final TransactionsRepository transactionsRepository;
    private final TransactionMapper transactionMapper;
    private final KafkaTransactionAcceptProducer<TransactionAcceptEvent> kafkaTransactionAcceptProducer;
    private final AccountService accountService;

    @Override
    @LogDataSourceError
    @Metric
    @Transactional
    public TransactionDto conductTransaction(TransactionDto transactionDto) {
        AccountDto toAccountDto = accountService.findAccountById(transactionDto.getToAccountId());
        AccountDto fromAccountDto = accountService.findAccountById(transactionDto.getFromAccountId());

        Account toAccount = accountMapper.toEntity(toAccountDto);
        Account fromAccount = accountMapper.toEntity(fromAccountDto);

        if (toAccount.getStatus() == AccountStatus.OPEN && fromAccount.getStatus() == AccountStatus.OPEN) {
            Transaction transaction = createTransaction(transactionDto, toAccount, fromAccount);
            transaction = transactionsRepository.save(transaction);
            sendTransactionAcceptEvent(transaction, toAccount, fromAccount);

            return transactionMapper.toDto(transaction);
        }

        throw new AccountException("Account is not open for transactions");
    }

    @Override
    @LogDataSourceError
    @Metric
    public TransactionDto getTransaction(Long transactionId) {
        Transaction transaction = findTransactionById(transactionId);
        return transactionMapper.toDto(transaction);
    }

    @Override
    @LogDataSourceError
    @Metric
    @Transactional
    public void cancelTransaction(Long transactionId) {
        Transaction transaction = findTransactionById(transactionId);

        transaction.setStatus(TransactionStatus.CANCELLED);

        transactionsRepository.save(transaction);
    }

    @Override
    @LogDataSourceError
    @Metric
    @Transactional
    public void processTransaction(TransactionResultEvent transactionResultEvent) {
        switch (transactionResultEvent.getStatus()) {
            case ACCEPTED:
                updateAndSaveTransactionAfterProcess(transactionResultEvent);
                processAcceptedTransaction(transactionResultEvent);
                break;
            case BLOCKED:
                updateAndSaveTransactionAfterProcess(transactionResultEvent);
                processBlockedTransaction(transactionResultEvent);
                break;
            case REJECTED:
                updateAndSaveTransactionAfterProcess(transactionResultEvent);
                break;
        }
    }

    public Transaction findTransactionById(long id) {
        return transactionsRepository.findById(id)
                .orElseThrow(() -> new TransactionException("Transaction not found"));
    }

    public Transaction findTransactionByTransactionId(UUID transactionId) {
        return transactionsRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new TransactionException("Transaction not found"));
    }

    private void updateAndSaveTransactionAfterProcess(TransactionResultEvent event) {
        Transaction transaction = findTransactionByTransactionId(event.getTransactionId());

        transaction.setStatus(event.getStatus());
        transaction.setProcessedAt(LocalDateTime.now());

        transactionsRepository.save(transaction);
    }

    private void processAcceptedTransaction(TransactionResultEvent event) {
        Transaction transaction = findTransactionByTransactionId(event.getTransactionId());

        AccountDto toAccountDto = accountService.findAccountByAccountId(event.getToAccountId());
        AccountDto fromAccountDto = accountService.findAccountByAccountId(event.getFromAccountId());

        Account toAccount = accountMapper.toEntity(toAccountDto);
        Account fromAccount = accountMapper.toEntity(fromAccountDto);

        toAccount.setBalance(toAccount.getBalance().add(transaction.getAmount()));
        fromAccount.setBalance(fromAccount.getBalance().subtract(transaction.getAmount()));

        accountRepository.save(toAccount);
        accountRepository.save(fromAccount);
    }

    private void processBlockedTransaction(TransactionResultEvent event) {
        Transaction transaction = findTransactionByTransactionId(event.getTransactionId());

        AccountDto accountDto = accountService.findAccountByAccountId(event.getToAccountId());
        Account account = accountMapper.toEntity(accountDto);

        account.setStatus(AccountStatus.BLOCKED);
        account.setBalance(account.getBalance().subtract(transaction.getAmount()));
        account.setFrozenAmount(account.getFrozenAmount().add(transaction.getAmount()));

        accountRepository.save(account);
    }

    private Transaction createTransaction(TransactionDto transactionDto, Account toAccount, Account fromAccount) {
        Transaction transaction = transactionMapper.toEntity(transactionDto);
        transaction.setStatus(TransactionStatus.REQUESTED);
        transaction.setTransactionId(UUID.randomUUID());
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        return transaction;
    }

    private TransactionAcceptEvent prepareTransactionAcceptEvent(Transaction transaction, Account toAccount, Account fromAccount) {
        return TransactionAcceptEvent.builder()
                .clientId(fromAccount.getClient().getClientId())
                .fromAccountId(fromAccount.getAccountId())
                .toAccountId(toAccount.getAccountId())
                .transactionId(transaction.getTransactionId())
                .createdAt(transaction.getCreatedAt())
                .transactionAmount(transaction.getAmount())
                .fromAccountBalance(fromAccount.getBalance())
                .build();
    }

    private void sendTransactionAcceptEvent(Transaction transaction, Account toAccount, Account fromAccount) {
        TransactionAcceptEvent event = prepareTransactionAcceptEvent(transaction, toAccount, fromAccount);
        kafkaTransactionAcceptProducer.send(event);
    }
}
