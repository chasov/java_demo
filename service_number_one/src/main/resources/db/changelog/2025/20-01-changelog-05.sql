CREATE SEQUENCE IF NOT EXISTS data_source_error_log_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE data_source_error_log
(
    id               BIGINT         NOT NULL DEFAULT NEXTVAL('data_source_error_log_seq'),
    stack_trace      VARCHAR(16200) not null,
    message          VARCHAR(1255),
    method_signature VARCHAR(1023)  not null,
    CONSTRAINT pk_dataSourceErrorLog PRIMARY KEY (id)
);