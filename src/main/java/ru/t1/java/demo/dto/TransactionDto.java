package ru.t1.java.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for {@link ru.t1.java.demo.model.Transaction}
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionDto {
    private Long id;
    private Long accountFromId;
    private Long accountToId;
    private BigDecimal amount;
    private LocalDateTime completedAt;
    private LocalDateTime updatedAt;
    private String status;
    private String transactionId;
}
