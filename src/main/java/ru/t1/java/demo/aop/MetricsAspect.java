package ru.t1.java.demo.aop;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.MetricsDTO;
import ru.t1.java.demo.kafka.KafkaMetricsProducer;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class MetricsAspect {

    @PostConstruct
    public void init(){
        log.debug("Проверка уровня логирования: DEBUG включен");
        log.info("Проверка уровня логирования: INFO включен");
    }
    @Autowired
    private final KafkaMetricsProducer kafkaMetricsProducer;

    @Value("${spring.kafka.topic.metrics}")
    private String topic;

    @Autowired
    public MetricsAspect(KafkaMetricsProducer kafkaMetricsProducer) {
        this.kafkaMetricsProducer = kafkaMetricsProducer;
    }

    @Pointcut("@annotation(Metrics)")
    public void metricMethods() {}

    @Around("ru.t1.java.demo.aop.MetricsAspect.metricMethods() && execution(* ru.t1.java.demo.service..*(..))")
    public Object calculateWorkingTimeExceeding(ProceedingJoinPoint joinPoint) {
        long startTime = System.currentTimeMillis();
        log.debug("Method {} execution started", joinPoint.getSignature().getName());  // Лог до выполнения


        try {
            Metrics metrics = getMetricsAnnotation(joinPoint);
            if (metrics == null) {
                return joinPoint.proceed();
            }
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            long workingTime = endTime-startTime;

            if ((workingTime)>metrics.milliseconds()){

                String methodName = joinPoint.getSignature().getName();
                String parameters = Arrays.stream(joinPoint.getArgs())
                        .map(Object::toString)
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("No parameters");
                log.debug(String.format("Method: %s, WorkingTime: %d ms, Parameters: %s", methodName, workingTime, parameters));

                MetricsDTO metricsDTO = new MetricsDTO(methodName, parameters, workingTime);
                try {
                    log.debug("Отправка сообщения метрик в Kafka. Тема: {}, Заголовок: METRICS, Данные: {}", topic, metricsDTO);
                    kafkaMetricsProducer.send(topic, "METRICS", metricsDTO);
                    log.info("Сообщение метрик успешно отправлено в Kafka. Метод: {}", methodName);
                } catch (Exception ex) {
                    log.error("Ошибка при отправке сообщения метрик в Kafka для метода {}: {}", methodName, ex.getMessage(), ex);
                }
            } else {
                log.debug("Время выполнения метода {} не превышает порог {} ms, отправка метрик в Kafka не требуется",
                        joinPoint.getSignature().getName(), metrics.milliseconds());
            }

            return result;

        } catch (Throwable e) {
            log.error("Ошибка при работе метрики : {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }


    }

    private Metrics getMetricsAnnotation(ProceedingJoinPoint joinPoint) {
       return ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(Metrics.class);
    }

}
