package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;
import ru.t1.java.demo.service.LogErrorService;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static ru.t1.java.demo.util.ExtractStack.getStackTraceAsString;

/**
 * Сервисный слой для работы с исключениями.
 */
@Service
@RequiredArgsConstructor
public class LogErrorServiceImpl implements LogErrorService {

    private final DataSourceErrorLogRepository dataSourceErrorLogRepository;

    @Transactional(propagation = REQUIRES_NEW)
    @Override
    public void logError(Throwable throwable, String methodSignature) {
        final DataSourceErrorLog errorLog = new DataSourceErrorLog();
        final String stackTrace = getStackTraceAsString(throwable);
        errorLog.setMessage(throwable.getMessage());
        errorLog.setStackTrace(stackTrace);
        errorLog.setMethodSignature(methodSignature);
        dataSourceErrorLogRepository.save(errorLog);
    }
}
