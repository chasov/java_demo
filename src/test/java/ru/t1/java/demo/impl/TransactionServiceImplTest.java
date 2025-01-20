package ru.t1.java.demo.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.t1.java.demo.dto.TransactionDtoResponse;
import ru.t1.java.demo.exception.EntityNotFoundException;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.impl.TransactionServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    void getAllTransactions() {
        final var pageable = PageRequest.of(0, 10);
        final var transaction1 = mock(Transaction.class);
        final var transaction2 = mock(Transaction.class);
        final var transactionDtoResponse1 = mock(TransactionDtoResponse.class);
        final var transactionDtoResponse2 = mock(TransactionDtoResponse.class);

        final var transactionPage = new PageImpl<>(List.of(transaction1, transaction2));

        when(transactionRepository.findAll(pageable)).thenReturn(transactionPage);
        when(modelMapper.map(transaction1, TransactionDtoResponse.class)).thenReturn(transactionDtoResponse1);
        when(modelMapper.map(transaction2, TransactionDtoResponse.class)).thenReturn(transactionDtoResponse2);

        final Page<TransactionDtoResponse> result = transactionService.getAllTransactions(pageable);
        final int numberElements = 2;

        assertNotNull(result);
        assertEquals(numberElements, result.getTotalElements());
        assertEquals(transactionDtoResponse1, result.getContent().get(0));
        assertEquals(transactionDtoResponse2, result.getContent().get(1));

        verify(transactionRepository).findAll(pageable);
        verify(modelMapper, times(2)).map(any(Transaction.class), eq(TransactionDtoResponse.class));
    }

    @Test
    void getTransaction() {
        final Long transactionId = 1L;
        final var transaction = mock(Transaction.class);
        final var transactionDtoResponse = mock(TransactionDtoResponse.class);

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(modelMapper.map(transaction, TransactionDtoResponse.class)).thenReturn(transactionDtoResponse);

        final TransactionDtoResponse result = transactionService.getTransaction(transactionId);

        assertNotNull(result);
        assertEquals(transactionDtoResponse, result);

        verify(transactionRepository).findById(transactionId);
        verify(modelMapper).map(transaction, TransactionDtoResponse.class);
    }

    @Test
    void getTransaction_EntityNotFountException() {
        final Long illegalId = 1L;

        when(transactionRepository.findById(illegalId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> transactionService.getTransaction(illegalId));

        verify(transactionRepository).findById(illegalId);
        verifyNoInteractions(modelMapper);
    }
}