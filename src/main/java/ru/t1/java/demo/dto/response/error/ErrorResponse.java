package ru.t1.java.demo.dto.response.error;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.t1.java.demo.dto.response.Response;

@Getter
@Setter
@Builder
public class ErrorResponse implements Response {
    private Error error;
}
