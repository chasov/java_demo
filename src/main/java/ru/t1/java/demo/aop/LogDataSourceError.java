package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.ErrorLogRepository;

@Aspect
@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class LogDataSourceError {
    private final ErrorLogRepository errorLogRepository;

    @AfterThrowing(pointcut = "@annotation(WriteLogException)", throwing = "ex")
    public void logError(Exception ex) {
        System.out.println("HERE");
        DataSourceErrorLog errorLog = new DataSourceErrorLog();
        errorLog.setExceptionStackTrace(ex.toString());
        errorLog.setMessage(ex.getMessage());
        errorLog.setMethodSignature(ex.getStackTrace()[0].toString());
        errorLogRepository.save(errorLog);
    }
}
