package ru.t1.java.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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

    @NotNull
    @PositiveOrZero(message = "Your account ID must be a non-negative number")
    private Long accountFromId;

    @NotNull
    @PositiveOrZero(message = "received account ID must be a non-negative number")
    private Long accountToId;

    @NotNull
    @PositiveOrZero(message = "Amount must be a non-negative number")
    private BigDecimal amount;
    private LocalDateTime completedAt;
    private LocalDateTime updatedAt;
    private String status;
    private String transactionId;
}
