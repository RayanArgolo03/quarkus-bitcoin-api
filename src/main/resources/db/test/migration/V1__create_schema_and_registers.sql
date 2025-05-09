------------ SCHEMA -------------

-- dropping tables if existing
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS clients;

-- creating tables

CREATE TABLE IF NOT EXISTS credentials
(
    credential_id UUID PRIMARY KEY,
    email         VARCHAR(55)  NOT NULL UNIQUE,
    password      VARCHAR(100) NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT NULL
);

-- creating credentials indexes for queries
CREATE INDEX IF NOT EXISTS idx_email ON credentials (email);

CREATE TABLE IF NOT EXISTS clients
(
    credential_id UUID PRIMARY KEY REFERENCES credentials (credential_id),
    first_name    VARCHAR(55) NOT NULL,
    surname       VARCHAR(55) NOT NULL,
    birth_date    DATE        NOT NULL,
    cpf           VARCHAR(11) NOT NULL UNIQUE,
    cep           VARCHAR(8)  NOT NULL,
    state         VARCHAR(55) NOT NULL,
    street        VARCHAR(55) NOT NULL,
    neighbourhood VARCHAR(55) NOT NULL,
    house_number  VARCHAR(35) NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT NULL
);

-- creating client indexes for queries
CREATE INDEX IF NOT EXISTS idx_credential_id ON clients (credential_id);
CREATE INDEX IF NOT EXISTS idx_cpf ON clients (cpf);
-- create enum type
CREATE TYPE TYPE_ENUM AS ENUM ('PURCHASE', 'SALE');

CREATE TABLE IF NOT EXISTS transactions
(
    transaction_id UUID PRIMARY KEY,
    quantity       NUMERIC   NOT NULL,
    credential_id  UUID REFERENCES clients (credential_id),
    type           TYPE_ENUM NOT NULL,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- create transactions indexes for queries
CREATE INDEX IF NOT EXISTS idx_type ON transactions (type);
CREATE INDEX IF NOT EXISTS idx_transaction_client ON transactions (credential_id);


-------------- PERSIST ----------------

INSERT INTO credentials
VALUES ('8c878e6f-ff13-4a37-a208-7510c2638944', 'admin@gmail.com',
        '$2a$10$jkVa9r1OC6uRUhV.sRwGEOMuw4nJQ/doFCB1xM.P8KXYMEd4INCZW');
INSERT INTO clients
VALUES ('8c878e6f-ff13-4a37-a208-7510c2638944', 'Rayan', 'Argolo', CURRENT_DATE, '16125142314', '12345678', 'SP',
        'Rua Admin', 'asas', '100');

INSERT INTO transactions
VALUES (GEN_RANDOM_UUID(), 0.029, '8c878e6f-ff13-4a37-a208-7510c2638944', 'PURCHASE'),
       (GEN_RANDOM_UUID(), 0.00200, '8c878e6f-ff13-4a37-a208-7510c2638944', 'SALE');

