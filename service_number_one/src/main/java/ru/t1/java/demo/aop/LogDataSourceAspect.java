package ru.t1.java.demo.aop;


import java.util.Arrays;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.kafka.producer.KafkaProducer;
import ru.t1.java.demo.model.DataSourceErrorLog;


@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LogDataSourceAspect {
    private final KafkaProducer kafkaProducer;

    @AfterThrowing(pointcut = "within(@ru.t1.java.demo.aop.LogDataSourceError *)" +
            " && execution(* *(..))", throwing = "exception")
    public void logDataSourceError(JoinPoint joinPoint, Throwable exception) {
        DataSourceErrorLog dataSourceErrorLog = DataSourceErrorLog.builder()
                .stackTrace(Arrays.toString(exception.getStackTrace()))
                .message(exception.getMessage())
                .methodSignature(joinPoint.getSignature()
                        .toString()).build();
        kafkaProducer.logDataSourceExceptionProcessing(dataSourceErrorLog);

        log.error("Ошибка в методе {}: {}", joinPoint.getSignature(), exception.getMessage(), exception);
    }
}

