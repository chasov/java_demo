package ru.t1.java.demo.exception;

public class TransctionException extends RuntimeException {
    public TransctionException() {
    }

    public TransctionException(String message) {
        super(message);
    }

    public TransctionException(String message, Throwable cause) {
        super(message, cause);
    }
}
