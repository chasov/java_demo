-- liquibase formatted sql

CREATE TABLE client (
  id BIGINT NOT NULL,
   first_name VARCHAR,
   last_name VARCHAR,
   middle_name VARCHAR,
   CONSTRAINT pk_client PRIMARY KEY (id)
);

