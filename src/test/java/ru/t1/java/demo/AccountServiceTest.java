package ru.t1.java.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.ClientService;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AccountServiceTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private DataSourceErrorLogRepository errorLogRepository;

    @Test
    public void testLogDataSourceErrorAspect() {
        Exception exception = assertThrows(DataIntegrityViolationException.class, () -> {
            accountService.save(new Account());
        });

        assertTrue(errorLogRepository.count() > 0, "Ошибка не была логирована в базе данных");

        DataSourceErrorLog errorLog = errorLogRepository.findAll().get(0);
        assertEquals(exception.getMessage(), errorLog.getMessage());
    }

    @Test
    @Transactional
    public void testSaveAccount() {
        Client client = clientService.findById(2L)
                .orElseThrow(() -> new RuntimeException("Client not found with id 1 "));

        Account account = Account.builder()
                .client(client)
                .accountType(Account.AccountType.DEBIT)
                .balance(BigDecimal.valueOf(1000.0))
                .build();

        Account savedAccount = accountService.save(account);

        assertNotNull(savedAccount.getId());
        assertEquals(client.getId(), savedAccount.getClient().getId());
        assertEquals(Account.AccountType.DEBIT, savedAccount.getAccountType());
        assertEquals(BigDecimal.valueOf(1000.0), savedAccount.getBalance());
    }

    @Test
    @Transactional
    public void testUpdateAccount() {
        Long accountId = 1L;
        Long clientId = 1L;

        Client client = clientService.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        Account existingAccount = accountService.findById(accountId).orElseThrow(() -> new RuntimeException("Account not found "));

        existingAccount.setBalance(BigDecimal.valueOf(333.));
        Account savedAccount = accountService.save(existingAccount);
        assertNotNull(savedAccount.getId());
        assertEquals(accountId, savedAccount.getId());
        assertEquals(client.getId(), savedAccount.getClient().getId());
        assertEquals(Account.AccountType.DEBIT, savedAccount.getAccountType());
        assertEquals(BigDecimal.valueOf(333.), savedAccount.getBalance());

    }
}