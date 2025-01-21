package ru.t1.java.demo.service;

import ru.t1.java.demo.model.DataSourceErrorLog;

public interface DataSourceErrorLogService {
    DataSourceErrorLog create(DataSourceErrorLog errorLog);
    void delete(DataSourceErrorLog errorLog);
}
