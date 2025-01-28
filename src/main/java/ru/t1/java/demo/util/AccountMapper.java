package ru.t1.java.demo.util;

import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.dto.AccountDto;
import ru.t1.java.demo.enums.AccountType;
import ru.t1.java.demo.model.Account;

@Component
public class AccountMapper {

    public static Account toEntity(AccountDto dto) {
        return Account.builder()
                .clientId(dto.getClientId())
                .accountType(AccountType.valueOf(dto.getAccountType().toUpperCase()))
                .balance(dto.getBalance())
                .build();
    }

    public static AccountDto toDto(Account entity) {
        return AccountDto.builder()
                .clientId(entity.getClientId())
                .accountType(String.valueOf(entity.getAccountType()).toLowerCase())
                .balance(entity.getBalance())
                .build();
    }

    public static Account toEntityWithId(AccountDto dto) {
//        if (dto.getMiddleName() == null) {
//            throw new NullPointerException();
//        }
        int randomInt = (int) (Math.random() * 100000000);
        return Account.builder()
                .accountId(randomInt)
                .clientId(dto.getClientId())
                .accountType(AccountType.valueOf(dto.getAccountType().toUpperCase()))
                .balance(dto.getBalance())
                .build();
    }

}
