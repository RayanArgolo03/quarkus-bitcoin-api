-- dropping tables in init if existing
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS clients;

-- creating tables

CREATE TABLE clients
(
    client_id   UUID PRIMARY KEY,
    first_name  VARCHAR(55) NOT NULL,
    surname     VARCHAR(55) NOT NULL,
    birth_date  DATE NOT NULL,
    cpf         VARCHAR(11) NOT NULL,
    email       VARCHAR(55) NOT NULL,
    cep         VARCHAR(8)  NOT NULL,
    state       VARCHAR(55) NOT NULL,
    street      VARCHAR(55) NOT NULL,
    user_name   VARCHAR(55) NOT NULL,
    password    VARCHAR(55) NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT NULL
);

-- create enum type
CREATE TYPE type_enum AS ENUM ('PURCHASE', 'SALE');

CREATE TABLE transactions
(
    transaction_id UUID PRIMARY KEY,
    quantity       NUMERIC   NOT NULL,
    client_id      UUID REFERENCES clients (client_id),
    type           type_enum NOT NULL,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- create transactions indexes for queries
CREATE INDEX IF NOT EXISTS idx_type ON transactions (type);
CREATE INDEX IF NOT EXISTS idx_client ON transactions (client_id);

-- mocking clients

INSERT INTO clients
VALUES ('8c878e6f-ee13-4a37-a208-7510c2638944', 'Re', 'Ju', CURRENT_DATE, '72651462871', 'rayan@gmail', '21820200', 'Fri', 'Fri', 'fri', '1234');

INSERT INTO clients
VALUES (GEN_RANDOM_UUID(), 'Re', 'Ju', CURRENT_DATE, '72651462871', 'rayan@gmail', '21820200', 'Fri', 'Fri', 'fri', '1234');

-- mocking transactions

INSERT INTO transactions
VALUES (GEN_RANDOM_UUID(), 0.00001, '8c878e6f-ee13-4a37-a208-7510c2638944', 'PURCHASE');

