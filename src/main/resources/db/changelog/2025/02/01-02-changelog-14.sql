-- liquibase formatted sql

-- changeset a_cha:1726477659739-23
ALTER TABLE account
    DROP CONSTRAINT fk_account_client;

-- changeset a_cha:1726477659739-24
ALTER TABLE transaction
    DROP CONSTRAINT fk_transaction_account;


