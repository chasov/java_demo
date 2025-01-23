package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.ErrorLogRepository;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class LogDataSourceError {
    private final ErrorLogRepository errorLogRepository;

    @AfterThrowing(pointcut = "@annotation(WriteLogException)", throwing = "ex")
    public void logError(Throwable ex) {
        saveErrorLog(ex);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveErrorLog(Throwable ex) {
        DataSourceErrorLog errorLog = new DataSourceErrorLog();
        errorLog.setExceptionStackTrace(ex.toString());
        errorLog.setMessage(ex.getMessage());
        errorLog.setMethodSignature(ex.getStackTrace()[0].toString());
        try {
            errorLogRepository.save(errorLog);
        } catch (DataAccessException dae) {
            log.error("Can't save error log", dae.getMessage());
        } catch (Exception e) {
            log.error("Unknown error", e);
        }
    }
}
