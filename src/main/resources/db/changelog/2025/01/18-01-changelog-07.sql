-- liquibase formatted sql

-- changeset a_cha:1726476397331-11
CREATE SEQUENCE IF NOT EXISTS datasourceerrorlog_seq START WITH 1 INCREMENT BY 50;

-- changeset a_cha:1726476397331-12
CREATE TABLE datasourceerrorlog
(
    id BIGINT NOT NULL,
    CONSTRAINT pk_datasourceerrorlog PRIMARY KEY (id)
);
