package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.annotation.LogDataSourceError;
import ru.t1.java.demo.dto.AccountDtoRequest;
import ru.t1.java.demo.dto.AccountDtoResponse;
import ru.t1.java.demo.exception.EntityNotFoundException;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.service.ClientService;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    private final ClientService clientService;

    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    @LogDataSourceError
    @Override
    public Page<AccountDtoResponse> getAllAccounts(Pageable pageable) {
        return accountRepository.findAll(pageable)
                .map(account -> modelMapper.map(account, AccountDtoResponse.class));

    }

    @Transactional(readOnly = true)
    @LogDataSourceError
    @Override
    public AccountDtoResponse getAccount(Long id) {
        return accountRepository.findById(id)
                .map(account -> modelMapper.map(account, AccountDtoResponse.class))
                .orElseThrow(() -> new EntityNotFoundException(Account.class, id));
    }

    @Override
    public Long createAccount(AccountDtoRequest accountDtoRequest) {
        final Client client = clientService.getClient(accountDtoRequest.clientId());
        final Account account = Account.builder()
                .client(client)
                .balance(BigDecimal.ZERO)
                .accountType(accountDtoRequest.accountType())
                .build();
        return accountRepository
                .save(account)
                .getId();
    }
}
