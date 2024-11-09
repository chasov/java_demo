package ru.t1.java.demo.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.MetricLogDto;
import ru.t1.java.demo.kafka.KafkaErrorLogProducer;
import ru.t1.java.demo.kafka.KafkaMetricProducer;

@Aspect
@Component
@Slf4j
public class MetricAspect {


    @Autowired
    private KafkaMetricProducer kafkaMetricProducer;

    @Around("@annotation(metric)")
    public Object logMethodExecutionTime(ProceedingJoinPoint joinPoint, Metric metric) throws Throwable {
        long startTime = System.currentTimeMillis();

        Object result = null;
        try {
            result = joinPoint.proceed();
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;

            if (executionTime > metric.value()) {
                log.warn("Method {} took {} ms which exceeds the limit of {} ms",
                        joinPoint.getSignature().toShortString(), executionTime, metric.value());

                MetricLogDto metricLog = MetricLogDto.builder()
                        .methodName(joinPoint.getSignature().toShortString())
                        .params(getMethodParams(joinPoint.getArgs()))
                        .executionTime(executionTime)
                        .build();

                try {
                    kafkaMetricProducer.send(metricLog);
                } catch (KafkaErrorLogProducer.KafkaSendException kafkaException) {
                    log.error("Failed to send message to Kafka");
                }
            }
        }

        return result;
    }


    private String getMethodParams(Object[] args) {
        if (args == null || args.length == 0) {
            return "No parameters";
        }
        StringBuilder params = new StringBuilder();
        for (Object arg : args) {
            if (params.length() > 0) {
                params.append(", ");
            }
            params.append(arg.getClass().getSimpleName()).append(": ").append(arg);
        }
        return params.toString();
    }
}