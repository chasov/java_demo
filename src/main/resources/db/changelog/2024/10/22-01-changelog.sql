-- liquibase formatted sql

-- changeset e_cha:1729611142826-1
ALTER TABLE client
    ADD COLUMN IF NOT EXISTS blocked_for BOOLEAN;

