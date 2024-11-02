package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.aop.HandlingResult;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.aop.LogException;
import ru.t1.java.demo.aop.Track;
import ru.t1.java.demo.exception.AccountException;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.exception.TransactionException;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.GenericService;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TransactionService implements GenericService<Transaction> {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    @Override
    @Transactional(readOnly = true)
    @LogDataSourceError
    public Transaction findById(Long id) {
        Optional<Transaction> transaction = transactionRepository.findById(id);
        if (transaction.isEmpty()) {
            throw new TransactionException(String.format("Account with id %s is not exists", id));
        }
        return transaction.get();
    }

    @Override
    @Transactional(readOnly = true)
    @LogException
    @Track
    @HandlingResult
    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    @Override
    public Transaction save(Transaction entity) {
        return transactionRepository.save(entity);
    }

    @Override
    @LogDataSourceError
    public void delete(Long id) throws TransactionException {
        transactionRepository.delete(findById(id));
    }

    @Transactional
    @LogDataSourceError
    public Transaction addTransaction(Transaction transaction) throws AccountException {
        Account account = accountService.findById(transaction.getAccount().getId());
        log.info("Balance of account id {} was {}", transaction.getAccount().getId(), account.getBalance());
        log.info("New transaction has amount {}", transaction.getAmount());
        account.setBalance(account.getBalance().add(transaction.getAmount()));
        log.info("New balance of account id {} are {} ", transaction.getAccount().getId(), account.getBalance());
        return this.save(transaction);
    }

    @LogDataSourceError
    public Transaction updateTransaction(Long id, Transaction transactionDetails) throws TransactionException, ClientException {
        Account account = accountService.findById(transactionDetails.getAccount().getId());
        Transaction transaction = this.findById(id);
        transaction.setAmount(transactionDetails.getAmount());
        transaction.setTime(transactionDetails.getTime());
        transaction.setAccount(account);
        return this.save(transaction);
    }
}
