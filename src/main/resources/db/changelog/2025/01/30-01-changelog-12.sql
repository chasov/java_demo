-- liquibase formatted sql

-- changeset a_cha:1726476397331-18
ALTER TABLE account
    ADD frozen_amount BIGINT;

-- changeset a_cha:1726476397331-19
ALTER TABLE account
    ADD state VARCHAR(20);

-- changeset a_cha:1726476397331-20
ALTER TABLE transaction
    ADD state VARCHAR(20);


