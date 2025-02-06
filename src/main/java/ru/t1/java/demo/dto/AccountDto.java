package ru.t1.java.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.enums.AccountStatus;
import ru.t1.java.demo.model.enums.AccountType;

import java.math.BigDecimal;

/**
 * DTO for {@link Account}
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDto {
    @JsonProperty(value = "id", access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @JsonProperty(value = "account_type")
    private AccountType accountType;
    @JsonProperty(value = "balance")
    private BigDecimal balance;
    @JsonProperty(value = "account_status")
    private AccountStatus accountStatus;
    @JsonProperty(value = "client_id")
    private Long clientId;


}