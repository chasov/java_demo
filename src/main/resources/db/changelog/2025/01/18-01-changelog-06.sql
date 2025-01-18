-- liquibase formatted sql

-- changeset a_cha:1726477659739-9
ALTER TABLE transaction
    ADD account_id BIGINT;
ALTER TABLE transaction
    ADD amount NUMERIC(15, 2);
ALTER TABLE transaction
    ADD timestamp TIMESTAMP;

-- changeset a_cha:1726477659739-10
ALTER TABLE transaction
    ADD CONSTRAINT fk_transaction_account
        FOREIGN KEY (account_id)
            REFERENCES account (id);