package ru.t1.java.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;

@Service
public class DataSourceErrorLogService {

    private final DataSourceErrorLogRepository dataSourceErrorLogRepository;

    @Autowired
    public DataSourceErrorLogService(DataSourceErrorLogRepository dataSourceErrorLogRepository) {
        this.dataSourceErrorLogRepository = dataSourceErrorLogRepository;
    }

    @Transactional
    public DataSourceErrorLog save(DataSourceErrorLog log) {
        return dataSourceErrorLogRepository.save(log);
    }
}