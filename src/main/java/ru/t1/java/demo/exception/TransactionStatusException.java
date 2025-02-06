package ru.t1.java.demo.exception;

public class TransactionStatusException extends RuntimeException {
    public TransactionStatusException(String message) {
        super(message);
    }

    public TransactionStatusException(String message, Throwable cause) {
        super(message, cause);
    }

}
