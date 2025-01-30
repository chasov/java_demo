-- liquibase formatted sql

-- changeset e_cha:1729611752509-1
ALTER TABLE client
    ADD COLUMN IF NOT EXISTS blocked_whom VARCHAR(255);

CREATE SEQUENCE IF NOT EXISTS client_seq START WITH 1 INCREMENT BY 50;


