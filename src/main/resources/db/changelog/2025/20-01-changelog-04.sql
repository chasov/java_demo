CREATE SEQUENCE IF NOT EXISTS transactional_seq START WITH 1 INCREMENT BY 50;


CREATE TABLE transactional
(
    id                  BIGINT NOT NULL DEFAULT NEXTVAL('transactional_seq'),
    price_transactional DECIMAL(18, 2) not null ,
    time_transactional  TIMESTAMP not null ,
    CONSTRAINT pk_transactional PRIMARY KEY (id)
);

