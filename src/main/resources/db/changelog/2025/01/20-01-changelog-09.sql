-- liquibase formatted sql

-- changeset a_cha:1726477659739-14
ALTER TABLE datasourceerrorlog
    ALTER COLUMN method_signature SET DATA TYPE VARCHAR;