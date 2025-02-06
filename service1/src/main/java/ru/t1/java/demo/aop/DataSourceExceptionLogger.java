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
public class DataSourceExceptionLogger {

    private final DataSourceErrorLogRepository errorRepository;
    private final KafkaErrorProducer kafkaErrorProducer;

    @Pointcut("@within(ru.t1.java.demo.aop.annotation.LogAfterThrowing)")
    public void annotatedClasses() {}

    @Pointcut("execution(* ru.t1.java.demo.repository.*.*(..))")
    public void repositoryMethods() {}

    // Перехватывает исключения и записывает их в лог
    @AfterThrowing(
            pointcut = "annotatedClasses()",
            throwing = "ex"
    )
    public void captureError(JoinPoint joinPoint, Exception ex) {
        String methodDetails = joinPoint.getSignature().toLongString();
        String errorTrace = Arrays.stream(ex.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"));
        String errorMessage = ex.getMessage();

        // Создаем сообщение для логирования
        String logMessage = String.format("Ошибка в методе: %s\nДетали: %s\nТрассировка стека:\n%s",
                methodDetails, errorMessage, errorTrace);
        try {
            kafkaErrorProducer.publishMessage(logMessage, Map.of("ErrorCategory", "DATABASE_FAILURE"));
            log.info("Ошибка отправлена в Kafka: ErrorCategory=DATABASE_FAILURE");
        } catch (Exception kafkaException) {
            persistError(methodDetails, errorMessage, errorTrace, kafkaException);
        }
    }

    private void persistError(String methodDetails, String errorMessage, String errorTrace, Exception kafkaEx) {
        log.error("Ошибка Kafka: {}", kafkaEx.getMessage());

        DataSourceErrorLog errorEntry = DataSourceErrorLog.builder()
                .methodSignature(methodDetails)
                .message(errorMessage)
                .stackTrace(errorTrace)
                .timestamp(LocalDateTime.now())
                .build();

        errorRepository.save(errorEntry);
        log.info("Ошибка записана в БД");
    }
}
