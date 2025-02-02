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
public class DataSourceErrorLog  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "exception_stacktrace")
    private String exceptionStackTrace;
    @Column(name = "error_message")
    private String errorMessage;
    @Column(name = "method_signature")
    private String methodSignature;
}
