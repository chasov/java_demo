package ru.t1.java.demo.util;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.AccountRepository;

@Component
@AllArgsConstructor
public class TransactionMapper {

    private final AccountRepository accountRepository;

    public Transaction toEntity(TransactionDto dto) {
        Account account = accountRepository.findById(dto.getAccountId()).orElse(null);
        return Transaction.builder()
                .amount(dto.getAmount())
                .time(dto.getTime())
                .account(account)
                .build();
    }

    public TransactionDto toDto(Transaction entity) {
        return TransactionDto.builder()
                .id(entity.getId())
                .amount(entity.getAmount())
                .time(entity.getTime())
                .accountId(entity.getAccount().getId())
                .build();
    }
}
