package ru.t1.java.demo.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.TransactionDto;
import ru.t1.java.demo.model.Transaction;

@Component
public class TransactionMapper {
    private final AccountMapper accountMapper;

    @Autowired
    public TransactionMapper(AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
    }

    public Transaction toEntity(TransactionDto dto) {
        return Transaction.builder()
                .account(accountMapper.toEntity(dto.getAccount()))
                .amount(dto.getAmount())
                .dateTime(dto.getDateTime())
                .build();
    }

    public TransactionDto toDto(Transaction entity) {
        return TransactionDto.builder()
                .id(entity.getId())
                .account(accountMapper.toDto(entity.getAccount()))
                .amount(entity.getAmount())
                .dateTime(entity.getDateTime())
                .build();
    }

}
