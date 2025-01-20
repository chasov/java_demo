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
import ru.t1.java.demo.dto.AccountDtoRequest;
import ru.t1.java.demo.dto.AccountDtoResponse;
import ru.t1.java.demo.dto.ClientDto;
import ru.t1.java.demo.exception.EntityNotFoundException;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.enums.AccountType;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.service.ClientService;
import ru.t1.java.demo.service.impl.AccountServiceImpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static ru.t1.java.demo.model.enums.AccountType.CREDIT;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @InjectMocks
    private AccountServiceImpl accountService;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private ClientService clientService;
    @Mock
    private ModelMapper modelMapper;

    @Test
    void testGetAllAccounts() {
        final var accountDtoResponse = getAccountDtoResponse();
        final var account = getAccount();
        final var accounts = List.of(account);
        final var pageable = PageRequest.of(0, 10);
        final var accountPage = new PageImpl<>(accounts, pageable, 1);

        when(accountRepository.findAll(pageable)).thenReturn(accountPage);
        when(modelMapper.map(account, AccountDtoResponse.class)).thenReturn(accountDtoResponse);

        final Page<AccountDtoResponse> result = accountService.getAllAccounts(pageable);
        final int numberElementResult = 1;

        assertNotNull(result);
        assertEquals(numberElementResult, result.getTotalElements());
        assertEquals(accountDtoResponse, result.getContent().get(0));

        verify(accountRepository).findAll(pageable);
    }

    @Test
    void testGetAccount_EntityNotFoundException() {
        final Long accountId = 1L;
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> accountService.getAccount(accountId));

        verify(accountRepository).findById(accountId);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void testGetAccount() {
        final var accountId = 1L;
        final var account = getAccount();
        final var accountDtoResponse = getAccountDtoResponse();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(modelMapper.map(account, AccountDtoResponse.class)).thenReturn(accountDtoResponse);

        final var result = accountService.getAccount(accountId);

        assertNotNull(result);
        assertEquals(accountDtoResponse, result);
        verify(accountRepository).findById(accountId);
    }

    @Test
    void testCreateAccount() {
        final Long resultId = 1L;
        final var accountDtoRequest = new AccountDtoRequest(resultId, AccountType.CREDIT);
        final var client = new Client();
        final var mockAccount = mock(Account.class);

        when(mockAccount.getId()).thenReturn(resultId);
        when(clientService.getClient(accountDtoRequest.clientId())).thenReturn(client);
        when(accountRepository.save(any(Account.class))).thenReturn(mockAccount);

        final Long accountId = accountService.createAccount(accountDtoRequest);

        assertNotNull(accountId);
        assertEquals(resultId, accountId);
        verify(accountRepository).save(any(Account.class));
    }

    private Account getAccount() {
        return Account.builder()
                .accountType(CREDIT)
                .client(new Client())
                .balance(BigDecimal.ZERO)
                .build();
    }

    private AccountDtoResponse getAccountDtoResponse() {
        AccountDtoResponse accountDtoResponse = new AccountDtoResponse();
        accountDtoResponse.setClient(new ClientDto());
        accountDtoResponse.setAccountType(CREDIT);
        return accountDtoResponse;
    }
}