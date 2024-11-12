package ru.t1.java.demo.util;

import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.TransactionDTO;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;



@Component
public class TransactionMapper {

    public static Transaction toEntity(TransactionDTO dto, Account account) {
        if (dto == null) {
            return null;
        }

        return Transaction.builder()
                .id(dto.getId())
                .amount(dto.getAmount())
                .timestamp(dto.getTimestamp())
                .account(account)
                .build();
    }

    public static TransactionDTO toDto(Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        return TransactionDTO.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .timestamp(transaction.getTimestamp())
                .build();
    }
}
