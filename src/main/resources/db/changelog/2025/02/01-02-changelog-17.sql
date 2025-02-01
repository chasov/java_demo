-- liquibase formatted sql

-- changeset a_cha:1726477659739-30
ALTER TABLE account
    DROP CONSTRAINT fk_account_client;

-- changeset a_cha:1726477659739-31
ALTER TABLE transaction
    DROP CONSTRAINT fk_transaction_account;

-- changeset a_cha:1726477659739-32
ALTER TABLE client
    ALTER COLUMN client_id TYPE UUID USING client_id::UUID;

-- changeset a_cha:1726477659739-33
ALTER TABLE account
    ALTER COLUMN account_id TYPE UUID USING account_id::UUID,
    ALTER COLUMN client_id TYPE UUID USING client_id::UUID;

-- changeset a_cha:1726477659739-34
ALTER TABLE transaction
    ALTER COLUMN transaction_id TYPE UUID USING transaction_id::UUID,
    ALTER COLUMN account_id TYPE UUID USING account_id::UUID;


