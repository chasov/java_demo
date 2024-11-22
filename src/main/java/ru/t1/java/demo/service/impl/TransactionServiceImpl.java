package ru.t1.java.demo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
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
import ru.t1.java.demo.model.dto.TransactionDto;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.GenericService;
import ru.t1.java.demo.service.TransactionParserService;
import ru.t1.java.demo.util.TransactionMapper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TransactionServiceImpl implements GenericService<Transaction>, TransactionParserService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final TransactionMapper transactionMapper;

    /*@PostConstruct
    void init() {
        List<Transaction> transactions = null;
        try {
            transactions = parseJson();
        } catch (IOException e) {
            log.error("Ошибка во время обработки записей", e);
        }
        if (transactions != null) {
            transactionRepository.saveAll(transactions);
        }
    }*/

    @Override
    public List<Transaction> parseJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TransactionDto[] transactions = mapper.readValue(new File("src/main/resources/mock_data/transaction/tbl_transaction.json"),
                TransactionDto[].class);
        return Arrays.stream(transactions)
                .map(transactionMapper::toEntity)
                .collect(Collectors.toList());
    }

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

    @Transactional
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
        log.info("Balance of account id {} was {}", account.getId(), account.getBalance());
        log.info("New transaction has amount {}", transaction.getAmount());
        account.setBalance(account.getBalance().add(transaction.getAmount()));
        log.info("New balance of account id {} are {} ", account.getId(), account.getBalance());
        return transactionRepository.save(transaction);
    }

    @Transactional
    @LogDataSourceError
    public Transaction updateTransaction(Long transactionId, TransactionDto transactionDto) throws TransactionException, ClientException {
        Optional<Transaction> transactionToUpdate = transactionRepository.findById(transactionId);
        if (transactionToUpdate.isEmpty()) {
            throw new TransactionException(String.format("Transaction with id %s is not exists", transactionId));
        }

        Account account = accountService.findById(transactionToUpdate.get().getAccountId());

        transactionToUpdate.get().setAmount(transactionDto.getAmount());
        transactionToUpdate.get().setTime(transactionDto.getTimestamp());
        transactionToUpdate.get().setAccount(account);

        return transactionRepository.save(transactionToUpdate.get());
    }
}

