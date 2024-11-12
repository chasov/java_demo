-- liquibase formatted sql
CREATE SEQUENCE IF NOT EXISTS errorLog_seq
    START WITH 1
    INCREMENT BY 50;

CREATE TABLE IF NOT EXISTS data_source_error_log (
    id BIGINT NOT NULL DEFAULT NEXTVAL('errorLog_seq'),
    stack_trace_text  TEXT,
    stack_trace_message VARCHAR(255),
    method_signature VARCHAR(255),
    PRIMARY KEY (id)
);
