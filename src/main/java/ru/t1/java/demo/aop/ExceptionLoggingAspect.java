package ru.t1.java.demo.aop;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.service.DataSourceErrorLogService;

@Log4j2
@Aspect
@Component
@Order(0)
public class ExceptionLoggingAspect {
    @Autowired
    private DataSourceErrorLogService errorLogService;

    // --------------------first variant----------------
//    @Around("@annotation(LogDataSourceError)")
//    public Object handleException(ProceedingJoinPoint joinPoint) throws Throwable {
//        try {
//            Object result = joinPoint.proceed();
//            return result;
//        } catch (Exception ex) {
//            log.info("ASPECT AROUND ANNOTATION @LogDataSourceError: Call method: {}", joinPoint.getSignature().toShortString());
//            DataSourceErrorLog errorLog = new DataSourceErrorLog();
//            errorLog.setStackTrace(ex.getStackTrace().toString());
//            errorLog.setMessage(ex.getMessage());
//            errorLog.setMethodSignature(joinPoint.getSignature().toString());
//            errorLogService.save(errorLog);
//            throw ex;
//        }
//    }

    // --------------------second variant----------------
    @AfterThrowing(pointcut = "@annotation(LogDataSourceError)", throwing = "e")
    public void handleException2(JoinPoint joinPoint, Exception e) {
        log.info("ASPECT AfterThrowing ANNOTATION @LogDataSourceError: Call method: {}", joinPoint.getSignature().toShortString());
        DataSourceErrorLog errorLog = new DataSourceErrorLog();
        errorLog.setStackTrace(e.getStackTrace().toString());
        errorLog.setMessage(e.getMessage());
        errorLog.setMethodSignature(joinPoint.getSignature().toString());
        errorLogService.save(errorLog);
    }
}
