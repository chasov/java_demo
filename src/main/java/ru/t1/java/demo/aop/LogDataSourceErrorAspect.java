package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.model.dto.DataSourceErrorLogDto;
import ru.t1.java.demo.service.DataSourceErrorLogService;
import ru.t1.java.demo.util.DataSourceErrorLogMapper;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class LogDataSourceErrorAspect {
    private final KafkaTemplate<String, DataSourceErrorLogDto> template;
    private final DataSourceErrorLogService service;
    private final static String TOPIC = "t1_demo_metrics";

    @AfterThrowing(pointcut = "within(ru.t1.java.demo.controller.*)", throwing = "exception")
    public void logException(JoinPoint joinPoint, RuntimeException exception) {
        DataSourceErrorLogDto dto = DataSourceErrorLogDto.builder()
                .exceptionStackTrace(Arrays.toString(exception.getStackTrace()))
                .message(exception.getMessage())
                .methodSignature(joinPoint.getSignature().toString())
                .build();
        List<Header> headers = new ArrayList<>();
        headers.add(new RecordHeader("ErrorType", "DATA_SOURCE".getBytes(StandardCharsets.UTF_8)));
        ProducerRecord<String, DataSourceErrorLogDto> record = new ProducerRecord<>(TOPIC, null, System.currentTimeMillis(),
                "error", dto, headers);
        var sendResult = template.send(record);
        sendResult.exceptionally(e -> {
            try {
                DataSourceErrorLog errorLog = DataSourceErrorLogMapper.toEntity(dto);
                service.create(errorLog);
            } catch (Throwable ex) {
                log.error("Не удалось добавить запись в таблицу data_source_error_log", ex);
            }
            return null;
        });
    }
}
