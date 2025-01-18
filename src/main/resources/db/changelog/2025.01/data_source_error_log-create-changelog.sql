CREATE TABLE data_source_error_log (
  id BIGINT NOT NULL,
   stack_trace VARCHAR,
   message VARCHAR,
   method_signature VARCHAR,
   CONSTRAINT pk_data_source_error_log PRIMARY KEY (id)
);