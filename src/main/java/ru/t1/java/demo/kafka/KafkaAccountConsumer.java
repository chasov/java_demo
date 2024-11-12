package ru.t1.java.demo.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.dto.AccountDTO;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.util.AccountMapper;

@Slf4j
@Service
public class KafkaAccountConsumer {
    @Autowired
    private AccountService accountService;
    @Autowired
    private ClientRepository clientRepository;

    @KafkaListener(id = "${spring.kafka.consumer.group-id}-accounts",
            topics = "${spring.kafka.topic.accounts}",
            containerFactory = "accountKafkaListenerContainerFactory")
    @Transactional
    public void listener(@Payload AccountDTO accountDTO,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.debug("Account consumer: Обработка новых сообщений. Topic: {}, Key: {}", topic, key);
        log.debug("Получено сообщение: {}", accountDTO);

        try {

                Client client = clientRepository.findById(accountDTO.getClientId())
                        .orElseThrow(() -> new IllegalArgumentException("Клиент с id " + accountDTO.getClientId() + " не найден"));
                Account account = AccountMapper.toEntity(accountDTO, client);
                accountService.createAccount(account, client.getId());

        } catch (Exception e) {
                 log.error("Ошибка обработки сообщений для аккаунтов: {}", accountDTO.getClientId(), e);
        } finally {
            ack.acknowledge();
        }
        log.debug("Account consumer: записи обработаны");
    }


}
