package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import ru.t1.java.demo.aop.annotations.LogDataSourceError;
import ru.t1.java.demo.config.KafkaMessageProducer;
import ru.t1.java.demo.dto.fraud_serviceDto.FraudServiceTransactionDto;
import ru.t1.java.demo.dto.fraud_serviceDto.TransactionResultAfterFraudServiceDto;
import ru.t1.java.demo.dto.transaction_serviceDto.TransactionDto;
import ru.t1.java.demo.mapper.TransactionMapper;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.enums.AccountStatusEnum;
import ru.t1.java.demo.model.enums.TransactionStatusEnum;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.TransactionService;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final AccountRepository accountRepository;

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final KafkaMessageProducer kafkaMessageProducer;
    private final AccountService accountService;

    @LogDataSourceError
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @LogDataSourceError
    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found with id: " + id));
    }

    @LogDataSourceError
    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @LogDataSourceError
    public Transaction updateTransaction(Long id, Transaction transaction) {
        if (!transactionRepository.existsById(id)) {
            throw new EntityNotFoundException("Transaction not found with id: " + id);
        }
        transaction.setId(id);
        return transactionRepository.save(transaction);
    }

    @LogDataSourceError
    public void deleteTransaction(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new EntityNotFoundException("Transaction not found with id: " + id);
        }
        transactionRepository.deleteById(id);
    }

    @Override
    public void processTransaction(TransactionDto transactionDto) {
        Transaction transaction = transactionMapper.toEntity(transactionDto);

        if (transaction.getAccount().getAccountStatus().equals(AccountStatusEnum.OPEN)) {
            transaction = transactionRepository.save(transaction);
            log.info("Transaction saved: {}", transaction);

            Account account = transaction.getAccount();
            account.setBalance(account.getBalance().subtract(transaction.getAmount()));
            accountService.updateAccount(account.getId(), account);
            kafkaMessageProducer.sendTransactionAcceptedTopic(transactionMapper.toFraudServiceDto(transaction));
        } else {
            log.info("Transaction rejected: account is not open");
        }
    }

    @Override
    public void processTransactionAfterFraud(TransactionResultAfterFraudServiceDto transactionResultAfterFraudServiceDto) {
        if (transactionResultAfterFraudServiceDto.getTransactionStatus().equals(TransactionStatusEnum.BLOCKED.toString())) {
            List<Transaction> transactions = getNLastTransactions(transactionResultAfterFraudServiceDto.getAccountId(), transactionResultAfterFraudServiceDto.getCountTransactionForBlocked());
            Account account = accountService.getAccountById(transactionResultAfterFraudServiceDto.getAccountId());
            Collections.reverse(transactions);
            for (Transaction transaction : transactions) {
                if (transaction.getTransactionStatus().equals(TransactionStatusEnum.ACCEPTED)) {
                    account.setFrozenAmount(account.getFrozenAmount().add(transaction.getAmount()));
                    account.setBalance(account.getBalance().add(transaction.getAmount()));
                } else if (transaction.getTransactionStatus().equals(TransactionStatusEnum.REQUESTED)) {
                    if(account.getBalance().compareTo(BigDecimal.ZERO)>=0){
                        account.setFrozenAmount(account.getFrozenAmount().add(transaction.getAmount()));
                    }
                    account.setBalance(account.getBalance().add(transaction.getAmount()));
                }
                transaction.setTransactionStatus(TransactionStatusEnum.BLOCKED);
                updateTransaction(transaction.getId(), transaction);
                log.info("Transaction update: {}", transaction);
            }
            log.info("All transaction updated");
            account.setAccountStatus(AccountStatusEnum.BLOCKED);
            accountService.updateAccount(account.getId(), account);
            log.info("Account updated");
        } else if (transactionResultAfterFraudServiceDto.getTransactionStatus().equals(TransactionStatusEnum.REJECTED.toString())) {
            Account account = accountService.getAccountById(transactionResultAfterFraudServiceDto.getAccountId());
            Transaction transaction = getTransactionById(transactionResultAfterFraudServiceDto.getTransactionId());
            transaction.setTransactionStatus(TransactionStatusEnum.REJECTED);
            account.setBalance(account.getBalance().add(transaction.getAmount()));
            updateTransaction(transaction.getId(), transaction);
            accountService.updateAccount(account.getId(), account);
            //проверь лучше баланс в фрауд сервисе епта
            log.info("Transaction rejected,accoun balance updated");
        } else if (transactionResultAfterFraudServiceDto.getTransactionStatus().equals(TransactionStatusEnum.ACCEPTED.toString())) {
            Transaction transaction = getTransactionById(transactionResultAfterFraudServiceDto.getTransactionId());
            transaction.setTransactionStatus(TransactionStatusEnum.ACCEPTED);
            updateTransaction(transaction.getId(), transaction);
            log.info("Transaction completed successfully.");
        }
    }

    @Override
    public List<Transaction> getNLastTransactions(long accountId, int n) {
        return transactionRepository.findTopNTransactionsByAccountId(accountId, n);
    }

}
