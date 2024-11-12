package ru.t1.java.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;

@Service
public class ErrorLogService {
    private final DataSourceErrorLogRepository errorLogRepository;

    public ErrorLogService(DataSourceErrorLogRepository errorLogRepository) {
        this.errorLogRepository = errorLogRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveErrorLog(DataSourceErrorLog errorLog) {
        errorLogRepository.save(errorLog);
    }
}