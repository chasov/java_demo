package ru.t1.java.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.t1.java.demo.model.account.AccountType;

import java.math.BigDecimal;

/**
 * DTO for {@link ru.t1.java.demo.model.account.Account}
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDto {
    private Long id;
    @JsonProperty("client")
    private ClientDto client;
    @JsonProperty("account_type")
    private AccountType accountType;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonProperty("balance")
    private BigDecimal balance;
}
