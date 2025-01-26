package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.kafka.KafkaErrorProducer;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.model.MetricErrorLog;
import ru.t1.java.demo.service.ErrorService;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

import static ru.t1.java.demo.enums.ErrorType.DATA_SOURCE;
import static ru.t1.java.demo.enums.ErrorType.METRICS;

@Service
@Slf4j
@RequiredArgsConstructor
public class ErrorServiceImpl implements ErrorService {
    private final KafkaErrorProducer producer;

    @Override
    public void sendMetricErrorLog(long executionTime, JoinPoint joinPoint) {

        String topic = "t1_demo_metrics";

        MetricErrorLog metricErrorLog = new MetricErrorLog(
                executionTime,
                joinPoint.getSignature().toString(),
                argsToString(joinPoint)
        );

        Message<MetricErrorLog> message = MessageBuilder.withPayload(metricErrorLog)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(KafkaHeaders.KEY, null)
                .setHeader("uuid", UUID.randomUUID().toString())
                .setHeader("error_type", METRICS)
                .build();

        producer.sendMessage(metricErrorLog, message);
    }

    @Override
    public void sendDataSourceErrorLog(JoinPoint joinPoint, Exception e) {

        String topic = "t1_demo_metrics";

        DataSourceErrorLog dataSourceErrorLog = new DataSourceErrorLog(
                stackTraceToString(e),
                e.getMessage(),
                joinPoint.getSignature().toString());

        Message<DataSourceErrorLog> message = MessageBuilder.withPayload(dataSourceErrorLog)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(KafkaHeaders.KEY, null)
                .setHeader("uuid", UUID.randomUUID().toString())
                .setHeader("error_type", DATA_SOURCE)
                .build();

        producer.sendMessage(dataSourceErrorLog, message);
    }

    private String stackTraceToString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    private String argsToString(JoinPoint joinPoint) {
        return Arrays.stream(joinPoint.getArgs())
                .map(Object::toString)
                .collect(Collectors.joining(" "));

    }
}
