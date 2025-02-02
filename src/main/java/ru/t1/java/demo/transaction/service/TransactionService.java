package ru.t1.java.demo.transaction.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.account.enums.AccountStatus;
import ru.t1.java.demo.account.model.Account;
import ru.t1.java.demo.account.repository.AccountRepository;
import ru.t1.java.demo.transaction.dto.TransactionDto;
import ru.t1.java.demo.transaction.enums.TransactionStatus;
import ru.t1.java.demo.transaction.model.Transaction;
import ru.t1.java.demo.transaction.model.TransactionAccept;
import ru.t1.java.demo.transaction.model.TransactionResult;
import ru.t1.java.demo.transaction.repository.TransactionRepository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private KafkaTemplate<String, TransactionAccept> kafkaTemplate;

    @Autowired
    private AccountRepository accountRepository;

    public Transaction createTransaction(Transaction transaction) {
        if (transaction.getAmount() == null || transaction.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Transaction amount must be greater than 0");
        }
        return transactionRepository.save(transaction);
    }

    public Transaction getTransactionById(UUID id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction with uuid: " + id + " not found"));
    }

    public void deleteTransactionById(UUID id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction with uuid: " + id + " not found"));
        transactionRepository.deleteById(transaction.getId());
    }

    public void save(Collection<Transaction> transactions) {
        Objects.requireNonNull(transactions, "The transaction list must not be null");
        Set<Transaction> nonNullAccounts = transactions.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        transactionRepository.saveAll(nonNullAccounts);
    }

    @Transactional
    public void processAndSendTransactionAccept(Collection<Transaction> transaction) {
        Set<Transaction> transactions =
                StreamSupport.stream(transactionRepository.findAllById(transaction.stream().map(Transaction::getId).collect(Collectors.toSet())).spliterator(), false)
                        .collect(Collectors.toSet());
        transactions.forEach(this::sendTransaction);
    }

    private void sendTransaction(Transaction transaction) {
        Optional<Account> accountOptional = accountRepository.findById(transaction.getId());
        if(accountOptional.isEmpty()) {
            throw new RuntimeException("Transaction with uuid: " + transaction.getId() + " not found");
        }
        Account account = accountOptional.get();
        if(account.getAccountStatus() != null && account.getAccountStatus().equals(AccountStatus.OPEN) && transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            transaction.setTransactionStatus(TransactionStatus.REQUESTED);
            save(Collections.singleton(transaction));
            account.setBalance(transaction.getAmount());
            accountRepository.save(account);
            kafkaTemplate.send("t1_demo_transaction_accept", new TransactionAccept(transaction, account));
        }
        else{
            log.error("Error to send transaction:: " + transaction.getId());
            throw new RuntimeException("Error to send transaction::" + transaction.getId());
        }
    }

    public Set<Transaction> dtoToTransaction(Collection<TransactionDto> transactionDtos) {
        ObjectMapper objectMapper = new ObjectMapper();
        Set<Transaction> transactions = new HashSet<>();
        transactionDtos.forEach(transactionDto -> {
            try {
                String json = objectMapper.writeValueAsString(transactionDto);
                Transaction transaction = objectMapper.readValue(json, Transaction.class);
                transactions.add(transaction);
            } catch (Exception e) {
                log.error("Failed to convert transactionDto", e);
                e.printStackTrace();
            }
        });
        return transactions;
    }

    @Transactional
    public void updateTransaction(Collection<TransactionDto> transactionDtos) {
        Set<Transaction> allTransactions =
                StreamSupport.stream(
                        transactionRepository.findAllById(dtoToTransaction(transactionDtos).stream().map(Transaction::getId).collect(Collectors.toSet())).spliterator(), false).collect(Collectors.toSet());
        transactionDtos.forEach(transactionDto -> {
            allTransactions.forEach(transaction -> {
                if(transactionDto.getTransactionalUuid().equals(transaction.getId())){
                    switch (transactionDto.getStatus()){
                        case ACCEPTED:
                            transaction.setTransactionStatus(TransactionStatus.ACCEPTED);
                            break;
                        case BLOCKED:
                            transaction.setTransactionStatus(TransactionStatus.BLOCKED);
                            Optional<Account> accountOptBlocked = accountRepository.findById(transaction.getAccountUuid());
                            if (accountOptBlocked.isPresent()) {
                                Account account = accountOptBlocked.get();
                                BigDecimal frozenAmount = account.getFrozenAmount();
                                account.setFrozenAmount(frozenAmount.add(transaction.getAmount()));
                                account.setAccountStatus(AccountStatus.BLOCKED);
                                accountRepository.save(account);
                            }
                            break;
                        case REJECTED:
                            transaction.setTransactionStatus(TransactionStatus.REJECTED);
                            Optional<Account> accountOptRejected  = accountRepository.findById(transaction.getAccountUuid());
                            if (accountOptRejected.isPresent()) {
                                Account account = accountOptRejected.get();
                                account.setBalance(account.getBalance().subtract(transaction.getAmount()));
                                accountRepository.save(account);
                            }
                            break;
                        default:
                            break;
                    }
                }
            });
        });
    }
}
