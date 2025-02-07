package ru.t1.java.demo.dto;

import lombok.Builder;

/**
 * DTO for {@link ru.t1.java.demo.model.Account}
 */
@Builder
public record AccountResponseDto(Long clientId,
                                 String accountType,
                                 Double balance) {}
