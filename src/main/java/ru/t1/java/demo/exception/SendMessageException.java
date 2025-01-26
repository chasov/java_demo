package ru.t1.java.demo.exception;

public class SendMessageException extends RuntimeException{

    public SendMessageException(String message) {
        super(message);
    }
}
