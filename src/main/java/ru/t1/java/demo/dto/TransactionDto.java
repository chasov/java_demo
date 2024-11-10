package ru.t1.java.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.t1.java.demo.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private Long id;
    private Long accountId;
    private BigDecimal amount;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime transactionTime;

    private UUID transactionId;

    private Transaction.Status status;
    private LocalDateTime timestamp;

}
