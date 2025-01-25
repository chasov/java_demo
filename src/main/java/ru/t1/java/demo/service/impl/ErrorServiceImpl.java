package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.kafka.KafkaClientProducer;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.service.ErrorService;

import java.io.PrintWriter;
import java.io.StringWriter;

@Service
@Slf4j
@RequiredArgsConstructor
public class ErrorServiceImpl implements ErrorService {
    private final KafkaClientProducer producer;

    @Override
    public void sendDataSourceErrorLog(JoinPoint joinPoint, Exception e) {

        String topic = "t1_demo_metrics";

        DataSourceErrorLog dataSourceErrorLog = new DataSourceErrorLog(
                stackTraceToString(e),
                e.getMessage(),
                joinPoint.getSignature().toString());

        producer.sendDataSourceErrorMessage(topic, dataSourceErrorLog);
    }

    private String stackTraceToString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

}
