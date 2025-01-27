package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.ErrorLogRepository;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class LogDataSourceError {
    private final ErrorLogRepository errorLogRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @AfterThrowing(pointcut = "@annotation(WriteLogException)", throwing = "ex")
    public void logError(Throwable ex) {
        sendErrorToKafka(ex);
    }

    private void sendErrorToKafka(Throwable ex) {
        String topic = "t1_demo_metrics";
        String message = ex.getMessage();
        String exceptionStackTrace = ex.toString();
        String methodSignature = ex.getStackTrace()[0].toString();

        String sendingMessage = String.format("Error: %s%nStackTrace: %s%nMethod: %s",
                message, exceptionStackTrace, methodSignature);

        try {
            Message<String> kafkaMessage = MessageBuilder
                    .withPayload(sendingMessage)
                    .setHeader("errorType", "DATA_SOURCE")
                    .build();
            kafkaTemplate.send(topic, kafkaMessage.toString());
        } catch (Exception e) {
            log.error("Failed to send error to Kafka: {}", e.getMessage());
            saveErrorLog(ex);
        }
    }


    private void saveErrorLog(Throwable ex) {
        DataSourceErrorLog errorLog = new DataSourceErrorLog();
        errorLog.setExceptionStackTrace(ex.toString());
        errorLog.setMessage(ex.getMessage());
        errorLog.setMethodSignature(ex.getStackTrace()[0].toString());
        try {
            errorLogRepository.save(errorLog);
        } catch (DataAccessException dae) {
            log.error("Can't save error log: {}", dae.getMessage());
        } catch (Exception e) {
            log.error("Unknown error: {}", e.getMessage());
        }
    }
}
