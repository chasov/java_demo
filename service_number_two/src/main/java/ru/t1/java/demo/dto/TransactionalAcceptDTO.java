package ru.t1.java.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionalAcceptDTO  {
    @NotNull
    @JsonProperty("client_id")
    private UUID clientId;
    @NotNull
    @JsonProperty("account_id")
    private UUID accountId;
    @NotNull
    @JsonProperty("transactional_id")
    private UUID transactionalId;
    @NotNull
    private LocalDateTime timestamp;
    @NotNull
    @JsonProperty("price_transactional")
    private BigDecimal priceTransactional;
    @NotNull
    private BigDecimal balance;
}
