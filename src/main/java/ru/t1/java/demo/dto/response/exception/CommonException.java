package ru.t1.java.demo.dto.response.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import ru.t1.java.demo.dto.response.constant.Code;

@Getter
@Setter
@RequiredArgsConstructor
@Builder
public class CommonException extends RuntimeException {

    private final Code code;
    private final String userMessage;
    private final String techMessage;
    private final HttpStatus httpStatus;
}
