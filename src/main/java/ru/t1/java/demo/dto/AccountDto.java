package ru.t1.java.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

/**
 * DTO for {@link ru.t1.java.demo.model.Account}
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDto {
    private Long id;

    @NotNull
    @PositiveOrZero(message = "Client ID must be a non-negative number")
    private Long clientId;

    @NotBlank(message = "account type field is empty")
    private String accountType;

    @NotNull
    @PositiveOrZero(message = "Balance must be a non-negative number")
    private BigDecimal balance;
    private String status;
    private String accountId;
    private BigDecimal frozenAmount;
}
