ALTER TABLE data_source_error_log
    ADD exception_stack_trace VARCHAR;
ALTER TABLE data_source_error_log
    ADD message VARCHAR;
ALTER TABLE data_source_error_log
    ADD method_signature VARCHAR;