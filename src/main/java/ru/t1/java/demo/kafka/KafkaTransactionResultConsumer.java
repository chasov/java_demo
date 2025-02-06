package ru.t1.java.demo.kafka;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.TransactionResultDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.enums.AccountStatus;
import ru.t1.java.demo.model.enums.TransactionStatus;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.TransactionRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaTransactionResultConsumer {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @KafkaListener(
            id = "transaction-result-listener-service1",
            topics = "t1_demo_transaction_result",
            containerFactory = "transactionResultKafkaListenerContainerFactory",
            groupId = "transaction-result-service1"
    )
    @Transactional
    public void listen(@Payload TransactionResultDto resultDto,
                       Acknowledgment ack,
                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                       @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.info("Получено сообщение из топика {} с ключом {}: {}", topic, key, resultDto);

        Optional<Transaction> optTransaction = transactionRepository.findByTransactionId(resultDto.getTransactionId());
        if (optTransaction.isEmpty()) {
            log.error("Транзакция с transactionId {} не найдена", resultDto.getTransactionId());
            ack.acknowledge();
            return;
        }
        Transaction transaction = optTransaction.get();

        Optional<Object> optAccount = accountRepository.findByAccountId(resultDto.getAccountId());
        if (optAccount.isEmpty()) {
            log.error("Счет с accountId {} не найден", resultDto.getAccountId());
            ack.acknowledge();
            return;
        }
        Account account = (Account) optAccount.get();

        String status = String.valueOf(resultDto.getStatus());
        switch (status.toUpperCase()) {
            case "ACCEPTED":
                transaction.setStatus(TransactionStatus.ACCEPTED);
                transactionRepository.save(transaction);
                log.info("Транзакция {} обновлена на статус ACCEPTED", transaction.getTransactionId());
                break;

            case "BLOCKED":
                transaction.setStatus(TransactionStatus.BLOCKED);
                transactionRepository.save(transaction);
                log.info("Транзакция {} обновлена на статус BLOCKED", transaction.getTransactionId());

                account.setStatus(AccountStatus.BLOCKED);
                account.setBalance(account.getBalance().subtract(resultDto.getAmount()));
                account.setFrozenAmount(account.getFrozenAmount().add(resultDto.getAmount()));
                accountRepository.save(account);
                log.info("Счет {} обновлен: статус BLOCKED, баланс уменьшен, frozenAmount увеличен на {}",
                        account.getAccountId(), resultDto.getAmount());
                break;

            case "REJECTED":
                transaction.setStatus(TransactionStatus.REJECTED);
                transactionRepository.save(transaction);
                log.info("Транзакция {} обновлена на статус REJECTED", transaction.getTransactionId());
                account.setBalance(account.getBalance().add(resultDto.getAmount()));
                accountRepository.save(account);
                log.info("Счет {} обновлен: баланс скорректирован на {}", account.getAccountId(), resultDto.getAmount());
                break;

            default:
                log.warn("Неизвестный статус в сообщении: {}", status);
                break;
        }

        ack.acknowledge();
    }
}
