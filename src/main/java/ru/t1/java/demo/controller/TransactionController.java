package ru.t1.java.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.exception.AccountException;
import ru.t1.java.demo.exception.TransactionException;
import ru.t1.java.demo.model.dto.TransactionDto;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.service.impl.AccountServiceImpl;
import ru.t1.java.demo.service.impl.TransactionServiceImpl;
import ru.t1.java.demo.util.TransactionMapper;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/transaction")
public class TransactionController {

    /*private final TransactionRegistrarServiceImpl transactionRegistrarService;
    private final TransactionHandlerService transactionHandlerService;*/

    private final TransactionServiceImpl transactionServiceImpl;
    private final AccountServiceImpl accountService;
    
    private final TransactionMapper transactionMapper;

    /*//@LogException
    @LogDataSourceError
    //@Track
    @GetMapping(value = "/transaction")
    //@HandlingResult
    public void doSomething() throws TransactionException {
        throw new TransactionException("Transaction error");
    }*/


    @PostMapping
    public ResponseEntity<TransactionDto> createTransaction(@RequestBody TransactionDto transactionDto) {
        try {
            Transaction savedTransaction = transactionServiceImpl.addTransaction(transactionMapper.toEntity(transactionDto));
            return new ResponseEntity<>(transactionMapper.toDto(savedTransaction), HttpStatus.CREATED);
        } catch (AccountException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<TransactionDto>> getAllTransaction() {
        return new ResponseEntity<>(transactionServiceImpl.findAll().stream()
                                                                    .map(transactionMapper::toDto)
                                                                    .toList(),
                                    HttpStatus.OK);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionDto> getTransactionById(@PathVariable Long transactionId) {
        try {
                return new ResponseEntity<>(transactionMapper.toDto(transactionServiceImpl.findById(transactionId)),
                                            HttpStatus.OK);
        } catch (TransactionException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<TransactionDto> updateTransaction(@PathVariable Long transactionId, @RequestBody TransactionDto transactionDto) {
        try {
            Transaction updatedTransaction = transactionServiceImpl.updateTransaction(transactionId, transactionDto);
            return new ResponseEntity<>(transactionMapper.toDto(updatedTransaction),
                                        HttpStatus.OK);
        } catch (AccountException | TransactionException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long transactionId) {
        try {
            transactionServiceImpl.delete(transactionId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (TransactionException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionDto>> getAllTransactionsByAccountId(@PathVariable Long accountId) {
        return new ResponseEntity<>(accountService.findAllTransactionsByAccountId(accountId).stream()
                                                                                            .map(transactionMapper::toDto)
                                                                                            .toList(),
                                    HttpStatus.OK);
    }
}
