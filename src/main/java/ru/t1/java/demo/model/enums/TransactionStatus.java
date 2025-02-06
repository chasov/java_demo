package ru.t1.java.demo.model.enums;

import lombok.Getter;

@Getter
public enum TransactionStatus {
    ACCEPTED("ACCEPTED"),
    REJECTED("REJECTED"),
    BLOCKED("BLOCKED"),
    CANCELLED("CANCELLED"),
    REQUESTED("REQUESTED");

    private final String name;

    TransactionStatus(String name) {
        this.name = name;
    }
}
