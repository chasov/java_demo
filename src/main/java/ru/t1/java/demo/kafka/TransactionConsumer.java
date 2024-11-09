package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.dto.TransactionDto;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.util.TransactionMapper;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionConsumer {

    private final AccountService accountService;
    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;

    private final TransactionProducer transactionProducer;

    @KafkaListener(topics = "${t1.kafka.topic.transactions_adding}",
            groupId = "${t1.kafka.consumer.group-id-transaction}",
            containerFactory = "transactionKafkaListenerContainerFactory")
    public void consumeTransactionMessage(TransactionDto transactionDto) {
        log.info("Received message from t1_demo_transactions: {}", transactionDto);


        try {
            Account account = accountService.findById(transactionDto.getAccountId())
                    .orElseThrow(() -> new RuntimeException("Account not found with ID: " + transactionDto.getAccountId()));

            if (account.getStatus() == Account.Status.OPEN) {
                Transaction transaction = Transaction.builder()
                        .transactionId(transactionDto.getTransactionId() != null ? transactionDto.getTransactionId() : UUID.randomUUID())
                        .account(account)
                        .amount(transactionDto.getAmount())
                        .transactionTime(LocalDateTime.now())
                        .timestamp(LocalDateTime.now())
                        .status(Transaction.Status.REQUESTED)
                        .build();

                Transaction savedTransaction = transactionService.save(transaction);

                Account updatedAccount = accountService.updateBalance(account, transactionDto.getAmount());

                transactionProducer.sendTransactionConfirmation(
                        account.getClient().getId(),
                        account.getAccountId(),
                        savedTransaction.getTransactionId(),
                        LocalDateTime.now(),
                        transactionDto.getAmount(),
                        account.getBalance()
                );

                log.info("Transaction processed successfully, transactionId: {}", savedTransaction.getTransactionId());
            } else {
                log.warn("Account with ID {} has status: {}. Transaction rejected.", transactionDto.getAccountId(), account.getStatus());
            }

        } catch (Exception e) {
            log.error("Error processing transaction: {}", e.getMessage());
        }
    }
}