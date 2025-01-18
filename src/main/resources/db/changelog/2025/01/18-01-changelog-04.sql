-- liquibase formatted sql

-- changeset a_cha:1726477659739-5
ALTER TABLE account
    ADD client_id BIGINT;
ALTER TABLE account
    ADD account_type VARCHAR(20);
ALTER TABLE account
    ADD balance NUMERIC(15, 2);

-- changeset a_cha:1726477659739-6
ALTER TABLE account
    ADD CONSTRAINT fk_account_client
        FOREIGN KEY (client_id)
            REFERENCES client (id);