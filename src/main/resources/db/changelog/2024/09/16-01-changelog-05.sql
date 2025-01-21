-- 2. Create Transaction table and sequence
CREATE TABLE IF NOT EXISTS t_transaction
(
    id               BIGINT NOT NULL,
    amount           DECIMAL(19, 2) NOT NULL,
    client_id        BIGINT         NOT NULL,
    transaction_time TIMESTAMP      NOT NULL,
    CONSTRAINT pk_t_transaction PRIMARY KEY (id)

);
CREATE SEQUENCE IF NOT EXISTS t_transaction_seq START WITH 1 INCREMENT BY 50;
