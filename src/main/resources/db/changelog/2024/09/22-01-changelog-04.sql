-- liquibase formatted sql

ALTER TABLE account
    ADD client_id BIGINT;
ALTER TABLE account
    ADD account_type VARCHAR check (account_type in ('DEBIT', 'CREDIT'));
ALTER TABLE account
    ADD balance DECIMAL;
