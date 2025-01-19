package ru.t1.java.demo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.annotation.LogDataSourceError;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.exception.ResourceNotFoundException;
import ru.t1.java.demo.exception.TransactionException;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.util.TransactionMapper;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService implements CRUDService<TransactionDto> {

    private final TransactionRepository transactionRepository;

    private final AccountRepository accountRepository;

    private final TransactionMapper transactionMapper;

    /**Transfers money between two accounts
     *
      * @param transactionDto with fields: accountFromId,
     *                                     accountToId,
     *                                     amount
     *
     * @return transactionDto, updates accounts balances
     */
    @Transactional
    @Override
    @LogDataSourceError
    public TransactionDto create(TransactionDto transactionDto) {
        log.info("Starting new transaction");
        Transaction transaction = transactionMapper.toEntity(transactionDto);
        Account accountFrom = accountRepository.findById(transaction.getAccountFrom().getId()).orElseThrow(
                () -> new ResourceNotFoundException("Account with given id " +
                        transaction.getAccountFrom().getId() + " is not exists")
        );
        Account accountTo = accountRepository.findById(transaction.getAccountTo().getId()).orElseThrow(
                () -> new ResourceNotFoundException("Account with given id " +
                        transaction.getAccountTo().getId() + " is not exists")
        );

        if (accountFrom.getBalance().longValue() < transaction.getAmount().longValue()) {
            log.info("There are insufficient funds in the account with ID " + accountFrom.getId());
            throw new TransactionException(
                    "There are insufficient funds in the account with ID " + accountFrom.getId());
        }

        accountFrom.setBalance(BigDecimal.valueOf
                (accountFrom.getBalance().longValue() - transaction.getAmount().longValue()));
        accountTo.setBalance(BigDecimal.valueOf
                (accountTo.getBalance().longValue() + transaction.getAmount().longValue()));

        transaction.setAccountFrom(accountFrom);
        transaction.setAccountTo(accountTo);

        log.info("Transaction between {} and {} account completed successfully",
                accountFrom.getId(), accountTo.getId());

        Transaction savedTransaction = transactionRepository.save(transaction);
        return transactionMapper.toDto(savedTransaction);
    }

    @Override
    @LogDataSourceError
    public TransactionDto getById(Long id) {
        log.info("Transaction get by ID: {}", id);
        Transaction transaction = transactionRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException(
                        "Transaction with given id: " + id + " is not exists"));
        return transactionMapper.toDto(transaction);
    }

    @Override
    @LogDataSourceError
    public Collection<TransactionDto> getAll() {
        log.info("Getting all transactions");
        List<Transaction> transactionList = transactionRepository.findAll();
        return transactionList.stream().map(transactionMapper::toDto)
                .toList();
    }

    @Override
    @Deprecated
    public TransactionDto update(Long id, TransactionDto item) {
        return null;
    }

    @Override
    @Transactional
    @LogDataSourceError
    public void delete(Long transactionId) {
        log.info("Deleting transaction with ID: {}", transactionId);
        Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(
                () -> new ResourceNotFoundException(
                        "Transaction with given id: " + transactionId + " is not exists")
        );
        transactionRepository.deleteById(transactionId);
        log.info("Transaction with ID: {} deleted successfully!", transactionId);
    }
}
