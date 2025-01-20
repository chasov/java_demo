package ru.t1.java.demo.dto;

import jakarta.validation.constraints.NotNull;
import ru.t1.java.demo.model.enums.AccountType;

/**
 * DTO для создания нового банковского счета.
 * @param clientId Идентификатор клиента
 * @param accountType Тип счета
 */
public record AccountDtoRequest(
        @NotNull Long clientId,
        @NotNull AccountType accountType) {
}
