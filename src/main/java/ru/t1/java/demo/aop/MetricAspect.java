package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class MetricAspect {

    @Value("${execution.time.ms.threshold}")
    private long executionTimeThreshold;

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Pointcut("@annotation(ru.t1.java.demo.aop.Track)")
    public void trackPointcut() {}
    @Around("trackPointcut()")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) {
        long startTime = System.currentTimeMillis();

        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            handleError(joinPoint, throwable);
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        logExecutionTime(joinPoint, elapsedTime);
        if (elapsedTime > executionTimeThreshold) {
            sendMetricToKafka(joinPoint, elapsedTime);
        }

        return result;
    }
    private void logExecutionTime(ProceedingJoinPoint joinPoint, long elapsedTime) {
        log.info("Executed method: {} | Time taken: {} ms", joinPoint.getSignature(), elapsedTime);
    }
    private void sendMetricToKafka(ProceedingJoinPoint joinPoint, long elapsedTime) {
        String message = String.format("Elapsed time: %d ms. Method name: %s. Parameters: %s",
                elapsedTime, joinPoint.getSignature(), Arrays.toString(joinPoint.getArgs()));

        kafkaTemplate.send("t1_demo_metrics", "METRICS", message);
        log.info("Sent metrics to Kafka: {}", message);
    }

    private void handleError(ProceedingJoinPoint joinPoint, Throwable throwable) {
        log.error("Exception thrown in method: {} Exception: {}", joinPoint.getSignature(), throwable.getMessage(), throwable);
    }
}
