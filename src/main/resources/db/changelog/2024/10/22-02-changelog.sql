-- liquibase formatted sql

-- changeset e_cha:1729611752509-1
ALTER TABLE client
    ADD COLUMN IF NOT EXISTS blocked_whom VARCHAR(255);

