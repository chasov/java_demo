package ru.t1.java.demo.dto;

import lombok.*;
import ru.t1.java.demo.model.Account;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    private Long id;
    private Long clientId;
    private String accountType;
    private BigDecimal balance;

    private UUID accountId;
    private BigDecimal frozenAmount;
    private Account.Status status;
}
