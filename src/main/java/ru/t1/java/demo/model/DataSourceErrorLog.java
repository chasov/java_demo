package ru.t1.java.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "data_source_error_log")
public class DataSourceErrorLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private UUID id;

    @Column(name = "text_exception_stacktrace", columnDefinition = "TEXT", nullable = false)
    private String exceptionStackTrace;

    @Column(name = "error_message",columnDefinition = "TEXT", nullable = false)
    private String errorMessage;

    @Column(name = "method_signature", columnDefinition = "TEXT",nullable = false)
    private String methodSignature;

    @Column(name = "log_time",columnDefinition = "TEXT", nullable = false)
    private LocalDateTime logTime;
}