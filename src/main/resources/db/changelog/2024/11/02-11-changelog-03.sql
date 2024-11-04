-- liquibase formatted sql

-- changeset e_cha:1730708245625-1
CREATE SEQUENCE IF NOT EXISTS data_source_error_log_seq START WITH 1 INCREMENT BY 50;

-- changeset e_cha:1730708245625-2
CREATE TABLE data_source_error_log
(
    id        BIGINT NOT NULL,
    stack_trace VARCHAR(2000),
    message VARCHAR(500),
    method_signature  VARCHAR(500),
    CONSTRAINT pk_data_source_error_log PRIMARY KEY (id)
);