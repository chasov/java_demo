package ru.t1.java.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.t1.java.demo.model.enums.AccountType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for {@link ru.t1.java.demo.model.Account}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDto implements Serializable {
    private Long id;
    @JsonProperty("client_id")
    private Long clientId;
    @JsonProperty("account_type")
    private AccountType accountType;
    @JsonProperty("balance")
    private BigDecimal balance;
    @JsonProperty("transactions")
    private List<TransactionDto> transactions;
}
