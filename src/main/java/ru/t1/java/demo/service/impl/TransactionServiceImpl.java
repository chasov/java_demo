package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.annotation.LogDataSourceError;
import ru.t1.java.demo.dto.TransactionDtoResponse;
import ru.t1.java.demo.exception.EntityNotFoundException;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.TransactionService;


@Service
@RequiredArgsConstructor
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    @LogDataSourceError
    @Override
    public Page<TransactionDtoResponse> getAllTransactions(Pageable pageable) {
        return transactionRepository.findAll(pageable).
                map(transaction -> modelMapper.map(transaction, TransactionDtoResponse.class));
    }

    @Transactional(readOnly = true)
    @LogDataSourceError
    @Override
    public TransactionDtoResponse getTransaction(Long id) {
        return transactionRepository.findById(id)
                .map(transaction -> modelMapper.map(transaction, TransactionDtoResponse.class))
                .orElseThrow(() -> new EntityNotFoundException(Transaction.class, id));
    }
}
