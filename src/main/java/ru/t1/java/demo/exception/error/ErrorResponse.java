package ru.t1.java.demo.exception.error;

import lombok.Builder;

@Builder
public record ErrorResponse(int code, String message) {
}
