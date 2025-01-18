package ru.t1.java.demo.service;

import java.io.IOException;
import java.util.List;

public interface ImplService<T> {
    List<T> parseJson() throws IOException;
}
