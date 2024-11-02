package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;
import ru.t1.java.demo.service.DataSourceErrorLogService;

@Service
@RequiredArgsConstructor
@Transactional
public class DataSourceErrorLogServiceImpl implements DataSourceErrorLogService {

    private final DataSourceErrorLogRepository repository;
    @Override
    public void save(DataSourceErrorLog errorLog) {
        repository.save(errorLog);
    }
}
