-- liquibase formatted sql

-- changeset a_cha:1726477659739-21
ALTER TABLE client
    ADD CONSTRAINT unique_client_id UNIQUE (client_id);

-- changeset a_cha:1726477659739-22
ALTER TABLE account

    DROP CONSTRAINT fk_account_client,

    ADD CONSTRAINT fk_account_client
        FOREIGN KEY (client_id)
            REFERENCES client (client_id);