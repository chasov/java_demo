package ru.t1.java.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

/**
 * DTO for {@link ru.t1.java.demo.model.DataSourceErrorLog}
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataSourceErrorLogDto {
    private Long id;
    private String stackTrace;
    private String message;
    private String methodSignature;
}
