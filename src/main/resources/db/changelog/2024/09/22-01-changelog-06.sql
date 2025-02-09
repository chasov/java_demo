-- liquibase formatted sql

ALTER TABLE transactions
    ADD account_id BIGINT;
ALTER TABLE transactions
    ADD amount DECIMAL;
ALTER TABLE transactions
    ADD date TIMESTAMP;
