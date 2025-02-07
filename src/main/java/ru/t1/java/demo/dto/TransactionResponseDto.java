package ru.t1.java.demo.dto;

import lombok.Builder;

/**
 * DTO for {@link ru.t1.java.demo.model.Transaction}
 */
@Builder
public record TransactionResponseDto (Double amount,
                                      Long clientId,
                                      String transactionTime){}
