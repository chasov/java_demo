-- liquibase formatted sql

-- changeset serge_sh:31012025-02

ALTER TABLE clients ADD client_id VARCHAR(255) UNIQUE;