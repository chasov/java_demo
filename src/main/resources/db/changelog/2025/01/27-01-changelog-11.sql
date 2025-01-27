-- liquibase formatted sql

-- changeset a_cha:1726476397331-16
ALTER TABLE account
    ADD account_id BIGINT;

-- changeset a_cha:1726476397331-17
ALTER TABLE transaction
    ADD transaction_id BIGINT;


