-- liquibase formatted sql

-- changeset a_cha:1726477659739-25
ALTER TABLE client
    ALTER COLUMN client_id SET DATA TYPE VARCHAR(36);

-- changeset a_cha:1726477659739-26
ALTER TABLE account
    ALTER COLUMN account_id SET DATA TYPE VARCHAR(36),
    ADD CONSTRAINT unique_account_id UNIQUE (account_id),
    ALTER COLUMN client_id SET DATA TYPE VARCHAR(36);

-- changeset a_cha:1726477659739-27
ALTER TABLE transaction
    ALTER COLUMN transaction_id SET DATA TYPE VARCHAR(36),
    ADD CONSTRAINT unique_transaction_id UNIQUE (transaction_id),
    ALTER COLUMN account_id SET DATA TYPE VARCHAR(36);

