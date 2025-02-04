package ru.t1.java.demo.service.account;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.t1.java.demo.exception.client.ClientException;
import ru.t1.java.demo.exception.account.AccountException;
import ru.t1.java.demo.model.dto.account.AccountDto;
import ru.t1.java.demo.model.enums.AccountType;
import ru.t1.java.demo.model.entity.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Testcontainers
public class AccountServiceIntegrationTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private DataSourceErrorLogRepository dataSourceErrorLogRepository;

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
    public void testCreateAccount() {
        accountService.createAccount(prepareData(2L));
    }

    @Test
    public void testGetAccount() {
        AccountDto accountDto = prepareData(2L);

        accountDto = accountService.createAccount(accountDto);
        AccountDto actualDto = accountService.getAccount(accountDto.getId());

        assertEquals(accountDto, actualDto);
    }

    @Test
    public void testDeleteAccount() {
        AccountDto accountDto = prepareData(2L);

        accountService.createAccount(accountDto);
        accountService.deleteAccount(accountDto.getId());
        assertThrows(AccountException.class,
                () -> accountService.getAccount(accountDto.getId()));
        List<DataSourceErrorLog> errorLogs = dataSourceErrorLogRepository.findAll();


        assertFalse(errorLogs.isEmpty(), "No error log found");
        DataSourceErrorLog latestErrorLog = errorLogs.get(errorLogs.size() - 1);
        assertNotNull(latestErrorLog, "Error log should not be null");
        assertEquals("Account not found", latestErrorLog.getMessage(), "Log message is incorrect");
    }

    @Test
    public void testCreateAccountIfClientDoesNotExist() {
        String exceptionMessage = "Client not found";

        assertThrows(ClientException.class,
                () -> accountService.createAccount(prepareData(0L)));
        List<DataSourceErrorLog> errorLogs = dataSourceErrorLogRepository.findAll();


        assertFalse(errorLogs.isEmpty(), "No error log found");
        DataSourceErrorLog latestErrorLog = errorLogs.get(errorLogs.size() - 1);
        assertNotNull(latestErrorLog, "Error log should not be null");
        assertEquals(exceptionMessage, latestErrorLog.getMessage(), "Log message is incorrect");
    }

    @Test
    public void testGetAccountIfAccountDoesNotExist() {
        String exceptionMessage = "Account not found";

        assertThrows(AccountException.class,
                () -> accountService.getAccount(0L));
        List<DataSourceErrorLog> errorLogs = dataSourceErrorLogRepository.findAll();

        assertFalse(errorLogs.isEmpty(), "No error log found");
        DataSourceErrorLog latestErrorLog = errorLogs.get(errorLogs.size() - 1);
        assertNotNull(latestErrorLog, "Error log should not be null");
        assertEquals(exceptionMessage, latestErrorLog.getMessage(), "Log message is incorrect");
    }

    @Test
    public void testGetAccountIfAccountsIdIsNull() {
        String exceptionMessage = "The given id must not be null";

        AccountDto accountDto = AccountDto.builder().build();

        assertThrows(InvalidDataAccessApiUsageException.class,
                () -> accountService.getAccount(accountDto.getId()));
        List<DataSourceErrorLog> errorLogs = dataSourceErrorLogRepository.findAll();


        assertFalse(errorLogs.isEmpty(), "No error log found");
        DataSourceErrorLog latestErrorLog = errorLogs.get(errorLogs.size() - 1);
        assertNotNull(latestErrorLog, "Error log should not be null");
        assertEquals(exceptionMessage, latestErrorLog.getMessage(), "Log message is incorrect");
    }

    private AccountDto prepareData(Long clientId) {
        return AccountDto.builder()
                .id(1L)
                .clientId(clientId)
                .type(AccountType.DEBIT_ACCOUNT)
                .balance(new BigDecimal("0.00"))
                .build();
    }
}
