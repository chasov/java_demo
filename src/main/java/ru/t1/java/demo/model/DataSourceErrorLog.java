package ru.t1.java.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name= "error_logs")
public class DataSourceErrorLog extends AbstractPersistable<Long> {

    @Column(name = "stack_trace")
    private String exceptionStackTrace;

    @Column(name = "message")
    private String message;

    @Column(name = "method_signature")
    private String methodSignature;
}
