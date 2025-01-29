CREATE EXTENSION IF NOT EXISTS "pgcrypto";

--transaction
ALTER TABLE transaction ADD COLUMN transaction_id UUID UNIQUE;
ALTER TABLE transaction ADD COLUMN status VARCHAR(20);
ALTER TABLE transaction ADD COLUMN timestamp TIMESTAMP;

UPDATE transaction SET transaction_id = gen_random_uuid() WHERE transaction_id IS NULL;
UPDATE transaction SET status = 'REQUESTED' WHERE status IS NULL;
UPDATE transaction SET timestamp = NOW() WHERE timestamp IS NULL;

ALTER TABLE transaction ALTER COLUMN transaction_id SET NOT NULL;
ALTER TABLE transaction ALTER COLUMN status SET NOT NULL;
ALTER TABLE transaction ALTER COLUMN timestamp SET NOT NULL;

--account
ALTER TABLE account ADD COLUMN account_id UUID UNIQUE;
ALTER TABLE account ADD COLUMN status VARCHAR(20);
ALTER TABLE account ADD COLUMN frozen_amount DECIMAL(19, 2) DEFAULT 0;

UPDATE account SET account_id = gen_random_uuid() WHERE account_id IS NULL;
UPDATE account SET status = 'OPEN' WHERE status IS NULL;
UPDATE account SET frozen_amount = 0 WHERE frozen_amount IS NULL;

ALTER TABLE account ALTER COLUMN account_id SET NOT NULL;
ALTER TABLE account ALTER COLUMN status SET NOT NULL;
ALTER TABLE account ALTER COLUMN frozen_amount SET NOT NULL;


--client
ALTER TABLE client ADD COLUMN client_id UUID UNIQUE;

UPDATE client SET client_id = gen_random_uuid() WHERE client_id IS NULL;

ALTER TABLE client ALTER COLUMN client_id SET NOT NULL;