package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.kafka.producer.KafkaDataSourceErrorLogProducer;
import ru.t1.java.demo.mapper.DataSourceErrorLogMapper;
import ru.t1.java.demo.model.dto.DataSourceErrorLogDto;
import ru.t1.java.demo.model.errorlog.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogDataSourceAspect {

    private final DataSourceErrorLogRepository dataSourceErrorLogRepository;
    private final DataSourceErrorLogMapper errorLogMapper;
    private final KafkaDataSourceErrorLogProducer<DataSourceErrorLogDto> kafkaDataSourceErrorLogProducer;

    @Value("${spring.t1.kafka.topic.error_metric}")
    private String metricsTopic;

    @AfterThrowing(pointcut = "@annotation(ru.t1.java.demo.aop.annotation.LogDataSourceError)", throwing = "ex")
    public void logExceptionAnnotation(JoinPoint joinPoint, Throwable ex) {
        log.error("An error occurred while working with the database {}", ex.getMessage());

        DataSourceErrorLog dataSourceErrorLog = DataSourceErrorLog.builder()
                .message(ex.getMessage())
                .methodSignature(String.valueOf(joinPoint.getSignature()))
                .build();

        StringBuilder stackTraceBuilder = new StringBuilder();
        for (StackTraceElement element : ex.getStackTrace()) {
            stackTraceBuilder.append(element.toString()).append("\n");
        }
        dataSourceErrorLog.setStackTrace(stackTraceBuilder.toString());

        DataSourceErrorLogDto errorLogDto = errorLogMapper.toDto(dataSourceErrorLog);
        sendMessageToKafka(errorLogDto);
    }

    private void sendMessageToKafka(DataSourceErrorLogDto errorLogDto) {
        try {
            kafkaDataSourceErrorLogProducer.sendTo(metricsTopic, "DATA_SOURCE", errorLogDto);
            log.info("Error message sent to Kafka topic {}: {}", metricsTopic, errorLogDto.getMessage());
        } catch (Exception e) {
            log.error("Failed to send error message to Kafka, saving to DB instead: {}", e.getMessage());
            dataSourceErrorLogRepository.save(errorLogMapper.toEntity(errorLogDto));
        }
    }
}
