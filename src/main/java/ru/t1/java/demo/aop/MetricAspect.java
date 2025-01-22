package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import ru.t1.java.demo.aop.annotation.Metric;
import ru.t1.java.demo.kafka.producer.KafkaDataSourceErrorLogProducer;
import ru.t1.java.demo.model.dto.DataSourceErrorLogDto;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class MetricAspect {

    @Value("${spring.t1.kafka.topic.error_metric}")
    private String errorMetricTopic;

    private final KafkaDataSourceErrorLogProducer<DataSourceErrorLogDto> kafkaDataSourceErrorLogProducer;

    @Around("@annotation(ru.t1.java.demo.aop.annotation.Metric)")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint, Metric metric) throws Throwable {
        long threshold = metric.threshold();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object result = joinPoint.proceed();

        stopWatch.stop();
        long elapsedTime = stopWatch.getTotalTimeMillis();

        if (elapsedTime > threshold) {
            sendMetricsToKafka(joinPoint, elapsedTime);
        }

        return result;
    }

    private void sendMetricsToKafka(JoinPoint joinPoint, long elapsedTime) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String methodName = methodSignature.getName();
        Object[] args = joinPoint.getArgs();

        StringBuilder message = new StringBuilder();
        message.append("Method: ").append(methodName)
                .append(", Time: ").append(elapsedTime).append(" ms");

        if (args.length > 0) {
            message.append(", Parameters: ");
            for (Object arg : args) {
                message.append(arg).append(" ");
            }
        }

        kafkaDataSourceErrorLogProducer.sendTo(errorMetricTopic, "METRICS", DataSourceErrorLogDto.builder()
                .message(message.toString())
                .methodSignature(methodSignature.toString())
                .build());
    }
}
