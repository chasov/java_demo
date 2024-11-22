package ru.t1.java.demo.util;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.exception.AccountException;
import ru.t1.java.demo.exception.ClientException;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.dto.TransactionDto;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.AccountRepository;

import java.util.Optional;


@Component
@AllArgsConstructor
public class TransactionMapper {

    private final AccountRepository accountRepository;

    public Transaction toEntity(TransactionDto dto) throws AccountException {
        Long accountId = dto.getAccountId();
        Optional<Account> account = accountRepository.findById(accountId);
        if (account.isEmpty()) {
            throw new AccountException(String.format("Client with id %s is not exists", accountId));
        }
        return Transaction.builder()
                .amount(dto.getAmount())
                .time(dto.getTimestamp())
                .account(account.get())
                .build();
    }

    public TransactionDto toDto(Transaction entity) {
        return TransactionDto.builder()
                .id(entity.getId())
                .amount(entity.getAmount())
                .timestamp(entity.getTime())
                .accountId(entity.getAccount().getId())
                .build();
    }
}
