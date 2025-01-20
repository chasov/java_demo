package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;
import ru.t1.java.demo.service.DataSourceErrorService;

import java.io.PrintWriter;
import java.io.StringWriter;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataSourceErrorServiceImpl implements DataSourceErrorService {
    private final DataSourceErrorLogRepository repository;
    DataSourceErrorLog dataSourceErrorLog;
    @Override
    public void saveDataSourceErrorLog(JoinPoint joinPoint, Exception e) {

        dataSourceErrorLog = new DataSourceErrorLog();

        dataSourceErrorLog.setMethodSignature(joinPoint.getSignature().toString());
        dataSourceErrorLog.setMessage(e.getMessage());
        dataSourceErrorLog.setStackTrace(stackTraceToString(e));

        repository.save(dataSourceErrorLog);
    }

    private String stackTraceToString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
