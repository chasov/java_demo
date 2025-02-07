package ru.t1.java.demo.dto.response.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.t1.java.demo.dto.response.constant.Code;
import ru.t1.java.demo.dto.response.error.Error;
import ru.t1.java.demo.dto.response.error.ErrorResponse;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;


/**
 * Александр, к сожалению, что данная практика предпочтительней, ты напомнил поздно(я её использовал на некоторых
 * проектах по ТЗ и даже на древнем монолите прода.
 * По хорошему нужно переписать все методы слоев сервисов и контроллеров через обозначенный в коде
 * Response - не успеваю к дедлайну. Потому что надо еще и всё протестировать. Оставил в полусыром виде.
 * Просьба не ругать за это.
 * Если возможно, то доделаю для себя позже и учту на будущее как best practice.
 */
@Slf4j
@ControllerAdvice
public class CustomErrorHandler {

    @ExceptionHandler(CommonException.class)
    public ResponseEntity<ErrorResponse> handleCommonException(CommonException ex) {
        log.error("common error: {}", ex.toString());
        return new ResponseEntity<>(ErrorResponse.builder().error(Error.builder()
                .code(ex.getCode())
                .userMessage(ex.getUserMessage())
                .techMessage(ex.getTechMessage())
                .build()).build(), ex.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedErrorException(Exception ex) {
        log.error("Exception: {}", ex.toString());
        return new ResponseEntity<>(ErrorResponse.builder().error(Error.builder()
                .code(Code.INTERNAL_SERVER_ERROR)
                .userMessage("Внутренняя ошибка сервиса")
                .build()).build(), INTERNAL_SERVER_ERROR);
    }
}
