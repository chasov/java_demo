CREATE SEQUENCE IF NOT EXISTS transactional_seq START WITH 1 INCREMENT BY 50;


CREATE TABLE transactional
(
    transactional_id                   BIGINT         NOT NULL DEFAULT NEXTVAL('transactional_seq'),
    account_id           BIGINT         NOT NULL,
    price_transactional  DECIMAL(18, 2) NOT NULL,
    transactional_status VARCHAR(32)    NOT NULL,
    time_transactional   BIGINT         NOT NULL,
    timestamp            TIMESTAMP      NOT NULL,
    CONSTRAINT pk_transactional PRIMARY KEY (transactional_id),
    CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES account (account_id)
);