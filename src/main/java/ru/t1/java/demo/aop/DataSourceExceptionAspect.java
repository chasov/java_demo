package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import ru.t1.java.demo.kafka.KafkaClientProducer;
import ru.t1.java.demo.service.DataSourceErrorService;

@Slf4j
@Aspect
@Component
@Order(0)
@RequiredArgsConstructor
public class DataSourceExceptionAspect {
    private final DataSourceErrorService dataSourceErrorService;

    @AfterThrowing(pointcut = "@annotation(LogDataSourceError)", throwing = "e")
    public void logExceptionDataSource(JoinPoint joinPoint, Exception e) {

        try {
            System.err.println("ASPECT EXCEPTION ANNOTATION: DataSource exception: " + joinPoint.getSignature().getName());
            log.info("В результате выполнения метода {}", joinPoint.getSignature().toShortString());

            dataSourceErrorService.sendDataSourceErrorLog(joinPoint, e);
        } finally {
            log.error("Ошибка при логировании исключения DataSource");
        }
    }
}


