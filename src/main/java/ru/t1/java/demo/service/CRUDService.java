package ru.t1.java.demo.service;


import java.util.Collection;

public interface CRUDService<T> {

    T getById(Long id);
    Collection<T> getAll();
    T create(T item);
    T update(Long id, T item);
    void delete(Long id);

}
