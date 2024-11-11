package ru.t1.java.demo.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Service
public class TransactionResultConsumer {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @KafkaListener(topics = "${t1.kafka.topic.transactions_result}",
            groupId = "${t1.kafka.consumer.group-id-result}",
            containerFactory = "transactionKafkaListenerContainerFactory")
    @Transactional
    public void consumeResultMessage(TransactionDto result) {
        log.info("Received message from t1_demo_transaction_result: {}", result);

        switch (result.getStatus()) {
            case ACCEPTED:
                handleAcceptedTransaction(result);
                break;
            case BLOCKED:
                handleBlockedTransaction(result);
                break;
            case REJECTED:
                handleRejectedTransaction(result);
                break;
            default:
                log.error("Unknown transaction status: " + result.getStatus());
        }
    }

    private void handleAcceptedTransaction(TransactionDto result) {
        Optional<Transaction> transactionOptional = transactionRepository.findByTransactionId(result.getTransactionId());
        if (transactionOptional.isPresent()) {
            Transaction transaction = transactionOptional.get();
            transaction.setStatus(Transaction.Status.ACCEPTED);
            transactionRepository.save(transaction);
        } else {
            log.error("Transaction with UUID " + result.getTransactionId() + " not found.");
        }
    }

    private void handleBlockedTransaction(TransactionDto result) {
        Optional<Transaction> transactionOptional = transactionRepository.findByTransactionId(result.getTransactionId());
        if (transactionOptional.isPresent()) {
            Transaction transaction = transactionOptional.get();

            transaction.setStatus(Transaction.Status.BLOCKED);
            transactionRepository.save(transaction);


            Optional<Account> accountOptional = accountRepository.findById(transaction.getAccount().getId());
            if (accountOptional.isPresent()) {
                Account account = accountOptional.get();

                account.setStatus(Account.Status.BLOCKED);


                BigDecimal frozenAmount = account.getFrozenAmount().add(result.getAmount());
                account.setFrozenAmount(frozenAmount);
                accountRepository.save(account);
            } else {
                System.out.println("Account with ID " + transaction.getAccount().getId() + " not found.");
            }
        } else {
            System.out.println("Transaction with UUID " + result.getTransactionId() + "  not found.");
        }
    }

    private void handleRejectedTransaction(TransactionDto result) {
        Optional<Transaction> transactionOptional = transactionRepository.findByTransactionId(result.getTransactionId());
        if (transactionOptional.isPresent()) {
            Transaction transaction = transactionOptional.get();

            transaction.setStatus(Transaction.Status.REJECTED);
            transactionRepository.save(transaction);


            Optional<Account> accountOptional = accountRepository.findById(transaction.getAccount().getId());
            if (accountOptional.isPresent()) {
                Account account = accountOptional.get();


                BigDecimal updatedBalance = account.getBalance().add(result.getAmount());
                account.setBalance(updatedBalance);

                accountRepository.save(account);
            } else {
                System.out.println("Account with ID " + transaction.getAccount().getId() + " not found");
            }
        } else {
            System.out.println("Transaction with UUID " + result.getTransactionId() + " not found.");
        }
    }
}
