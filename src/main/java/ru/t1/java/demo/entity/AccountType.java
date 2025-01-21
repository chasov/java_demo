package ru.t1.java.demo.entity;

import lombok.Getter;

@Getter
public enum AccountType {
    DEBIT("debit"),
    CREDIT("credit"),
    UNKNOWN("Неизвестный тип");

    private final String strAccountType;

    AccountType(String strAccountType) {
        this.strAccountType = strAccountType;
    }

    public static AccountType getByType(String accountType) {
        for (AccountType aType : AccountType.values()) {
            if (aType.getStrAccountType().equals(accountType)) {
                return aType;
            }
        }
        return UNKNOWN;
    }
}
