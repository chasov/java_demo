package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Transaction;

import java.io.IOException;
import java.util.List;

public interface GenericService<T> {
    T findById(Long id);

    List<T> findAll();

    T save(T entity);

    void delete(Long id);
}
