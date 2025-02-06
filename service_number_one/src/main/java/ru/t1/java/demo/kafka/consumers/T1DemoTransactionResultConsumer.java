package ru.t1.java.demo.kafka.consumers;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.TransactionResultDTO;
import ru.t1.java.demo.exception.TransactionalException;
import ru.t1.java.demo.model.Transactional;
import ru.t1.java.demo.model.enums.TransactionalStatus;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.TransactionalService;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class T1DemoTransactionResultConsumer {

    private final TransactionalService transactionalService;
    private final AccountService accountService;

    @KafkaListener(id = "${t1.kafka.consumer.group-id.transactional-result-id}",
            topics = {"${t1.kafka.topic.transactional-result}"},
            containerFactory = "kafkaListenerTransactionResponseContainerFactory")
    public void listener(@Payload List<TransactionResultDTO> listTransactions,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        try {
            for (TransactionResultDTO transactionResultDTO : listTransactions) {
                if (transactionResultDTO.getStatus().equalsIgnoreCase("ACCEPTED")) {
                    transactionalService.patchTransactional(transactionResultDTO.getTransactionalId(), TransactionalStatus.ACCEPTED);
                } else if (transactionResultDTO.getStatus().equalsIgnoreCase("BLOCKED")) {
                    Transactional transactional = transactionalService.patchTransactional(transactionResultDTO.getTransactionalId(), TransactionalStatus.BLOCKED);
                    BigDecimal bigDecimal = transactional.getPriceTransactional();
                    accountService.patchAccount(transactionResultDTO.getAccountId(), bigDecimal, bigDecimal);
                } else if (transactionResultDTO.getStatus().equalsIgnoreCase("REJECTED")) {
                    Transactional transactional = transactionalService.patchTransactional(transactionResultDTO.getTransactionalId(), TransactionalStatus.REJECTED);
                    accountService.
                            patchAccount(transactionResultDTO.getAccountId(), transactional.getPriceTransactional(), BigDecimal.ZERO);
                } else {
                    throw new TransactionalException("Неверный статус транзакции");
                }
                log.info("Получено и обработано  сообщение из топика: {}", topic);
            }
        } catch (Exception e) {
            log.error("Ошибка при обработке сообщений. Key: {}, Topic: {}, Ошибка: {}", key, topic, e.getMessage(), e);
        } finally {
            ack.acknowledge();
        }
    }
}

