package ru.t1.java.demo.dto;

import lombok.Getter;
import lombok.Setter;
import ru.t1.java.demo.model.enums.AccountType;

import java.math.BigDecimal;

@Getter
@Setter
public class AccountDtoResponse {
    private Long id;

    private ClientDto client;

    private AccountType accountType;

    private BigDecimal balance;
}
