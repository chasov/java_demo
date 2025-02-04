package ru.t1.java.demo.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.DataSourceErrorLogDto;
import ru.t1.java.demo.mapper.DataSourceErrorLogMapper;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;

import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
@Order(0)
@Slf4j
public class DataSourceErrorLogAspect {

    private final DataSourceErrorLogRepository dataSourceErrorLogRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final DataSourceErrorLogMapper dataSourceErrorLogMapper;

    @Value("${kafka.topic.metrics}")
    private String metricsTopic;

    @AfterThrowing(pointcut = "@annotation(ru.t1.java.demo.aop.annotations.LogDataSourceError)", throwing = "ex")
    public void logDataSourceError(JoinPoint joinPoint, Throwable ex) {
        DataSourceErrorLogDto dataSourceErrorLogDto = createDataSourceErrorLog(ex, joinPoint);
        try {
            kafkaTemplate.send(metricsTopic,
                    "DATA_SOURCE",
                    objectMapper.writeValueAsString(dataSourceErrorLogDto));
            log.info("Message send to Kafka: " + dataSourceErrorLogDto);
        } catch (Exception kafkaException) {
            log.warn("Error send to Kafka: " + kafkaException.getMessage() + ". Write to DB");

            DataSourceErrorLogDto log = createDataSourceErrorLog(kafkaException, joinPoint);

            dataSourceErrorLogRepository.save(dataSourceErrorLogMapper.toEntity(log));
        }

    }

    private DataSourceErrorLogDto createDataSourceErrorLog(Throwable ex, JoinPoint joinPoint) {
        return DataSourceErrorLogDto.builder()
                .message(truncateString(ex.getMessage(), 600))
                .stackTrace(truncateString(String.join(" ", Arrays.stream(ex.getStackTrace())
                        .map(StackTraceElement::toString)
                        .toArray(String[]::new)), 1000))
                .methodSignature(truncateString(joinPoint.getSignature().toLongString(), 600))
                .build();
    }

    private String truncateString(String value, int maxLength) {
        if (value != null && value.length() > maxLength) {
            return value.substring(0, maxLength);
        }
        return value;
    }
}
