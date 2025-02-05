package ru.t1.java.demo.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.transaction.dto.TransactionDto;
import ru.t1.java.demo.transaction.model.Transaction;
import ru.t1.java.demo.transaction.service.TransactionService;

import java.util.Collection;
import java.util.Set;


@Slf4j
@Component
public class KafkaTransactionConsumer {

    private final TransactionService transactionService;

    @Autowired
    public KafkaTransactionConsumer(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @KafkaListener(id = "${t1.kafka.consumer.group-id-transaction}", topics = "t1_demo_transactions", containerFactory = "kafkaListenerContainerFactory")
    public void listener(@Payload Collection<TransactionDto> transactionDtos,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.info("TransactionDtos: Обработка новых сообщений");
        try {
            log.info("Topic: {}, Key: {}", topic, key);
            Set<Transaction> transactions = transactionService.dtoToTransaction(transactionDtos);
            try{
                if(!transactions.isEmpty()){
                   transactionService.processAndSendTransactionAccept(transactions);
                }
                log.info("Transactions complete send t1_demo_transaction_accept");
            }
            catch (Exception e){
                e.printStackTrace();
            }
            log.info("Transactions saved to database");
        }
        catch (Exception e){
            log.error("Failed to send topic t1_demo_transactions " + e.getMessage());
            e.printStackTrace();
        }
        finally {
            ack.acknowledge();
        }
        log.info("TransactionDtos: записи обработаны");
    }

}
