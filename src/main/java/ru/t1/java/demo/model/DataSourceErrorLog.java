package ru.t1.java.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "data_source_error_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DataSourceErrorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "stack_trace", nullable = false)
    private String stackTrace;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "method_signature", nullable = false)
    private String methodSignature;
}
