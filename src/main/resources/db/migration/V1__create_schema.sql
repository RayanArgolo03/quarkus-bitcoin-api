
-- dropping tables in init if existing
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS clients;

-- creating tables

CREATE TABLE clients
(
    client_id          UUID PRIMARY KEY,
    first_name  VARCHAR(55) NOT NULL,
    surname     VARCHAR(55) NOT NULL,
    cpf         VARCHAR(11) NOT NULL,
    cep         VARCHAR(8)  NOT NULL,
    state       VARCHAR(55) NOT NULL,
    street      VARCHAR(55) NOT NULL,
    user_name   VARCHAR(55) NOT NULL,
    password    VARCHAR(55) NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_update TIMESTAMP DEFAULT NULL
);

CREATE TABLE transactions
(
    transaction_id         UUID PRIMARY KEY,
    quantity   FLOAT(10) NOT NULL,
    client_id  UUID REFERENCES clients (client_id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- mocking clients

INSERT INTO clients
VALUES ('8c878e6f-ee13-4a37-a208-7510c2638944', 'Re', 'Ju', '72651462871', '21820200', 'Fri', 'Fri', 'fri', '1234');

INSERT INTO clients
VALUES (GEN_RANDOM_UUID(), 'Ja', 'pe', '72651432871', '21840200', 'Fa', 'Fe', 'jui', '1234');

-- mocking transactions

INSERT INTO transactions
VALUES (GEN_RANDOM_UUID(), 0.00001, '8c878e6f-ee13-4a37-a208-7510c2638944')

