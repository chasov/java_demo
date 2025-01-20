package ru.t1.java.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;


/**
 * Логер для ошибок.
 * <p>{@link #stackTrace} - текст стек-трейса</p>
 * <p>{@link #message} - сообщение об ошибке</p>
 * <p>{@link #methodSignature} - сигнатура метода, в котором возникло исключение</p>
 */
@Entity
@Table(name = "data_source_error_logs")
@Getter
@Setter
public class DataSourceErrorLog extends AbstractPersistable<Long> {

    @NotBlank
    @Column(name = "stack_trace")
    private String stackTrace;
    @Column(name = "message")
    private String message;
    @NotBlank
    @Column(name = "method_signature")
    private String methodSignature;
}
