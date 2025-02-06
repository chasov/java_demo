package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.kafka.producer.KafkaTransactionProducer;
import ru.t1.java.demo.service.TransactionService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transactions")
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;

    private final KafkaTransactionProducer transactionProducer;

    @GetMapping("/{id}")
    public TransactionDto getTransactionById(@PathVariable("id") Long id) {
        return transactionService.getById(id);
    }

    @GetMapping
    public Collection<TransactionDto> getAllTransactions() {
        return transactionService.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionDto createTransaction(@RequestBody TransactionDto transactionDto) {
        return transactionService.create(transactionDto);
    }

    /**
     * Method and endpoint to create Transaction with sending message to Kafka
     *
     * @param @RequestBody transactionDto
     * @return ResponseEntity transactionDto
     */
    @PostMapping("/send")
    @ResponseStatus(HttpStatus.OK)
    public TransactionDto sendTransactionRequest(@RequestBody TransactionDto transactionDto) {
        transactionProducer.send(transactionDto);
        return transactionDto;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteTransaction(@PathVariable("id") Long transactionId) {
        transactionService.delete(transactionId);
    }
}
