package ru.t1.java.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "data_source_error_log")
public class DataSourceErrorLog extends AbstractPersistable<Long> {

    @Column(name = "stack_trace", columnDefinition = "TEXT")
    String stackTrace;

    @Column(name = "message", nullable = false)
    String message;

    @Column(name = "method_signature", nullable = false)
    String methodSignature;
}