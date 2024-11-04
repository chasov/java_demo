package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.aop.HandlingResult;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.aop.LogException;
import ru.t1.java.demo.aop.Track;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.exception.AccountException;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.exception.TransactionException;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.GenericService;
import ru.t1.java.demo.util.mapper.TransactionMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TransactionService implements GenericService<TransactionDto> {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    private final TransactionMapper transactionMapper;

    @Override
    @Transactional(readOnly = true)
    @LogDataSourceError
    public TransactionDto findById(Long id) {
        return transactionMapper.toDto(findEntityById(id));
    }

    @Transactional(readOnly = true)
    @LogDataSourceError
    public Transaction findEntityById(Long id) {
        Optional<Transaction> transaction = transactionRepository.findById(id);
        if (transaction.isEmpty()) {
            throw new TransactionException(String.format("Transaction with id %s is not exists", id));
        }
        return transaction.get();
    }

    @Override
    @Transactional(readOnly = true)
    @LogException
    @Track
    @HandlingResult
    public List<TransactionDto> findAll() {
        return transactionRepository.findAll()
                .stream().map(transaction -> transactionMapper.toDto(transaction)).collect(Collectors.toList());
    }

    @Override
    public TransactionDto save(TransactionDto entity) {
        return transactionMapper.toDto(this.saveEntity(transactionMapper.toEntity(entity)));
    }

    public Transaction saveEntity(Transaction entity) {
        return transactionRepository.save(entity);
    }

    @Override
    @LogDataSourceError
    public void delete(Long id) throws TransactionException {
        transactionRepository.delete(findEntityById(id));
    }

    @Transactional
    @LogDataSourceError
    public TransactionDto addTransaction(TransactionDto transaction) throws AccountException {
        AccountDto account = accountService.findById(transaction.getAccountId());
        log.info("Balance of account id {} was {}", transaction.getAccountId(), account.getBalance());
        log.info("New transaction has amount {}", transaction.getAmount());
        account.setBalance(account.getBalance().add(transaction.getAmount()));
        log.info("New balance of account id {} are {} ", transaction.getAccountId(), account.getBalance());
        return this.save(transaction);
    }

    @LogDataSourceError
    public TransactionDto updateTransaction(Long id, TransactionDto transactionDetailsDto) throws TransactionException, ClientException {
        Account account = accountService.findEntityById(transactionDetailsDto.getAccountId());
        Optional<Transaction> transaction = transactionRepository.findById(id);
        if (transaction.isEmpty()) {
            throw new TransactionException(String.format("Transaction with id %s is not exists", id));
        }
        transaction.get().setAmount(transactionDetailsDto.getAmount());
        transaction.get().setTime(transactionDetailsDto.getTime());
        transaction.get().setAccount(account);
        return transactionMapper.toDto(this.saveEntity(transaction.get()));
    }

    @Transactional(readOnly = true)
    @LogDataSourceError
    public List<TransactionDto> findAllTransactionsById(Long id) throws AccountException{
        this.findById(id);
        return transactionRepository.findAllTransactionsByAccountId(id)
                .stream().map(transaction -> transactionMapper.toDto(transaction)).collect(Collectors.toList());
    }
}
