package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;
import ru.t1.java.demo.service.DataSourceErrorLogService;

@Service
@RequiredArgsConstructor
public class DataSourceErrorLogServiceImpl implements DataSourceErrorLogService {
    private final DataSourceErrorLogRepository repository;

    @Override
    public DataSourceErrorLog create(DataSourceErrorLog errorLog) {
        return repository.save(errorLog);
    }

    @Override
    public void delete(DataSourceErrorLog errorLog) {
        repository.delete(errorLog);
    }
}
