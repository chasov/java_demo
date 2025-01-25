package ru.t1.java.demo.account.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.t1.java.demo.account.enums.AccountScoreType;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
public class AccountDto {

    private AccountScoreType accountScoreType;

    private BigDecimal balance;
}
