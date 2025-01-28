package ru.t1.java.demo.aop.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.DataSourceErrorLogDto;
import ru.t1.java.demo.kafka.producer.KafkaErrorLogProducer;
import ru.t1.java.demo.service.DataSourceErrorLogService;

import java.io.PrintWriter;
import java.io.StringWriter;

@Component
@Aspect
@RequiredArgsConstructor
@Slf4j
public class LogDataSourceAspect {

    private final DataSourceErrorLogService errorLogService;

    private final KafkaErrorLogProducer errorLogProducer;

    @AfterThrowing(value = "@annotation(ru.t1.java.demo.aop.annotation.LogDataSourceError)"
            , throwing = "ex")
    public void LogDataSourceErrorAfterThrowing(JoinPoint jp, Exception ex) {

        DataSourceErrorLogDto errorLogDto = DataSourceErrorLogDto.builder()
                .methodSignature(jp.getSignature().toString())
                .stackTrace(formatStackTrace(ex))
                .message(ex.getMessage())
                .build();
        try {
            errorLogProducer.send(errorLogDto);
            //throw new SendMessageException("----ERRROORR----");
        } catch (Exception e) {
            log.error("Error sending message", e);
            log.info("Saving exception info");
            errorLogService.create(errorLogDto);
            log.info("ErrorLog saved successfully!");
        }
    }

    private String formatStackTrace(Exception ex) {
        StringWriter stringWriter = new StringWriter();
        ex.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }
}
