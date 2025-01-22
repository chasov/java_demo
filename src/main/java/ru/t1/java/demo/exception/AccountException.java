package ru.t1.java.demo.exception;

public class AccountException extends RuntimeException {

    public AccountException(String message) {
        super(message);
    }

    public AccountException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
