package ru.t1.java.demo.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.aop.annotations.Metric;
import ru.t1.java.demo.dto.MetricDto;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Aspect
@Component
@RequiredArgsConstructor
@Order(0)
@Slf4j
public class MetricAspect {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topic.metrics}")
    private String metricsTopic;

    @Around("@annotation(metric)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint, Metric metric) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;
        if (executionTime > metric.time()) {
            log.info("The execution time exceeds the specified time: {}>{}",
                    executionTime, metric.time());
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            MetricDto metricDto = MetricDto.builder()
                    .methodName(signature.getName())
                    .executionTime(executionTime)
                    .params(IntStream.range(0, signature.getParameterNames().length)
                            .boxed()
                            .collect(Collectors.toMap(i -> signature.getParameterNames()[i], i -> joinPoint.getArgs()[i])))
                    .build();
            try {
                kafkaTemplate.send(metricsTopic,
                        "METRICS",
                        objectMapper.writeValueAsString(metricDto));
                log.info("Metric message send to kafka: " + metricDto);
            } catch (Exception e) {
                log.warn("Error send to kafka: " + e.getMessage());
            }
        }
        return proceed;
    }
}
