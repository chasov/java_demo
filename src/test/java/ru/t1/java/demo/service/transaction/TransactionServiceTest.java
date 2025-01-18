package ru.t1.java.demo.service.transaction;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.t1.java.demo.exception.account.AccountException;
import ru.t1.java.demo.exception.transaction.TransactionException;
import ru.t1.java.demo.model.dto.AccountDto;
import ru.t1.java.demo.model.dto.TransactionDto;
import ru.t1.java.demo.model.entity.Transaction;
import ru.t1.java.demo.model.enums.AccountType;
import ru.t1.java.demo.model.errorlog.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;
import ru.t1.java.demo.repository.TransactionsRepository;
import ru.t1.java.demo.service.impl.account.AccountServiceImpl;
import ru.t1.java.demo.service.impl.transaction.TransactionServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Testcontainers
public class TransactionServiceTest {

    @Autowired
    private AccountServiceImpl accountService;

    @Autowired
    private TransactionServiceImpl transactionService;

    @Autowired
    private DataSourceErrorLogRepository dataSourceErrorLogRepository;

    @Autowired
    private TransactionsRepository transactionsRepository;

    @Container
    public static PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:11.13");

    @DynamicPropertySource
    public static void postgresqlProperties(DynamicPropertyRegistry registry) {
        POSTGRESQL_CONTAINER.start();

        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testConductTransaction() {
        accountService.createAccount(prepareAccountDto(2L));
        transactionService.conductTransaction(prepareTransactionDto(1L));
    }

    @Test
    public void testGetTransaction() {
        accountService.createAccount(prepareAccountDto(2L));
        TransactionDto transactionDto = prepareTransactionDto(1L);

        transactionService.conductTransaction(transactionDto);

        Transaction transaction = transactionsRepository.findById(transactionDto.getId()).orElse(null);

        assertThat(transaction).isNotNull();
        assertThat(transaction.getId()).isEqualTo(transactionDto.getId());
    }

    @Test
    public void testCancelTransaction() {
        accountService.createAccount(prepareAccountDto(2L));
        TransactionDto transactionDto = prepareTransactionDto(1L);
        transactionService.conductTransaction(transactionDto);
        transactionService.cancelTransaction(transactionDto.getId());

        assertThrows(TransactionException.class,
                () -> transactionService.getTransaction(transactionDto.getId()));
        List<DataSourceErrorLog> errorLogs = dataSourceErrorLogRepository.findAll();

        assertFalse(errorLogs.isEmpty(), "No error log found");
        DataSourceErrorLog latestErrorLog = errorLogs.get(errorLogs.size() - 1);
        assertNotNull(latestErrorLog, "Latest error log should not be null");
        assertEquals("Transaction not found", latestErrorLog.getMessage(), "Log message is incorrect");
    }

    @Test
    public void testConductTransactionIfAccountDoesNotExist() {
        String exceptionMessage = "Account not found";

        assertThrows(AccountException.class,
                () -> transactionService.conductTransaction(prepareTransactionDto(0L)));
        List<DataSourceErrorLog> errorLogs = dataSourceErrorLogRepository.findAll();


        assertFalse(errorLogs.isEmpty(), "No error log found");
        DataSourceErrorLog latestErrorLog = errorLogs.get(errorLogs.size() - 1);
        assertNotNull(latestErrorLog, "Latest error log should not be null");
        assertEquals(exceptionMessage, latestErrorLog.getMessage(), "Log message is incorrect");
    }

    @Test
    public void testGetTransactionIfTransactionDoesNotExist() {
        String exceptionMessage = "Transaction not found";

        assertThrows(TransactionException.class,
                () -> transactionService.getTransaction(0L));
        List<DataSourceErrorLog> errorLogs = dataSourceErrorLogRepository.findAll();

        assertFalse(errorLogs.isEmpty(), "No error log found");
        DataSourceErrorLog latestErrorLog = errorLogs.get(errorLogs.size() - 1);
        assertNotNull(latestErrorLog, "Latest error log should not be null");
        assertEquals(exceptionMessage, latestErrorLog.getMessage(), "Log message is incorrect");
    }

    @Test
    public void testGetTransactionIfTransactionsIdIsNull() {
        String exceptionMessage = "The given id must not be null";

        TransactionDto transactionDto = TransactionDto.builder().build();

        assertThrows(InvalidDataAccessApiUsageException.class,
                () -> transactionService.getTransaction(transactionDto.getId()));
        List<DataSourceErrorLog> errorLogs = dataSourceErrorLogRepository.findAll();

        assertFalse(errorLogs.isEmpty(), "No error log found");
        DataSourceErrorLog latestErrorLog = errorLogs.get(errorLogs.size() - 1);
        assertNotNull(latestErrorLog, "Latest error log should not be null");
        assertEquals(exceptionMessage, latestErrorLog.getMessage(), "Log message is incorrect");
    }

    private AccountDto prepareAccountDto(Long clientId) {
        return AccountDto.builder()
                .id(1L)
                .clientId(clientId)
                .type(AccountType.DEBIT_ACCOUNT)
                .balance(new BigDecimal("0.00"))
                .build();
    }

    private TransactionDto prepareTransactionDto(Long accountId) {
        return TransactionDto.builder()
                .id(1L)
                .accountId(accountId)
                .amount(new BigDecimal("10000.00"))
                .createdAt(LocalDateTime.now())
                .build();
    }
}
