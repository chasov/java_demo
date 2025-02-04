package ru.t1.java.demo.controller.transaction;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.t1.java.demo.aop.annotation.Metric;
import ru.t1.java.demo.kafka.producer.KafkaTransactionProducer;
import ru.t1.java.demo.model.dto.TransactionDto;
import ru.t1.java.demo.service.transaction.TransactionService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transaction")
public class TransactionController {

    private final TransactionService transactionService;
    private final KafkaTransactionProducer<TransactionDto> kafkaTransactionProducer;

    @Metric
    @PostMapping
    public void conductTransaction(@RequestBody TransactionDto transaction) {
        try {
            kafkaTransactionProducer.send(transaction);
        } catch (Exception e) {
            throw new RuntimeException("Error sending transaction");
        }
    }

    @Metric
    @GetMapping("/{transactionId}")
    public TransactionDto getTransaction(@PathVariable Long transactionId) {
        return transactionService.getTransaction(transactionId);
    }

    @Metric
    @DeleteMapping("/{transactionId}")
    public void cancelTransaction(@PathVariable Long transactionId) {
        transactionService.cancelTransaction(transactionId);
    }
}
