package ru.t1.java.demo.service.impl.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.aop.logdatasource.LogDataSourceError;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.exception.account.AccountException;
import ru.t1.java.demo.mapper.AccountMapper;
import ru.t1.java.demo.model.dto.AccountDto;
import ru.t1.java.demo.model.entity.Account;
import ru.t1.java.demo.model.entity.Client;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.ClientRepository;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    @LogDataSourceError
    public AccountDto createAccount(AccountDto accountDto) {
        Account account = accountMapper.toEntity(accountDto);
        Client client = clientRepository.findById(accountDto.getClientId())
                .orElseThrow(() -> new ClientException("Client not found"));

        account.setClient(client);

        account = accountRepository.save(account);

        return accountMapper.toDto(account);
    }

    @Override
    @LogDataSourceError
    public AccountDto getAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountException("Account not found"));
        return accountMapper.toDto(account);
    }

    @Override
    @LogDataSourceError
    public void deleteAccount(Long accountId) {
        accountRepository.deleteById(accountId);
    }
}
