package ru.t1.java.demo.controller.transaction;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.t1.java.demo.model.dto.TransactionDto;
import ru.t1.java.demo.service.impl.transaction.TransactionServiceImpl;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class TransactionController {

    private final TransactionServiceImpl transactionService;

    @PostMapping("/transaction")
    public TransactionDto conductTransaction(@RequestBody TransactionDto transactionDto) {
        return transactionService.conductTransaction(transactionDto);
    }

    @GetMapping("/transaction/{transactionId}")
    public TransactionDto getTransaction(@PathVariable Long transactionId) {
        return transactionService.getTransaction(transactionId);
    }

    @DeleteMapping("/transaction/{transactionId}")
    public ResponseEntity<Void> cancelTransaction(@PathVariable Long transactionId) {
        transactionService.cancelTransaction(transactionId);
        return ResponseEntity.ok().build();
    }
}
