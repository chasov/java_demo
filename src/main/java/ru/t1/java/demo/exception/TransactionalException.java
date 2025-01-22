package ru.t1.java.demo.exception;

public class TransactionalException extends RuntimeException {

    public TransactionalException(String message) {
        super(message);
    }

    public TransactionalException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
