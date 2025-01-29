package ru.t1.java.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.MetricDto;
import ru.t1.java.demo.exception.SendMessageException;
import ru.t1.java.demo.kafka.producer.KafkaMetricProducer;

@Service
@Slf4j
@RequiredArgsConstructor
public class MetricService {

    private final KafkaMetricProducer<MetricDto> metricProducer;

    private final ObjectMapper objectMapper;

    public void sendMetricToKafka(ProceedingJoinPoint joinPoint, long executionTime)
            throws SendMessageException, JsonProcessingException {
        MetricDto metricDto = MetricDto.builder()
                .executionTime(executionTime)
                .methodName(String.valueOf(joinPoint.getSignature()))
                .jsonArgs(objectMapper.writeValueAsString(joinPoint.getArgs()))
                .build();
        try {
            metricProducer.send(metricDto);
        } catch (Exception ex) {
            log.error("Error sending metric", ex);
            throw new SendMessageException("Error sending metric");
        }
    }
}
