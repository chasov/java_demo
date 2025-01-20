--liquibase formatted sql

--changeset Dmitry Smirnov:1_0
--comment: Создание таблицы банковских счетов

CREATE TABLE IF NOT EXISTS accounts (
    id BIGSERIAL PRIMARY KEY NOT NULL,
    client_id BIGINT NOT NULL,
    account_type VARCHAR(20) NOT NULL,
    balance DECIMAL(19, 2) NOT NULL,
    CONSTRAINT fk_accounts_client FOREIGN KEY (client_id) REFERENCES client (id)
);