package ru.t1.java.demo.kafka.consumer;

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
//import ru.t1.java.demo.dto.TransactionResultDto;
import ru.t1.dto.TransactionResultDto;
import ru.t1.java.demo.exception.ResourceNotFoundException;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.enums.TransactionStatus;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.util.AccountMapper;
import ru.t1.java.demo.util.TransactionMapper;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaTransactionResultConsumer {

    private final TransactionService transactionService;

    private final TransactionRepository transactionRepository;

    private final TransactionMapper transactionMapper;

    private final AccountMapper accountMapper;

    private final AccountService accountService;

    private final AccountRepository accountRepository;

    @KafkaListener(topics = "${t1.kafka.topic.transactions-result}",
            groupId = "${t1.kafka.consumer.transaction-result-group-id}",
            containerFactory = "kafkaListenerContainerFactoryTransactionResult")
    public void transactionListener(@Payload List<TransactionResultDto> messages,
                                    Acknowledgment ack,
                                    @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                    @Header(KafkaHeaders.RECEIVED_KEY) String key
    ) {
        log.info("Topic: {}, Key: {} start proceeding", topic, key);
         try {
             messages.forEach(dto -> {
/*                 TransactionResultDto resultDto = TransactionResultDto.builder()
                         .accountId(dto.getAccountId())
                         .transactionId(dto.getTransactionId())
                         .transactionStatus(dto.getTransactionStatus())
                         .build();*/
                 TransactionResultDto resultDto = new TransactionResultDto();
                 resultDto.setTransactionId(dto.getTransactionId());
                 resultDto.setTransactionStatus(dto.getTransactionStatus());
                 resultDto.setAccountId(dto.getAccountId());


                 TransactionDto transactionDtoToSave =
                         transactionMapper.toDto(transactionRepository.findTransactionByTransactionId(
                                 resultDto.getTransactionId()).orElseThrow(
                                         () -> new ResourceNotFoundException(
                                                 "Transaction not found with id: " + resultDto.getTransactionId())));
                 AccountDto accountDtoFromToSave =
                         accountMapper.toDto(accountRepository.findByAccountId(resultDto.getAccountId()).orElseThrow(
                                 () -> new ResourceNotFoundException(
                                         "Account not found with id: " + resultDto.getAccountId())
                         ));

                 AccountDto accountDtoToToSave = accountMapper.toDto(
                         accountRepository.findById(transactionDtoToSave.getAccountToId()).orElseThrow(
                                 () -> new ResourceNotFoundException(
                                         "Account with given id: " + transactionDtoToSave.getAccountToId()
                                                 + " is not exists")
                         ));


                 Transaction transactionToSave =
                         transactionRepository.findTransactionByTransactionId(
                                 resultDto.getTransactionId()).orElseThrow(
                                         () -> new ResourceNotFoundException("Transaction not found " +
                                                 resultDto.getTransactionId())
                         );
/*                 AccountDto accountToSave =
                         accountMapper.toDto(accountRepository.findByAccountId(resultDto.getAccountId()));

                 AccountDto accountToToSave = accountMapper.toDto(
                         accountRepository.findById(transactionToSave.getAccountToId()).orElseThrow(
                                 () -> new ResourceNotFoundException(
                                         "Account with given id: " + transactionToSave.getAccountToId()
                                                 + " is not exists")
                         ));

                 transactionToSave.setStatus(TransactionStatus.valueOf(resultDto.getTransactionStatus()));
                 //transactionService.saveTransaction(transactionToSave);
                 transactionRepository.save(transactionToSave);*/

                 log.error("-----------------FUCKIIING!!!---------------");

                transactionService.processTransactionResult(resultDto, transactionDtoToSave,
                        accountDtoToToSave, accountDtoFromToSave);

             });
         } finally {
             ack.acknowledge();
         }

    }
}
