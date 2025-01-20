package ru.t1.java.demo.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@Table(name = "data_source_error_log")
public class DataSourceErrorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String stackTrace;

    @Column(length = 500)
    private String message;

    @Column()
    private String methodSignature;

    private LocalDateTime timestamp;

    public DataSourceErrorLog() {
        this.timestamp = LocalDateTime.now();
    }
}
