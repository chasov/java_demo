package ru.t1.java.demo.exception;

public class TransactionException extends RuntimeException {

    public TransactionException(String message) {
        super(message);
    }
}
