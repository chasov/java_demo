package ru.t1.java.demo.dto;

import lombok.*;

import java.math.BigDecimal;

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
}
