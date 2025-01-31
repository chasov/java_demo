CREATE SEQUENCE IF NOT EXISTS data_source_error_log_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE data_source_error_log
(
    id BIGINT NOT NULL,
    CONSTRAINT pk_data_source_error_log PRIMARY KEY (id)
);