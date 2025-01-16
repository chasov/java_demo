package ru.t1.java.demo.DataSourceErrorLog.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Entity
@NoArgsConstructor
public class DataSourceErrorLog{

    @Id
    @GeneratedValue
    private Long id;

    @Column(columnDefinition = "text")
    private String stackTrace;

    @Column(columnDefinition = "text")
    private String message;

    @Column(columnDefinition = "text")
    private String methodSignature;

    public DataSourceErrorLog(String stackTrace, String message, String methodSignature) {
        this.stackTrace = stackTrace;
        this.message = message;
        this.methodSignature = methodSignature;
    }

}
