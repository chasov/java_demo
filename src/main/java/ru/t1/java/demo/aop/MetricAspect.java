package ru.t1.java.demo.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class MetricAspect {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate; // Используем KafkaTemplate для отправки сообщений

    // Метод для отслеживания времени выполнения метода с аннотацией @Track
    @Around("@annotation(ru.t1.java.demo.aop.annotation.Track)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        // Получаем параметры аннотации, чтобы использовать порог времени
        long timeThreshold = ((ru.t1.java.demo.aop.annotation.Track) joinPoint.getSignature()
                .getDeclaringType()
                .getMethod(joinPoint.getSignature().getName(), Arrays.stream(joinPoint.getArgs()).map(Object::getClass).toArray(Class[]::new))
                .getAnnotation(ru.t1.java.demo.aop.annotation.Track.class))
                .timeThreshold();

        // Логируем начало выполнения метода
        long startTime = System.currentTimeMillis();
        log.info("Вызов метода: {}", joinPoint.getSignature().toShortString());
        Object result = null;

        try {
            // Выполняем основной метод
            result = joinPoint.proceed();
        } finally {
            // Получаем время после выполнения метода
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            // Логируем время выполнения метода
            log.info("Метод {} выполнен за {} ms", joinPoint.getSignature().toShortString(), executionTime);

            // Проверяем, превышает ли время выполнения пороговое значение
            if (executionTime > timeThreshold) {
                // Формируем сообщение для отправки в Kafka
                String message = String.format("Метод: %s, Время: %d ms, Параметры: %s",
                        joinPoint.getSignature().toShortString(), executionTime, Arrays.toString(joinPoint.getArgs()));

                // Отправляем сообщение в топик Kafka
                kafkaTemplate.send("t1_demo_metrics", "METRICS", message);
                log.info("Отправлено сообщение в Kafka: {}", message);
            }
        }

        return result;
    }
}
