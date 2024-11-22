package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.kafka.KafkaLogDataSourceErrorProducer;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
@Setter
@RequiredArgsConstructor
@Order(0)
public class LogDataSourceErrorAspect {

    private final KafkaLogDataSourceErrorProducer<DataSourceErrorLog> producer;

    private final DataSourceErrorLogRepository repository;

    @Pointcut("within(ru.t1.java.demo.*)")
    public void dataSourceErrorLogMethods() {}

    @AfterThrowing(pointcut = "@annotation(ru.t1.java.demo.aop.LogDataSourceError)", throwing = "ex")
    public void logDataSourceErrorAdvice(JoinPoint joinPoint, Exception ex) {
        DataSourceErrorLog dataSourceError = DataSourceErrorLog.builder()
                                                               .errorMessage(ex.getMessage())
                                                               .methodSignature(String.valueOf(joinPoint.getSignature()))
                                                               .trace(Arrays.toString(ex.getStackTrace()))
                                                               .build();

        log.error("Advice logDataSourceErrorAdvice: Data source exception: {}", dataSourceError.toString());

        if (!producer.send(dataSourceError)) {
            try {
                repository.saveAndFlush(dataSourceError);
            } catch (DataAccessException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

}
