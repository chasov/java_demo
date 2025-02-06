package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.aop.annotation.Metric;
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

    /**
     * Метод обрабатывает аннотацию @Metric, логируя время выполнения метода
     * и проверяя, превышает ли оно пороговое значение.
     */
    @Around("@annotation(metric)")
    public Object monitorMetric(ProceedingJoinPoint joinPoint, Metric metric) throws Throwable {
        long threshold = getThreshold(metric);
        return startLogMetrics(joinPoint, threshold);
    }

    /**
     * Метод обрабатывает аннотацию @Metric на уровне класса и логирует время выполнения всех методов этого класса.
     */
    @Around("@within(metric)")
    public Object monitoring(ProceedingJoinPoint joinPoint, Metric metric) throws Throwable {
        long threshold = getThreshold(metric);
        return startLogMetrics(joinPoint, threshold);
    }

    /**
     * Получение порогового значения времени выполнения из аннотации Metric.
     *
     * @param metric Аннотация, содержащая информацию о пороге времени
     * @return Пороговое значение времени
     */
    private long getThreshold(Metric metric) {
        String thresholdProperty = metric.thresholdProperty();
        return Long.parseLong(environment.getProperty(thresholdProperty, "1000"));
    }

    /**
     * Выполнение метода и логирование времени его выполнения.
     * Если время превышает порог, отправляется сообщение в Kafka.
     */
    private Object startLogMetrics(ProceedingJoinPoint joinPoint, long threshold) throws Throwable {
        long startTime = System.currentTimeMillis();
        try {
            // Выполнение метода
            return joinPoint.proceed();
        } finally {
            // Логируем время выполнения метода
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
                    // Отправляем сообщение о превышении времени в Kafka
                    kafkaErrorProducer.publishMessage(payload, Map.of("ErrorType", "METRICS"));
                    log.info("Сообщение о превышении времени отправлено в Kafka: {}", payload);
                } catch (Exception kafkaException) {
                    log.error("Ошибка при отправке сообщения в Kafka: {}", kafkaException.getMessage(), kafkaException);
                }
            }
        }
    }
}
