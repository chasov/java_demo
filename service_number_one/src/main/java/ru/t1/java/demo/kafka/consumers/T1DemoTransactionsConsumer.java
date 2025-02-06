package ru.t1.java.demo.kafka.consumers;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.TransactionalAcceptDTO;
import ru.t1.java.demo.dto.TransactionalDTO;
import ru.t1.java.demo.kafka.producer.KafkaProducer;
import ru.t1.java.demo.model.Transactional;
import ru.t1.java.demo.model.enums.AccountStatus;
import ru.t1.java.demo.service.TransactionalService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class T1DemoTransactionsConsumer {
    private final TransactionalService transactionalService;

    private final KafkaProducer kafkaProducer;


    @KafkaListener(id = "${t1.kafka.consumer.group-id.transactions-group-id}",
            topics = {"${t1.kafka.topic.create_new_transactions}"},
            containerFactory = "kafkaListenerTransactionsContainerFactory")
    public void listener(@Payload List<TransactionalDTO> listTransactions,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {

        try {
            for (TransactionalDTO transactionalDTO : listTransactions) {
                if (transactionalDTO.getAccount().getAccountStatus().equals(AccountStatus.OPEN)) {
                    Transactional transactional = transactionalService.addTransactional(transactionalDTO);
                    TransactionalAcceptDTO acceptDTO = TransactionalAcceptDTO.builder().
                            clientId(transactional.getAccount().getClient().getClientId()).
                            accountId(transactional.getAccount().getAccountId()).
                            transactionalId(transactional.getTransactionalId()).
                            timestamp(transactional.getTimestamp()).
                            priceTransactional(transactional.getPriceTransactional()).
                            balance(transactional.getAccount().getBalance()).
                            build();
                    kafkaProducer.dispatchToAnotherService(acceptDTO);
                }
            }

            log.info("Получено и обработано  сообщение из топика: {}", topic);

        } catch (Exception e) {
            log.error("Ошибка при обработке сообщений. Key: {}, Topic: {}, Ошибка: {}", key, topic, e.getMessage(), e);
        } finally {
            ack.acknowledge();
        }
    }
}
