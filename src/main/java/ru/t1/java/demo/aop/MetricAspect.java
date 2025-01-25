package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.kafka.KafkaErrorProducer;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class MetricAspect {

    private final KafkaErrorProducer kafkaErrorProducer;
    private final Environment environment;

    @Around("@annotation(metric)")
    public Object monitorMethod(ProceedingJoinPoint joinPoint, Metric metric) throws Throwable {
        long threshold = getThresholdFromAnnotation(metric);
        return executeAndLogMetrics(joinPoint, threshold);
    }

    // Точка среза для классов, аннотированных @Metric
    @Around("@within(metric)")
    public Object monitorClass(ProceedingJoinPoint joinPoint, Metric metric) throws Throwable {
        long threshold = getThresholdFromAnnotation(metric);
        return executeAndLogMetrics(joinPoint, threshold);
    }

    private long getThresholdFromAnnotation(Metric metric) {
        String thresholdProperty = metric.thresholdProperty();
        return Long.parseLong(environment.getProperty(thresholdProperty, "1000"));
    }

    private Object executeAndLogMetrics(ProceedingJoinPoint joinPoint, long threshold) throws Throwable {
        long startTime = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;

            if (executionTime > threshold) {
                String methodName = joinPoint.getSignature().toShortString();
                String params = Arrays.stream(joinPoint.getArgs())
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));
                String payload = String.format(
                        "Метод %s превысил порог времени выполнения (%d ms). Время выполнения: %d ms. Параметры: [%s]",
                        methodName, threshold, executionTime, params);

                try {
                    kafkaErrorProducer.send(payload, Map.of("ErrorType", "METRICS"));
                    log.info("Сообщение о превышении времени отправлено в Kafka: {}", payload);
                } catch (Exception kafkaException) {
                    log.error("Ошибка при отправке сообщения в Kafka: {}", kafkaException.getMessage(), kafkaException);
                }
            }
        }
    }

//    @Around("@annotation(metric)")
//    public Object monitorExecutionTime(ProceedingJoinPoint joinPoint, Metric metric) throws Throwable {
//        String thresholdProperty = metric.thresholdProperty();
//        long threshold = Long.parseLong(environment.getProperty(thresholdProperty, "1000"));
//
//        long startTime = System.currentTimeMillis();
//        try {
//            return joinPoint.proceed();
//        } finally {
//            long executionTime = System.currentTimeMillis() - startTime;
//            if (executionTime > threshold) {
//                String methodName = joinPoint.getSignature().toShortString();
//                String params = Arrays.stream(joinPoint.getArgs())
//                        .map(Object::toString)
//                        .collect(Collectors.joining(", "));
//                String payload = String.format(
//                        "Метод %s превысил порог времени выполнения (%d ms). Время выполнения: %d ms. Параметры: [%s]",
//                        methodName, threshold, executionTime, params);
//                try {
//                    kafkaErrorProducer.send(payload,
//                            Map.of("ErrorType", "METRICS"));
//                    log.info("Сообщение о превышении времени отправлено в Kafka: {}", payload);
//                } catch (Exception kafkaException){
//                    log.error("Ошибка при отправке сообщения в Kafka: {}", kafkaException.getMessage(), kafkaException);
//                }
//            }
//        }
//    }
}
