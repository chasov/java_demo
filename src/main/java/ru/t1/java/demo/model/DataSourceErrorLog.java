package ru.t1.java.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "data_source_error_log")
public class DataSourceErrorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "stack_trace", nullable = false)
    private String stackTrace;
    @Column(name = "message", nullable = false)
    private String message;
    @Column(name = "method_signature")
    private String methodSignature;

}
