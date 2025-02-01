-- liquibase formatted sql

-- changeset serge_sh:31012025-03

ALTER TABLE accounts ADD account_id VARCHAR(255) UNIQUE;
ALTER TABLE accounts ADD status VARCHAR(255) NOT NULL DEFAULT 'OPEN';
ALTER TABLE accounts ADD frozen_amount BIGINT NULL DEFAULT 0;