-- liquibase formatted sql

-- changeset serge_sh:31012025-6
ALTER TABLE transactions ADD transaction_id VARCHAR(255) UNIQUE NOT NULL;
ALTER TABLE transactions ADD status VARCHAR(255) NOT NULL;
ALTER TABLE transactions ADD updated_at TIMESTAMP;