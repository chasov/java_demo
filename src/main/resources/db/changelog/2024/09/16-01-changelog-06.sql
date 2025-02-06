CREATE TABLE IF NOT EXISTS data_source_error_log
(
    id               BIGINT NOT NULL,
    stack_trace      TEXT NOT NULL,
    message          TEXT NOT NULL,
    method_signature TEXT NOT NULL,
    CONSTRAINT pk_data_source_error_log PRIMARY KEY (id)
);
CREATE SEQUENCE IF NOT EXISTS data_source_error_log_seq START WITH 1 INCREMENT BY 50;
