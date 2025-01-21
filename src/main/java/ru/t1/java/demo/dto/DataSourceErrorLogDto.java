package ru.t1.java.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.t1.java.demo.model.DataSourceErrorLog;

import java.io.Serializable;

@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataSourceErrorLogDto implements Serializable {

    @JsonProperty("stack_trace")
    private String stackTrace;

    @JsonProperty("message")
    private String message;

    @JsonProperty("method_signature")
    private String methodSignature;

    public DataSourceErrorLogDto(@JsonProperty("stack_trace") String stackTrace,
                                 @JsonProperty("message") String message,
                                 @JsonProperty("method_signature") String methodSignature) {
        this.stackTrace = stackTrace;
        this.message = message;
        this.methodSignature = methodSignature;
    }
    private DataSourceErrorLogDto(){
        this.stackTrace = "";
        this.message = "";
        this.methodSignature = "";
    }

}
