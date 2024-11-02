package ru.t1.java.demo.util.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.service.impl.AccountService;

@Component
@AllArgsConstructor
public class TransactionMapper {

    private final AccountService accountService;

    public Transaction toEntity(TransactionDto dto) {
        Account account = accountService.findById(dto.getAccountId());
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
