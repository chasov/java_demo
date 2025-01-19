package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.util.TransactionMapper;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Override
    public TransactionDto save(TransactionDto dto) {

        transactionRepository.save(TransactionMapper.toEntity(dto));

        return dto;
    }

    @Override
    public TransactionDto patchById(Long transactionId, TransactionDto dto) {
        return null;
    }

    @Override
    public List<TransactionDto> getAllById(Long transactionId) {
        return null;
    }

    @Override
    public TransactionDto getById(Long transactionId) {
        return null;
    }

    @Override
    public void deleteById(Long transactionId) {

    }
}
