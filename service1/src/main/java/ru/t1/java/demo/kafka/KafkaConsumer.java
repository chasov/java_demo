package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.AccountDto;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.dto.TransactionResultDto;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.TransactionService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaConsumer {

    private final AccountService accountService;
    private final TransactionService transactionService;

    @KafkaListener(
            id = "${app.kafka.consumer.group-id.accounts}",
            topics = "${app.kafka.topics.accounts}",
            containerFactory = "accountKafkaListenerContainerFactory"
    )
    public void listenAccounts(@Payload List<AccountDto> accounts,
                               Acknowledgment ack,
                               @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                               @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        try {
            log.info("Получено {} запись из топика аккаунтов", accounts.size());
            accounts.forEach(accountService::saveAccount);
        } catch (Exception e) {
            log.error("Ошибка обработки сообщений из топика аккаунтов: {}", e.getMessage(), e);
        } finally {
            ack.acknowledge();
        }
    }

    @KafkaListener(
            id = "${app.kafka.consumer.group-id.transactions}",
            topics = "${app.kafka.topics.transactions}",
            containerFactory = "transactionKafkaListenerContainerFactory"
    )
    public void listenTransactions(@Payload List<TransactionDto> transactions,
                                   Acknowledgment ack,
                                   @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                   @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        try {
            log.info("Получено {} записей из топика транзакций", transactions.size());
            //transactions.forEach(transactionService::saveTransaction);
            transactions.forEach(transactionService::processTransaction);
        } catch (Exception e) {
            log.error("Ошибка обработки сообщений из топика транзакций: {}", e.getMessage(), e);
        } finally {
            ack.acknowledge();
        }
    }

    @KafkaListener(
            id = "${app.kafka.consumer.group-id.transaction-results}",
            topics = "${app.kafka.topics.transaction-result}",
            containerFactory = "accountKafkaListenerContainerFactory"
    )
    public void listenTransactionResults(@Payload List<TransactionResultDto> resultDtoList,
                                         Acknowledgment ack,
                                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        try {
            log.info("Получено {} записей из топика результатов обработки", resultDtoList.size());

        } catch (Exception e) {
            log.error("Ошибка обработки сообщений из топика результатов обработки: {}", e.getMessage(), e);
        } finally {
            ack.acknowledge();
        }
    }
}