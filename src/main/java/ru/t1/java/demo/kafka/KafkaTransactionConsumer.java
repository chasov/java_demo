package ru.t1.java.demo.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.TransactionDTO;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.util.TransactionMapper;

import java.util.List;

@Slf4j
@Service
public class KafkaTransactionConsumer {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountRepository accountRepository;

    @KafkaListener(id = "${spring.kafka.consumer.group-id}-transactions",
            topics =  {"${spring.kafka.topic.transactions}", "${spring.kafka.topic.transactionsAccept}","${spring.kafka.topic.transactionsResult}"},
            containerFactory = "transactionKafkaListenerContainerFactory")
    public void listener(@Payload List<TransactionDTO> messageList,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.debug("Transaction consumer: Обработка новых сообщений");

        try {
            for (TransactionDTO transactionDTO : messageList) {
                     Account account = accountRepository.findAccountByGlobalAccountId(transactionDTO.getGlobalAccountId())
                        .orElseThrow(() -> new IllegalArgumentException("Аккаунт с id " + transactionDTO.getGlobalAccountId() + " не найден"));


                Transaction transaction = TransactionMapper.toEntity(transactionDTO, account);
                 transactionService.operate(topic, transaction);
            }
        } catch (Exception e) {
            log.error("Ошибка обработки сообщений для транзакций: {}", messageList, e);
        } finally {
            ack.acknowledge();
        }

        log.debug("Transaction consumer: записи обработаны");
    }
}