package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.kafka.KafkaErrorProducer;
import ru.t1.java.demo.model.entity.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LogDataSourceErrorAspect {

    private final DataSourceErrorLogRepository errorLogRepository;
    private final KafkaErrorProducer kafkaErrorProducer;

    @Pointcut("@within(LogAfterThrowing)") // На уровне класса
    public void classLevelAnnotated() {}

    @Pointcut("execution(* ru.t1.java.demo.repository.*.*(..))")
    public void repositoryLevel() {}

    // Логирование при возникновении исключений
    @AfterThrowing(
            pointcut = "classLevelAnnotated() /*|| repositoryLevel()*/",
            throwing = "exception"
    ) //поставил аннотации только над классами сервисов, так как в них больше логики, а ошибки возникающие в репозитории и так до классов доходят
    public void logError(JoinPoint joinPoint, Exception exception) {

        String methodSignature = joinPoint.getSignature().toLongString();
        String stackTrace = Arrays.stream(exception.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"));
        String message = exception.getMessage();
        // Формируем сообщение для отправки
        String payload = String.format("Ошибка в методе: %s\nСообщение: %s\nСтек вызовов:\n%s",
                methodSignature, message, stackTrace);
        try {
            kafkaErrorProducer.send(payload,
                    Map.of("ErrorType", "DATA_SOURCE"));
            log.info("Сообщение успешно отправлено в топик t1_demo_metrics с заголовком ErrorType: DATA_SOURCE");
        } catch (Exception kafkaException) {
            logErrorToDatabase(methodSignature, message, stackTrace, kafkaException);
        }
    }

    private void logErrorToDatabase(String methodSignature, String message, String stackTrace, Exception kafkaException) {
        log.error("Не удалось отправить сообщение в Kafka: {}", kafkaException.getMessage());

        DataSourceErrorLog dataSourceErrorLog = DataSourceErrorLog.builder()
                .methodSignature(methodSignature)
                .message(message)
                .stackTrace(stackTrace)
                .timestamp(LocalDateTime.now())
                .build();

        errorLogRepository.save(dataSourceErrorLog);
        log.info("Ошибка сохранена в базе данных");
    }
}
