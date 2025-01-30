package ru.t1.java.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.t1.java.demo.model.dto.ClientDto;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "data_source_error_log")
public class DataSourceErrorLog extends ClientDto {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private UUID id;

    @Column(name = "exception_stacktrace", columnDefinition = "TEXT", nullable = false)
    private String exceptionStackTrace;

    @Column(name = "error_message", nullable = false)
    private String errorMessage;

    @Column(name = "method_signature", nullable = false)
    private String methodSignature;

    @Column(name = "log_time", nullable = false)
    private LocalDateTime logTime;
}
