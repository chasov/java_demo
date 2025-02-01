-- liquibase formatted sql

-- changeset a_cha:1726477659739-28
ALTER TABLE account
    ADD CONSTRAINT fk_account_client
        FOREIGN KEY (client_id)
            REFERENCES client (client_id);

-- changeset a_cha:1726477659739-29
ALTER TABLE transaction
    ADD CONSTRAINT fk_transaction_account
        FOREIGN KEY (account_id)
            REFERENCES account (account_id);

