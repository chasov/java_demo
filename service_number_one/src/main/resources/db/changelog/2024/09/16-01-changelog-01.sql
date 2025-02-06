CREATE SEQUENCE IF NOT EXISTS client_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE client
(
    client_id   BIGINT       NOT NULL DEFAULT NEXTVAL('client_seq'),
    first_name  VARCHAR(255) NOT NULL,
    last_name   VARCHAR(255) NOT NULL,
    middle_name VARCHAR(255),
    CONSTRAINT pk_client PRIMARY KEY (client_id)
);

