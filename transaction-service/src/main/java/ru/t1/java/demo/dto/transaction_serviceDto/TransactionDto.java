package ru.t1.java.demo.dto.transaction_serviceDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import ru.t1.java.demo.model.enums.TransactionStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionDto {
    @JsonProperty("transaction_id")
    private Long id;
    @JsonProperty("account_id")
    private Long account_id;
    private BigDecimal amount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime transactionTime;
    private String transactionStatus;

}
