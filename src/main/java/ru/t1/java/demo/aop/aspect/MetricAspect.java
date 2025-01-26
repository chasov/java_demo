package ru.t1.java.demo.aop.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.MetricDto;
import ru.t1.java.demo.exception.SendMessageException;
import ru.t1.java.demo.kafka.producer.KafkaMetricProducer;

import java.util.concurrent.CompletableFuture;

@Async
@Component
@Aspect
@RequiredArgsConstructor
@Slf4j
public class MetricAspect {

    private final KafkaMetricProducer metricProducer;

    private final ObjectMapper objectMapper;

    @Value("${t1.metric.execution-time-limit}")
    private Long executionTimeLimit;

    @Around("@annotation(ru.t1.java.demo.aop.annotation.Metric)")
    public CompletableFuture<Object> sendMetricIfTimeExpired(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;

        try {
            if (executionTime > executionTimeLimit) {
                sendMetricToKafka(joinPoint, executionTime);
            }
        } catch (Throwable throwable) {
            log.error("Send metric failed", throwable);
        }

        return CompletableFuture.completedFuture(proceed);
    }

    private void sendMetricToKafka(ProceedingJoinPoint joinPoint,long executionTime)
            throws JsonProcessingException {
        MetricDto metricDto = MetricDto.builder()
                .executionTime(executionTime)
                .methodName(String.valueOf(joinPoint.getSignature()))
                .jsonArgs(objectMapper.writeValueAsString(joinPoint.getArgs()))
                .build();
        try {
            metricProducer.send(metricDto);
        } catch (Exception ex) {
            log.error("Error sending metric to Kafka", ex);
            throw new SendMessageException("Error sending metric to Kafka");
        }
    }
}
