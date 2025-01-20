package ru.t1.java.demo.exception;


public class EntityNotFoundException extends RuntimeException {

    private static final String MESSAGE = "%s with id %s not found";

    public EntityNotFoundException(Class<?> entityClass, Object id) {
        super(MESSAGE.formatted(entityClass.getSimpleName(), id));
    }
}
