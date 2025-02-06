package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import ru.t1.java.demo.service.ErrorService;

@Slf4j
@Aspect
@Component
@Order(0)
@RequiredArgsConstructor
public class DataSourceExceptionAspect {
    private final ErrorService errorService;

    @AfterThrowing(pointcut = "@annotation(LogDataSourceError)", throwing = "e")
    public void logExceptionDataSource(JoinPoint joinPoint, Exception e) {

        try {
            log.info("ASPECT EXCEPTION ANNOTATION: DataSource exception: " + joinPoint.getSignature().getName());
            errorService.sendDataSourceErrorLog(joinPoint, e);
        } finally {
            log.error("Ошибка при логировании исключения DataSource");
        }
    }
}


