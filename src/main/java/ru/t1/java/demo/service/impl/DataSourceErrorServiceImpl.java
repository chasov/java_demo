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

    @Override
    public void saveDataSourceErrorLog(JoinPoint joinPoint, Exception e) {

        DataSourceErrorLog dataSourceErrorLog = new DataSourceErrorLog(
                joinPoint.getSignature().toString(),
                e.getMessage(),
                stackTraceToString(e));

        repository.save(dataSourceErrorLog);
    }

    private String stackTraceToString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

}
