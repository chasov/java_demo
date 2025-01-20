--liquibase formatted sql

--changeset Dmitry Smirnov:2_0
--comment: Создание таблицы транзакций

CREATE TABLE IF NOT EXISTS transactions (
    id BIGSERIAL PRIMARY KEY NOT NULL,
    account_id BIGINT NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    time TIMESTAMP NOT NULL,
    CONSTRAINT fk_transaction_account FOREIGN KEY (account_id) REFERENCES accounts (id)
);