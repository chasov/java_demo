package ru.t1.java.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.t1.java.demo.model.enums.TransactionalStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionalDTO {

    @JsonProperty("transactional_id")
    private UUID transactionalId;

    private AccountDTO account;

    @NotNull
    @JsonProperty("price_transactional")
    private BigDecimal priceTransactional;
    @NotNull
    @JsonProperty("time_transactional")
    private Long timeTransactional;
    @NotNull
    @JsonProperty("transactional_status")
    TransactionalStatus transactionalStatus;

    private LocalDateTime timestamp;
}
